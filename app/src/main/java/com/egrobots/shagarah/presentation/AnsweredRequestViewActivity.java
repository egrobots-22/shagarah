package com.egrobots.shagarah.presentation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.Request;
import com.egrobots.shagarah.presentation.helpers.ViewModelProviderFactory;
import com.egrobots.shagarah.presentation.viewmodels.SelectedRequestViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

public class AnsweredRequestViewActivity extends DaggerAppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    private LatLng imageLocation;
    private SelectedRequestViewModel selectedRequestViewModel;

    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.tree_type_value_text_view)
    TextView treeTypeTextView;
    @BindView(R.id.tree_code_value_text_view)
    TextView treeCodeTextView;
    @BindView(R.id.tree_status_value_text_view)
    TextView treeStatusTextView;
    @BindView(R.id.diseases_value_text_view)
    TextView diseasesTextView;
    @BindView(R.id.tasmed_value_text_view)
    TextView tasmedTextView;
    @BindView(R.id.alray_value_text_view)
    TextView alrayTextView;
    @BindView(R.id.operations_value_text_view)
    TextView operationsTextView;
    @BindView(R.id.althemar_value_text_view)
    TextView althemarTextView;
    @Inject
    ViewModelProviderFactory providerFactory;
    private boolean mapLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answered_request_view);
        ButterKnife.bind(this);
        setTitle(getString(R.string.tree_analysis_request_title));

        String requestId = getIntent().getStringExtra("request_id");
        String requestUserId = getIntent().getStringExtra("request_user_id");

        selectedRequestViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(SelectedRequestViewModel.class);
        selectedRequestViewModel.getRequest(requestId, requestUserId);
        observeRequest(savedInstanceState);
        observeError();
    }

    private void observeRequest(Bundle savedInstanceState) {
        selectedRequestViewModel.observeRequest().observe(this, request -> {
            //set location
            double latitude = request.getImages().get(0).getLatitude();
            double longitude = request.getImages().get(0).getLongitude();
            imageLocation = new LatLng(latitude, longitude);
            if (allPermissionsGranted()) {
                mapView.getMapAsync(AnsweredRequestViewActivity.this);
                mapView.onCreate(savedInstanceState);
                mapView.onResume();
            } else {
                ActivityCompat.requestPermissions(AnsweredRequestViewActivity.this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
            //set question answers
            treeTypeTextView.setText(request.getQuestionAnalysis().getTreeType());
            treeCodeTextView.setText(request.getQuestionAnalysis().getTreeCode());
            treeStatusTextView.setText(request.getQuestionAnalysis().getTreeStatus());
            diseasesTextView.setText(request.getQuestionAnalysis().getDiseases());
            tasmedTextView.setText(request.getQuestionAnalysis().getTasmeed());
            alrayTextView.setText(request.getQuestionAnalysis().getAlrai());
            operationsTextView.setText(request.getQuestionAnalysis().getOperations());
            althemarTextView.setText(request.getQuestionAnalysis().getElthemar());
        });
    }

    private void observeError() {
        selectedRequestViewModel.observeError().observe(this, error -> {
            Toast.makeText(AnsweredRequestViewActivity.this, error, Toast.LENGTH_SHORT).show();
        });
    }

    private boolean allPermissionsGranted() {
        if (REQUIRED_PERMISSIONS != null) {
            for (String permission : REQUIRED_PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                mapView.getMapAsync(this);
            } else {
                Toast.makeText(this, "Permissions not granted by the user..", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapLoaded = true;
        googleMap.addMarker(new MarkerOptions()
                .position(imageLocation)
                .title("Marker in Sydney"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(imageLocation,15));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapLoaded) mapView.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mapLoaded) mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapLoaded) mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapLoaded) mapView.onLowMemory();
    }
}