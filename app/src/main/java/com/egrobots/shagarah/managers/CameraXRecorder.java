package com.egrobots.shagarah.managers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Rational;
import android.util.Size;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.ViewPort;
import androidx.camera.core.impl.ImageAnalysisConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.PendingRecording;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.lifecycle.LifecycleOwner;


public class CameraXRecorder {

    private Context context;
    private PreviewView previewView;
    private VideoCapture<Recorder> videoCapture;
    private Recording recording;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private CameraXCallback cameraXCallback;
    private ProcessCameraProvider cameraProvider;
    private ImageCapture imageCapture;
    private boolean recordingAudio;

    public CameraXRecorder(Context context, PreviewView previewView, CameraXCallback cameraXCallback) {
        this.context = context;
        this.previewView = previewView;
        this.cameraXCallback = cameraXCallback;
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(context);
    }

    public void setupCameraX() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                startCameraX();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutor());
    }

    @SuppressLint("RestrictedApi")
    private void startCameraX() {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Video capture use case
        Recorder recorder = new Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build();

        videoCapture = VideoCapture.withOutput(recorder);
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setMaxResolution(new Size(1080, 1920))
                .build();

        Rational rational = new Rational(previewView.getWidth(), previewView.getHeight());
        ViewPort viewPort = new ViewPort.Builder(rational, previewView.getDisplay().getRotation())
                .setScaleType(ViewPort.FILL_CENTER)
                .build();
        UseCaseGroup.Builder useCaseGroupBuilder = new UseCaseGroup.Builder().setViewPort(viewPort);

        useCaseGroupBuilder.addUseCase(preview);
        useCaseGroupBuilder.addUseCase(imageCapture);

        //bind to lifecycle:
        cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, useCaseGroupBuilder.build());
    }

    public void captureImage() {
        if (imageCapture != null) {
            long timestamp = System.currentTimeMillis();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraXImages");
            }
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                    context.getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues).build();
            imageCapture.takePicture(outputFileOptions,
                    getExecutor(),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            Uri imageUri = outputFileResults.getSavedUri();
                            recordingAudio = true;
//                            cameraXCallback.onCaptureImage(imageUri, true);

                            InputImage image;
                            try {
                                image = InputImage.fromFilePath(context, imageUri);
                                ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
                                labeler.process(image)
                                        .addOnSuccessListener(labels -> {
                                            boolean isPlanet = false;
                                            for (ImageLabel label : labels) {
                                                String text = label.getText();
                                                float confidence = label.getConfidence();
                                                int index = label.getIndex();
                                                if (text.contains("Plant")) {
                                                    isPlanet = true;
                                                    break;
                                                }
                                            }
                                            cameraXCallback.onCaptureImage(imageUri, isPlanet);
                                        })
                                        .addOnFailureListener(e -> {
                                            cameraXCallback.onCaptureImage(null, false);
                                        });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            Log.i("Image Capture", "onError: " + exception);
                        }
                    });
        }
    }

    public void recordVideo() {
        if (videoCapture != null) {
            cameraXCallback.onPreparingRecording();
            Recording curRecording = recording;
            if (curRecording != null) {
                curRecording.stop();
                recording = null;
                cameraProvider.unbindAll();
                return;
            }

            long timestamp = System.currentTimeMillis();

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video");
            }

            MediaStoreOutputOptions mediaStoreOutputOptions = new MediaStoreOutputOptions
                    .Builder(context.getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    .setContentValues(contentValues)
                    .build();

            PendingRecording pendingRecording = videoCapture.getOutput().prepareRecording(context, mediaStoreOutputOptions);
            if (PermissionChecker.checkSelfPermission
                    (context, Manifest.permission.RECORD_AUDIO) == PermissionChecker.PERMISSION_GRANTED) {
                pendingRecording.withAudioEnabled();
            }

            recording = pendingRecording.start(getExecutor(), videoRecordEvent -> {
                if (videoRecordEvent instanceof VideoRecordEvent.Start) {
                    cameraXCallback.onStartRecording();
                } else if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                    if (!((VideoRecordEvent.Finalize) videoRecordEvent).hasError()) {
                        Uri videoUri = ((VideoRecordEvent.Finalize) videoRecordEvent).getOutputResults().getOutputUri();
                        cameraXCallback.onStopRecording(videoUri);
                    } else {
                        if (recording != null) {
                            recording.close();
                            recording = null;
                        }
                        cameraXCallback.onError("Error");
                    }
                }
            });
        }
    }

    public void releaseCameraProvider() {
        cameraProvider.unbindAll();
    }

    public void stopRecording() {
        recordVideo();
    }

    public void cancelVideoRecording() {
        recording = null;
        cameraProvider.unbindAll();
    }

    public interface CameraXCallback {
        void onPreparingRecording();

        void onStartRecording();

        void onStopRecording(Uri videoUri);

        void onError(String error);

        void onCaptureImage(Uri imageUri, boolean isPlanet);

        void onStartRecordingAudio();

        void onStopRecordingAudio();
    }
}
