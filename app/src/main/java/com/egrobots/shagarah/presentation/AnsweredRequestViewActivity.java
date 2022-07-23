package com.egrobots.shagarah.presentation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.NDVI;
import com.egrobots.shagarah.data.models.NDWI;
import com.egrobots.shagarah.presentation.helpers.ViewModelProviderFactory;
import com.egrobots.shagarah.presentation.viewmodels.SelectedRequestViewModel;
import com.egrobots.shagarah.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import androidx.annotation.NonNull;
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
    @BindView(R.id.nvdi_index_image_view)
    ImageView ndviImageView;
    @BindView(R.id.ndwi_index_image_view)
    ImageView ndwiImageView;
    @BindView(R.id.temp_text_view)
    TextView tempTextView;
    @BindView(R.id.humidity_text_view)
    TextView humidityTextView;
    @BindView(R.id.tree_type_value_text_view)
    TextView treeTypeTextView;
    @BindView(R.id.tree_code_value_text_view)
    TextView treeCodeTextView;
    @BindView(R.id.tree_category_value_text_view)
    TextView treeCategoryTextView;

    @BindView(R.id.afat_value_text_view)
    TextView afatTextView;
    @BindView(R.id.amrad_3odwia_value_text_view)
    TextView amrad3odwiaTextView;
    @BindView(R.id.amrad_bikteria_value_text_view)
    TextView amradBikteriaTextView;
    @BindView(R.id.amrad_fetrya_value_text_view)
    TextView amradFetryaTextView;
    @BindView(R.id.amrad_viruses_value_text_view)
    TextView amradVirusesTextView;

    @BindView(R.id.tasmed_value_text_view)
    TextView tasmedTextView;
    @BindView(R.id.alray_value_text_view)
    TextView alrayTextView;
    @BindView(R.id.operations_value_text_view)
    TextView operationsTextView;
    @BindView(R.id.althemar_value_text_view)
    TextView althemarTextView;
    @BindView(R.id.question_answer_value_text_view)
    TextView questionAnswerTextView;
    @BindView(R.id.mean_value_status_text_view)
    TextView ndviMeanValueStatusTextView;
    @BindView(R.id.mean_value_desc_text_view)
    TextView ndviMeanValueDescTextView;
    @BindView(R.id.point_value_status_text_view)
    TextView ndviPointValueStatusTextView;
    @BindView(R.id.point_value_desc_text_view)
    TextView ndviPointValueDescTextView;
    @BindView(R.id.ndwi_mean_value_status_text_view)
    TextView ndwiMeanValueStatusTextView;
    @BindView(R.id.ndwi_point_value_status_text_view)
    TextView ndwiPointValueStatusTextView;
    @Inject
    ViewModelProviderFactory providerFactory;
    private boolean mapLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answered_request_view);
        ButterKnife.bind(this);
        setTitle(getString(R.string.tree_analysis_request_title));

        String requestId = getIntent().getStringExtra(Constants.REQUEST_ID);
        String requestUserId = getIntent().getStringExtra(Constants.REQUEST_USER_ID);
        boolean isAdmin = getIntent().getBooleanExtra(Constants.IS_ADMIN, false);
        selectedRequestViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(SelectedRequestViewModel.class);
        selectedRequestViewModel.getRequest(requestId);
        observeRequest(savedInstanceState);
        observeError();
    }

    private void getNDVIindex(double latitude, double longitude) {
        String myUrl = "http://34.66.122.93/ndvi?lon="+longitude+"&lat=" + latitude;
        StringRequest myRequest = new StringRequest(Request.Method.GET, myUrl,
                response -> {
                    try{
                        //Create a JSON object containing information from the API.
                         JSONObject myJsonObject = new JSONObject(response);
                        String NDVIImgUrl = myJsonObject.get("ndvi_image_url").toString();
                        String meanValue = myJsonObject.get("ndvi_mean_value").toString();
                        String pointValue = myJsonObject.get("ndvi_point_value").toString();

                        NDVI meanNDVI = new NDVI(Double.parseDouble(meanValue));
                        ndviMeanValueStatusTextView.setText(meanNDVI.getStatus());
                        ndviMeanValueDescTextView.setText(meanNDVI.getDesc());

                        NDVI pointNDVI = new NDVI(Double.parseDouble(pointValue));
                        ndviPointValueStatusTextView.setText(pointNDVI.getStatus());
                        ndviPointValueDescTextView.setText(pointNDVI.getDesc());

                        Glide.with(AnsweredRequestViewActivity.this)
                                .load(NDVIImgUrl)
                                .placeholder(R.drawable.shagarah_logo)
                                .into(ndviImageView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AnsweredRequestViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                volleyError -> {
                    Toast.makeText(AnsweredRequestViewActivity.this, volleyError.getMessage()!=null ? volleyError.getMessage() : "Can't connect to ther server", Toast.LENGTH_SHORT).show();
                    Glide.with(AnsweredRequestViewActivity.this)
                            .load("")
                            .placeholder(R.drawable.shagarah_logo)
                            .into(ndviImageView);
                }
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(myRequest);

    }

    private void getNDWIindex(double latitude, double longitude) {
        String myUrl = "http://34.66.122.93/ndwi?lon="+longitude+"&lat=" + latitude;
        StringRequest myRequest = new StringRequest(Request.Method.GET, myUrl,
                response -> {
                    try{
                        //Create a JSON object containing information from the API.
                        JSONObject myJsonObject = new JSONObject(response);
                        String NDWIImgUrl = myJsonObject.get("ndwi_image_url").toString();
                        String meanValue = myJsonObject.get("ndwi_mean_value").toString();
                        String pointValue = myJsonObject.get("ndwi_point_value").toString();

                        NDWI meanNDWI = new NDWI(Double.parseDouble(meanValue));
                        ndwiMeanValueStatusTextView.setText(meanNDWI.getStatus());

                        NDWI pointNDWI = new NDWI(Double.parseDouble(pointValue));
                        ndwiPointValueStatusTextView.setText(pointNDWI.getStatus());

                        Glide.with(AnsweredRequestViewActivity.this)
                                .load(NDWIImgUrl)
                                .placeholder(R.drawable.shagarah_logo)
                                .into(ndwiImageView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AnsweredRequestViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                volleyError -> {
                    Toast.makeText(AnsweredRequestViewActivity.this, volleyError.getMessage()!=null ? volleyError.getMessage() : "Can't connect to ther server", Toast.LENGTH_SHORT).show();
                    Glide.with(AnsweredRequestViewActivity.this)
                            .load("")
                            .placeholder(R.drawable.shagarah_logo)
                            .into(ndviImageView);
                }
        );
        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(myRequest);

    }

    private void getWeather(double latitude, double longitude) {
        String myUrl = "http://34.66.122.93/weather?lon="+longitude+"&lat=" + latitude;
        StringRequest myRequest = new StringRequest(Request.Method.GET, myUrl,
                response -> {
                    try{
                        //Create a JSON object containing information from the API.
                        JSONObject myJsonObject = new JSONObject(response);
                        JSONObject mainJsonObject = myJsonObject.getJSONObject("main");
                        String temp = mainJsonObject.getString("temp");
                        String humidity = mainJsonObject.getString("humidity");

                        tempTextView.setText("درجة الحرارة : " + temp);
                        humidityTextView.setText("الرطوبة : " + humidity);
                        Log.i("Volley Response", "get weather: " + response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AnsweredRequestViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                volleyError -> {
                    Toast.makeText(AnsweredRequestViewActivity.this, volleyError.getMessage()!=null ? volleyError.getMessage() : "Can't connect to ther server", Toast.LENGTH_SHORT).show();
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(myRequest);
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
            getNDVIindex(latitude, longitude);
            getWeather(latitude, longitude);
            getNDWIindex(latitude, longitude);
            //set question answers
            treeTypeTextView.setText(request.getQuestionAnalysis().getTreeType());
            treeCodeTextView.setText(request.getQuestionAnalysis().getTreeCode());
            treeCategoryTextView.setText(request.getQuestionAnalysis().getTreeCategory());

            afatTextView.setText(request.getQuestionAnalysis().getAlafatAsString());
            amrad3odwiaTextView.setText(request.getQuestionAnalysis().getAmarad3odwiaAsString());
            amradBikteriaTextView.setText(request.getQuestionAnalysis().getAmradBikteriaAsString());
            amradFetryaTextView.setText(request.getQuestionAnalysis().getAmradFetryaAsString());
            amradVirusesTextView.setText(request.getQuestionAnalysis().getAmradVirusesAsString());


            tasmedTextView.setText(request.getQuestionAnalysis().getTasmeed());
            alrayTextView.setText(request.getQuestionAnalysis().getAlrai());
            operationsTextView.setText(request.getQuestionAnalysis().getOperations());
            althemarTextView.setText(request.getQuestionAnalysis().getElthemar());
            questionAnswerTextView.setText(request.getQuestionAnalysis().getQuestionAnswer());
//            requestAnswerRating.setRating(request.getQuestionAnalysis().getRating());
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
                .title("موقع الشجرة"));
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.addCircle(new CircleOptions()
                .center(imageLocation)
                .radius(1)
                .strokeColor(Color.RED));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(imageLocation,15));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);
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