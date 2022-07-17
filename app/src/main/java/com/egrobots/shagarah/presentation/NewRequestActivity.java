package com.egrobots.shagarah.presentation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.Image;
import com.egrobots.shagarah.managers.AudioRecorder;
import com.egrobots.shagarah.managers.CameraXRecorder;
import com.egrobots.shagarah.managers.ExoPlayerVideoManager;
import com.egrobots.shagarah.presentation.helpers.GuideDialog;
import com.egrobots.shagarah.presentation.helpers.ViewModelProviderFactory;
import com.egrobots.shagarah.presentation.viewmodels.NewRequestViewModel;
import com.egrobots.shagarah.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.ui.PlayerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class NewRequestActivity extends DaggerAppCompatActivity implements GuideDialog.GuidelineCallback
        , CameraXRecorder.CameraXCallback, LocationListener {

    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA
            , Manifest.permission.RECORD_AUDIO
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE
            , ACCESS_FINE_LOCATION};

    private GuideDialog guideDialog = new GuideDialog(this);

    @BindView(R.id.viewFinder)
    PreviewView previewView;
    @BindView(R.id.videoView)
    PlayerView playerView;
    @BindView(R.id.capture_button)
    ImageButton captureButton;
    @BindView(R.id.recorded_time_tv)
    TextView recordedSecondsTV;
    @BindView(R.id.review_video_view)
    View reviewView;
    @BindView(R.id.multiple_images_view)
    View multipleImagesView;
    @BindView(R.id.image_switcher)
    ImageSwitcher imageSwitcher;
    @BindView(R.id.prevImageButton)
    ImageButton prevImageButton;
    @BindView(R.id.nextImageButton)
    ImageButton nextImageButton;
    @BindView(R.id.add_image_button)
    ImageButton addImageButton;
    @BindView(R.id.delete_image_button)
    ImageButton deleteImageButton;
    @BindView(R.id.add_question_view)
    View addQuestionView;
    @BindView(R.id.text_question_edit_text)
    EditText textQuestionEditText;
    @BindView(R.id.record_audio_button)
    ImageButton recordAudioButton;
    @BindView(R.id.image_num_text_view)
    TextView imageNumTextView;

    private Uri fileUri;
    private List<Uri> imagesUris = new ArrayList<>();
    private List<Image> uploadedImagesUris = new ArrayList<>();
    private ActivityResultLauncher openGalleryLauncher;
    private CameraXRecorder cameraXRecorder;
    private boolean isAddingNewImage = true;
    private boolean isAudioRecordingStarted;
    private int selectedImagePosition;
    private File audioRecordedFile;
    private AudioRecorder audioRecorder = new AudioRecorder();
    ;
    private Runnable updateEverySecRunnable;
    private int recordedSeconds;
    private Handler handler = new Handler();
    private int uploadedImageIndex;
    private ProgressDialog progressDialog;
    private LocationManager locationManager;
    private NewRequestViewModel newRequestViewModel;
    @Inject
    ViewModelProviderFactory providerFactory;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private String questionText;
    private String userId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_request);
        ButterKnife.bind(this);
        guideDialog.show(getSupportFragmentManager(), null);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.uploading));
        registerClickListenersForPrevNextButtons();
        initializeImageSwitcher();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        token = getIntent().getStringExtra(Constants.DEVICE_TOKEN);
        newRequestViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(NewRequestViewModel.class);
        observeImageUpload();
        observeAddingRequest();
        observeError();
    }

    private void initializeCameraX() {
        // Request camera permissions
        if (allPermissionsGranted()) {
            cameraXRecorder = new CameraXRecorder(this, previewView, this);
            cameraXRecorder.setupCameraX();
        } else if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        boolean gpsEnabled = checkEnableGpsLocationAccess();
        if (!gpsEnabled) {
            //show dialog
            showGPSDisabledAlertToUser();
        }
    }

    private void initializeImageSwitcher() {
        imageSwitcher.setFactory(() -> {
            FrameLayout.LayoutParams layoutParams
                    = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            ImageView imgView = new ImageView(this);
            imgView.setLayoutParams(layoutParams);
            imgView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imgView;
        });
    }

    private void registerClickListenersForPrevNextButtons() {
        nextImageButton.setOnClickListener(v -> {
            ++selectedImagePosition;
            prevImageButton.setVisibility(View.VISIBLE);
            if (selectedImagePosition == imagesUris.size() - 1) {
                nextImageButton.setVisibility(View.GONE);
            }
            imageSwitcher.setImageURI(imagesUris.get(selectedImagePosition));
            imageNumTextView.setText(String.format("صورة رقم: %d", selectedImagePosition + 1));
        });

        prevImageButton.setOnClickListener(v -> {
            --selectedImagePosition;
            nextImageButton.setVisibility(View.VISIBLE);
            if (selectedImagePosition == 0) {
                prevImageButton.setVisibility(View.GONE);
            }
            imageSwitcher.setImageURI(imagesUris.get(selectedImagePosition));
            imageNumTextView.setText(String.format("صورة رقم: %d", selectedImagePosition + 1));
        });
    }

    private void observeImageUpload() {
        newRequestViewModel.observeUploadImage().observe(this, downloadUrl -> {
            Image img = new Image();
            double[] latlong = getLatLongImage(imagesUris.get(uploadedImageIndex));
            img.setUrl(downloadUrl);
            if (latlong != null) {
                img.setLatitude(latlong[0]);
                img.setLongitude(latlong[1]);
            }
            uploadedImagesUris.add(img);
            uploadedImageIndex++;
            if (uploadedImageIndex < imagesUris.size()) {
                Uri imageUri = imagesUris.get(uploadedImageIndex);
                newRequestViewModel.uploadImageToFirebaseStorage(imageUri);
            } else {
                //send request
                newRequestViewModel.addNewRequest(userId, token, uploadedImagesUris, audioRecordedFile, questionText);
            }
        });
    }

    private void observeAddingRequest() {
        newRequestViewModel.observeAddingRequest().observe(this, success -> {
            if (success) {
                progressDialog.dismiss();
                Toast.makeText(NewRequestActivity.this, R.string.request_added_successfully, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                progressDialog.dismiss();
                Toast.makeText(NewRequestActivity.this, R.string.unkown_error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void observeError() {
        newRequestViewModel.observeError().observe(this, error -> {
            Toast.makeText(NewRequestActivity.this, error, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onStartNowClicked() {
        initializeCameraX();
        imageNumTextView.setVisibility(View.VISIBLE);
        imageNumTextView.setText(String.format("متبقى %d صور", 4));
    }

    @OnClick(R.id.capture_button)
    public void onCaptureButtonClicked() {
        if (isAddingNewImage) {
            cameraXRecorder.captureImage();
        }
    }

    @OnClick(R.id.record_audio_button)
    public void onRecordQuestionAudioClicked() {
        if (!isAudioRecordingStarted) {
            //recording audio is not started yet, so start it
            onStartRecordingAudio();
        } else {
            onStopRecordingAudio();
        }
    }

    @OnClick(R.id.send_text_button)
    public void onSendRequestButtonClicked() {
        progressDialog.show();
        if (audioRecorder != null) {
            onStopRecordingAudio();
        }
        questionText = textQuestionEditText.getText().toString();
        //upload question as text or audio
        newRequestViewModel.uploadImageToFirebaseStorage(imagesUris.get(uploadedImageIndex));
    }

    private boolean checkEnableGpsLocationAccess() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onCaptureImage(Uri imageUri, boolean isPlanet) {
        if (isPlanet) {
            Toast.makeText(this, "الصورة تحتوي علي أشجار", Toast.LENGTH_SHORT).show();
            isAddingNewImage = false;
            previewView.setVisibility(View.GONE);
            multipleImagesView.setVisibility(View.VISIBLE);
            addImageButton.setVisibility(View.VISIBLE);
            imagesUris.add(imageUri);
            selectedImagePosition = imagesUris.size() - 1;
            imageSwitcher.setImageURI(imagesUris.get(selectedImagePosition));

            //if user capture images from 4 angles, change icon to record audio
            if (imagesUris.size() == 4) {
                //hide record button
                captureButton.setVisibility(View.GONE);
                //hide add/delete images
                addImageButton.setVisibility(View.GONE);
                deleteImageButton.setVisibility(View.GONE);
                //add question as text or voice
                addQuestionView.setVisibility(View.VISIBLE);
                imageNumTextView.setVisibility(View.GONE);
            } else {
                captureButton.setEnabled(false);
                if (imagesUris.size() > 1) {
                    deleteImageButton.setVisibility(View.VISIBLE);
                } else {
                    deleteImageButton.setVisibility(View.GONE);
                    prevImageButton.setVisibility(View.GONE);
                    return;
                }

                if (selectedImagePosition == 0) {
                    nextImageButton.setVisibility(View.VISIBLE);
                    prevImageButton.setVisibility(View.GONE);
                } else if (selectedImagePosition == imagesUris.size() - 1) {
                    nextImageButton.setVisibility(View.GONE);
                    prevImageButton.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (imageUri == null) {
                Toast.makeText(this, "لم يتم التعرف علي محتوى الصورة", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "الصورة التي تم التقاطها لا تحتوي علي أشجار", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.done_button)
    public void onDoneClicked() {
        Toast.makeText(this, "images will be uploaded", Toast.LENGTH_SHORT).show();
        progressDialog.show();
    }

    @OnClick(R.id.cancel_button)
    public void onCancelButton() {
        recordedSeconds = 0;
        recordedSecondsTV.setVisibility(View.GONE);
        //show camerax view
        previewView.setVisibility(View.VISIBLE);
        playerView.setVisibility(View.GONE);
        multipleImagesView.setVisibility(View.GONE);
        //hide review view
        reviewView.setVisibility(View.GONE);
        //show record button
        captureButton.setVisibility(View.VISIBLE);
        deleteRecordedAudio();
        fileUri = null;

        //reinitialize camera
        initializeCameraX();
    }

    private void deleteRecordedAudio() {
        if (audioRecordedFile != null && audioRecordedFile.exists()) {
            audioRecordedFile.delete();
        }
    }

    @OnClick(R.id.add_image_button)
    public void onAddImageClicked() {
        initializeCameraX();
        fileUri = null; //to capture new image
        previewView.setVisibility(View.VISIBLE);
        multipleImagesView.setVisibility(View.GONE);
        captureButton.setEnabled(true);
        captureButton.setImageDrawable(ContextCompat.getDrawable(NewRequestActivity.this, R.drawable.start_record));
        imageNumTextView.setText(String.format("متبقى %d صور", 4 - imagesUris.size()));
        isAddingNewImage = true;
    }

    @OnClick(R.id.delete_image_button)
    public void onDeleteImageClicked() {
        imagesUris.remove(selectedImagePosition);
        if (imagesUris.size() < 4) {
            addImageButton.setVisibility(View.VISIBLE);
            captureButton.setImageDrawable(ContextCompat.getDrawable(NewRequestActivity.this, R.drawable.start_record));
        }
        if (selectedImagePosition != 0) {
            --selectedImagePosition;
        }
        imageSwitcher.setImageURI(imagesUris.get(selectedImagePosition));
        if (imagesUris.size() > 1) {
            deleteImageButton.setVisibility(View.VISIBLE);
        } else if (imagesUris.size() == 1) {
            deleteImageButton.setVisibility(View.GONE);
            prevImageButton.setVisibility(View.GONE);
            nextImageButton.setVisibility(View.GONE);
            return;
        }
        if (selectedImagePosition == 0) {
            nextImageButton.setVisibility(View.VISIBLE);
            prevImageButton.setVisibility(View.GONE);
        } else if (selectedImagePosition == imagesUris.size() - 1) {
            nextImageButton.setVisibility(View.GONE);
            prevImageButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPreparingRecording() {

    }

    @Override
    public void onStartRecording() {

    }

    @Override
    public void onStopRecording(Uri videoUri) {

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onStartRecordingAudio() {
        isAudioRecordingStarted = true;
        //set button as stop recording
        recordAudioButton.setEnabled(true);
        recordAudioButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.stop_record));
        //start recording audio
        audioRecordedFile = new File(getFilesDir().getPath(), UUID.randomUUID().toString() + Constants.AUDIO_FILE_TYPE);
        try {
            audioRecorder.start(this, audioRecordedFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        recordedSecondsTV.setVisibility(View.VISIBLE);
        recordedSecondsTV.setText("00:00");
        updateEverySecRunnable = new Runnable() {
            @Override
            public void run() {
                ++recordedSeconds;
                String seconds = recordedSeconds < 10 ? "0" + recordedSeconds : recordedSeconds + "";
                recordedSecondsTV.setText(String.format("00:%s", seconds));
                //start recording question audio
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(updateEverySecRunnable, 1000);
    }

    @Override
    public void onStopRecordingAudio() {
        isAudioRecordingStarted = false;
        handler.removeCallbacks(updateEverySecRunnable);
        //stop recorded audio
        audioRecorder.stop();
        audioRecorder = null;
        recordAudioButton.setEnabled(true);
        recordAudioButton.setImageDrawable(ContextCompat.getDrawable(NewRequestActivity.this, R.drawable.recording_audio));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(updateEverySecRunnable);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacks(updateEverySecRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(updateEverySecRunnable);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cameraXRecorder = new CameraXRecorder(this, previewView, this);
                cameraXRecorder.setupCameraX();
            } else {
                Toast.makeText(this, "Permissions not granted by the user..", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && REQUIRED_PERMISSIONS != null) {
            for (String permission : REQUIRED_PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage(
                        "GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        (dialog, id) -> {
                            Intent callGPSSettingIntent = new Intent(
                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(callGPSSettingIntent);
                        });

        alertDialogBuilder.setNegativeButton("Cancel",
                (dialog, id) -> {
                    dialog.cancel();
                    finish();
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @SuppressLint("MissingPermission")
    private double[] getLatLongImage(Uri imageUri) {
        double[] latlong = new double[2];
        Criteria criteria = new Criteria();
        LocationManager locationmanager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        if (locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            if (locationManager != null) {
                Location location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    latlong[0] = location.getLatitude();
                    latlong[1] = location.getLongitude();
                }
            }
            return latlong;
        }
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

}