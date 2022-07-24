package com.egrobots.shagarah.presentation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.Image;
import com.egrobots.shagarah.data.models.RequestSurveyQuestion;
import com.egrobots.shagarah.managers.CameraXRecorder;
import com.egrobots.shagarah.presentation.adapters.ImagesAdapter;
import com.egrobots.shagarah.presentation.helpers.GuideDialog;
import com.egrobots.shagarah.presentation.helpers.ImageDialog;
import com.egrobots.shagarah.presentation.helpers.SurveyDialog;
import com.egrobots.shagarah.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class CapturingImagesActivity extends DaggerAppCompatActivity
        implements GuideDialog.GuidelineCallback, CameraXRecorder.CameraXCallback, LocationListener {

    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA
            , Manifest.permission.RECORD_AUDIO
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE
            , ACCESS_FINE_LOCATION};
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // 1 minute

    private GuideDialog guideDialog = new GuideDialog(this);
    private SurveyDialog surveyDialog = SurveyDialog.newInstance();
    private ArrayList<String> imagesUris = new ArrayList<>();
    private List<Image> images = new ArrayList<>();
    private ImagesAdapter imagesAdapter = new ImagesAdapter();
    private List<RequestSurveyQuestion> requestSurveyQuestions;
    private LocationManager locationManager;
    private CameraXRecorder cameraXRecorder;
    private int capturedImagesCount = 0;
    private int selectedImagePosition = 0;

    @BindView(R.id.previewView)
    PreviewView previewView;
    @BindView(R.id.view_images_layout)
    FrameLayout viewImagesLayout;
    @BindView(R.id.request_images_viewpager)
    ViewPager2 imagesViewPager;
    @BindView(R.id.capture_button)
    ImageButton captureButton;
    @BindView(R.id.image1)
    ImageView image1;
    @BindView(R.id.image2)
    ImageView image2;
    @BindView(R.id.image3)
    ImageView image3;
    @BindView(R.id.image4)
    ImageView image4;
    @BindView(R.id.lbl_1)
    TextView lbl1;
    @BindView(R.id.lbl_2)
    TextView lbl2;
    @BindView(R.id.lbl_3)
    TextView lbl3;
    @BindView(R.id.lbl_4)
    TextView lbl4;
    @BindView(R.id.done_button)
    ImageButton doneButton;
    @BindView(R.id.add_image_button)
    ImageButton addImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturing_images);
        ButterKnife.bind(this);
        guideDialog.show(getSupportFragmentManager(), null);
        imagesViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                selectedImagePosition = position;
            }
        });
    }

    @Override
    public void onStartNowClicked() {
        surveyDialog.show(getSupportFragmentManager(), null);
        surveyDialog.setSurveyDialogCallback(surveyQuestions -> requestSurveyQuestions = surveyQuestions);
        initializeCameraX();
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

    @OnClick(R.id.capture_button)
    public void onCaptureButtonClicked() {
        cameraXRecorder.captureImage();
    }

    @OnClick(R.id.delete_image_button)
    public void onDeleteImage() {
        int position = selectedImagePosition;
        capturedImagesCount--;
        imagesUris.remove(position);
        images.remove(position);
        imagesAdapter.notifyDataSetChanged();

        doneButton.setVisibility(View.GONE);
        addImageButton.setVisibility(View.VISIBLE);

        image1.setImageDrawable(null);
        image2.setImageDrawable(null);
        image3.setImageDrawable(null);
        image4.setImageDrawable(null);

        if (imagesUris.size() == 0) {
            viewImagesLayout.setVisibility(View.GONE);
            captureButton.setVisibility(View.VISIBLE);
            previewView.setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.GONE);
            addImageButton.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < imagesUris.size(); i++) {
                String imageUri = imagesUris.get(i);
                switch (i) {
                    case 0:
                        Glide.with(this).load(imageUri).into(image1);
                        break;
                    case 1:
                        Glide.with(this).load(imageUri).into(image2);
                        break;
                    case 2:
                        Glide.with(this).load(imageUri).into(image3);
                        break;
                    case 3:
                        Glide.with(this).load(imageUri).into(image4);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @OnClick(R.id.done_button)
    public void onDoneClicked() {
        Intent intent = new Intent(this, ReviewRequestActivity.class);
        intent.putParcelableArrayListExtra(Constants.IMAGES, (ArrayList<? extends Parcelable>) images);
        intent.putParcelableArrayListExtra(Constants.SURVEY_QUESTIONS, (ArrayList<? extends Parcelable>) requestSurveyQuestions);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0) {
            finish();
        }
    }

    @OnClick(R.id.add_image_button)
    public void onAddImageClicked() {
        viewImagesLayout.setVisibility(View.GONE);
        previewView.setVisibility(View.VISIBLE);
        captureButton.setVisibility(View.VISIBLE);
        doneButton.setVisibility(View.GONE);
        addImageButton.setVisibility(View.GONE);
        initializeCameraX();
    }

    @OnClick(R.id.image1)
    public void onImage1Clicked() {
        if (imagesAdapter.getItemCount() > 0) {
            imagesViewPager.setCurrentItem(0);
        }
    }

    @OnClick(R.id.image2)
    public void onImage2Clicked() {
        if (imagesAdapter.getItemCount() > 1) {
            imagesViewPager.setCurrentItem(1);
        }
    }

    @OnClick(R.id.image3)
    public void onImage3Clicked() {
        if (imagesAdapter.getItemCount() > 2) {
            imagesViewPager.setCurrentItem(2);
        }
    }

    @OnClick(R.id.image4)
    public void onImage4Clicked() {
        if (imagesAdapter.getItemCount() > 3) {
            imagesViewPager.setCurrentItem(3);
        }
    }

    @Override
    public void onCaptureImage(Uri imageUri, boolean isPlanet) {
        if (isPlanet) {
            capturedImagesCount++;
            switch (capturedImagesCount) {
                case 1:
                    Glide.with(this).load(imageUri).into(image1);
                    break;
                case 2:
                    Glide.with(this).load(imageUri).into(image2);
                    break;
                case 3:
                    Glide.with(this).load(imageUri).into(image3);
                    break;
                case 4:
                    Glide.with(this).load(imageUri).into(image4);
                    break;
                default:
                    break;
            }
            imagesUris.add(imageUri.toString());
            if (imagesUris.size() == 4) {
                //all required images are captured
                viewImagesLayout.setVisibility(View.VISIBLE);
                doneButton.setVisibility(View.VISIBLE);
                previewView.setVisibility(View.GONE);
                captureButton.setVisibility(View.GONE);

                imagesViewPager.setAdapter(imagesAdapter);
                for (String uri : imagesUris) {
                    Image img = new Image();
                    img.setUrl(uri);
                    Location location = getLatLongImage();
                    if (location != null) {
                        img.setLatitude(location.getLatitude());
                        img.setLongitude(location.getLongitude());
                    }
                    images.add(img);
                }
                imagesAdapter.setImages(images);
                imagesAdapter.notifyDataSetChanged();
            } else {
                initializeCameraX();
            }
        } else {
            if (imageUri == null) {
                Toast.makeText(this, "لم يتم التعرف علي محتوى الصورة", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "الصورة التي تم التقاطها لا تحتوي علي أشجار", Toast.LENGTH_SHORT).show();
            }
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

    }

    @Override
    public void onStopRecordingAudio() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (imagesUris.size() == 4) {
            viewImagesLayout.setVisibility(View.VISIBLE);
            previewView.setVisibility(View.GONE);
            captureButton.setVisibility(View.GONE);
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

    private boolean checkEnableGpsLocationAccess() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private Location getLatLongImage() {
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
                return location;
            }
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