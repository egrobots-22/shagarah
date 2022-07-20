package com.egrobots.shagarah.presentation;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.Image;
import com.egrobots.shagarah.managers.AudioPlayer;
import com.egrobots.shagarah.managers.AudioRecorder;
import com.egrobots.shagarah.presentation.adapters.ImagesAdapter;
import com.egrobots.shagarah.presentation.helpers.ViewModelProviderFactory;
import com.egrobots.shagarah.presentation.viewmodels.NewRequestViewModel;
import com.egrobots.shagarah.utils.Constants;
import com.egrobots.shagarah.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

public class ReviewRequestActivity extends DaggerAppCompatActivity {

    private static final int TIME_UNIT = 500;

    @BindView(R.id.request_images_viewpager)
    ViewPager2 imagesViewPager;
    @BindView(R.id.planet_type_value_edit_text)
    EditText planetTypeEditText;
    @BindView(R.id.planet_cat_value_edit_text)
    EditText planetCatEditText;
    @BindView(R.id.question_desc_value_edit_text)
    EditText questionDescEditText;
    @BindView(R.id.record_audio_layout)
    View recordAudioLayout;
    @BindView(R.id.record_audio_button)
    ImageButton recordAudioButton;
    @BindView(R.id.play_audio_item_layout)
    View playAudioLayout;
    @BindView(R.id.audio_record_progress_textview)
    TextView audioRecordProgress;
    @BindView(R.id.playButton)
    ImageButton playButton;
    @BindView(R.id.pauseButton)
    ImageButton pauseButton;
    @BindView(R.id.audio_progress_textview)
    TextView audioProgressTextView;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    @BindView(R.id.audio_length_textview)
    TextView audioLengthTextView;
    @Inject
    ViewModelProviderFactory providerFactory;

    private ArrayList<Image> images = new ArrayList<>();
    private List<Image> uploadedImagesUris = new ArrayList<>();
    private Handler handler = new Handler();
    private AudioRecorder audioRecorder;
    private AudioPlayer audioPlayer;
    private File audioRecordedFile;
    private Runnable updateEverySecRunnable;
    private Runnable mUpdateTimeTask;
    private NewRequestViewModel newRequestViewModel;
    private ProgressDialog progressDialog;
    private String userId;
    private String token;
    private String planetType;
    private String planetCat;
    private String problemDesc;
    private int uploadedImageIndex = 0;
    private int recordedSeconds;
    private boolean isAudioRecordingStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_request);
        setTitle("إضافة الطلب");
        ButterKnife.bind(this);
        setRequestImages();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.uploading));

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        token = getIntent().getStringExtra(Constants.DEVICE_TOKEN);
        newRequestViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(NewRequestViewModel.class);
        observeImageUpload();
        observeAddingRequest();
        observeError();
    }

    private void observeImageUpload() {
        newRequestViewModel.observeUploadImage().observe(this, downloadUrl -> {
            Image img = images.get(uploadedImageIndex);
            img.setUrl(downloadUrl);
            uploadedImagesUris.add(img);
            uploadedImageIndex++;
            if (uploadedImageIndex < images.size()) {
                Uri imageUri = Uri.parse(images.get(uploadedImageIndex).getUrl());
                newRequestViewModel.uploadImageToFirebaseStorage(imageUri);
            } else {
                //send request
                newRequestViewModel.addNewRequest(userId, token, uploadedImagesUris, audioRecordedFile, problemDesc, planetType, planetCat);
            }
        });
    }

    private void observeAddingRequest() {
        newRequestViewModel.observeAddingRequest().observe(this, success -> {
            if (success) {
                progressDialog.dismiss();
                Toast.makeText(ReviewRequestActivity.this, R.string.request_added_successfully, Toast.LENGTH_SHORT).show();
                setResult(0);
                finish();
            } else {
                progressDialog.dismiss();
                Toast.makeText(ReviewRequestActivity.this, R.string.unkown_error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void observeError() {
        newRequestViewModel.observeError().observe(this, error -> {
            Toast.makeText(ReviewRequestActivity.this, error, Toast.LENGTH_SHORT).show();
        });
    }

    private void setRequestImages() {
        images = getIntent().getParcelableArrayListExtra(Constants.IMAGES);
        ImagesAdapter imagesAdapter = new ImagesAdapter();
        imagesViewPager.setAdapter(imagesAdapter);
        if (images != null) {
            imagesAdapter.setImages(images);
            imagesAdapter.notifyDataSetChanged();
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

    public void onStartRecordingAudio() {
        isAudioRecordingStarted = true;
        //set button as stop recording
        recordAudioButton.setEnabled(true);
        recordAudioButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.stop_record));

        audioRecorder = new AudioRecorder();
        audioRecordedFile = new File(getFilesDir().getPath(), UUID.randomUUID().toString() + Constants.AUDIO_FILE_TYPE);
        try {
            audioRecorder.start(this, audioRecordedFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        audioRecordProgress.setVisibility(View.VISIBLE);
        audioRecordProgress.setText("00:00");
        updateEverySecRunnable = new Runnable() {
            @Override
            public void run() {
                ++recordedSeconds;
                String seconds = recordedSeconds < 10 ? "0" + recordedSeconds : recordedSeconds + "";
                audioRecordProgress.setText(String.format("00:%s", seconds));
                //start recording question audio
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(updateEverySecRunnable, 1000);
    }

    public void onStopRecordingAudio() {
        isAudioRecordingStarted = false;
        handler.removeCallbacks(updateEverySecRunnable);
        //stop recorded audio
        audioRecorder.stop();
        audioRecorder = null;
        recordAudioButton.setEnabled(true);
        recordAudioButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.recording_audio));

        recordAudioLayout.setVisibility(View.GONE);
        playAudioLayout.setVisibility(View.VISIBLE);
        setAudioPlayer(audioRecordedFile.getPath());
    }

    private void setAudioPlayer(String audioQuestion) {
        audioPlayer = new AudioPlayer();
        audioPlayer.setAudio(audioQuestion, new AudioPlayer.AudioPlayCallback() {
            @Override
            public void onFinishPlayingAnswerAudio() {
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
                seekBar.setProgress(0);
                handler.removeCallbacks(mUpdateTimeTask);
                audioProgressTextView.setText("00:00");
            }

            @Override
            public void onStartPlayingAnswerAudio(AudioPlayer audioPlayer) {
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
                audioProgressTextView.setText(Utils.formatMilliSeconds(audioPlayer.getCurrentPosition()));
                updateSeekBar();
            }
        });
        mUpdateTimeTask = new Runnable() {
            @Override
            public void run() {
                if (audioPlayer != null) {
                    audioProgressTextView.setText(Utils.formatMilliSeconds(audioPlayer.getCurrentPosition()));
                    seekBar.setProgress(audioPlayer.getCurrentPosition() / TIME_UNIT);
                    handler.postDelayed(this, TIME_UNIT);
                }
            }
        };
        try {
            int audioDuration = Integer.parseInt(audioPlayer.getAudioDuration());
            audioLengthTextView.setText(Utils.formatMilliSeconds(audioDuration));
            seekBar.setMax(audioDuration / TIME_UNIT);
        } catch (Exception exception) {
            Toast.makeText(this, "خطأ في تحميل السؤال الصوتي", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSeekBar() {
        handler.postDelayed(mUpdateTimeTask, TIME_UNIT);
    }

    @OnClick(R.id.playButton)
    public void onPlayButton() {
        if (audioPlayer != null) {
            audioPlayer.playAudio();
        }
    }

    @OnClick(R.id.pauseButton)
    public void onPauseClicked() {
        if (audioPlayer != null) {
            audioPlayer.stopAudio();
        }
    }

    @OnClick(R.id.delete_audio_button)
    public void onDeleteAudioClicked() {
        if (audioRecordedFile != null && audioRecordedFile.exists()) {
            audioRecordedFile.delete();
        }
        recordAudioLayout.setVisibility(View.VISIBLE);
        playAudioLayout.setVisibility(View.GONE);
        audioRecordProgress.setText("00:00");
        isAudioRecordingStarted = false;
        recordedSeconds = 0;
    }

    @OnClick(R.id.send_request_button)
    public void onSendRequestClicked() {
        planetType = planetTypeEditText.getText().toString();
        planetCat = planetCatEditText.getText().toString();
        problemDesc = questionDescEditText.getText().toString();

        if (audioRecorder != null) {
            onStopRecordingAudio();
        }
        if (problemDesc.isEmpty() && audioRecordedFile == null) {
            Toast.makeText(this, "يجب إضافة المشكلة كنص او كتسجيل صوتي", Toast.LENGTH_SHORT).show();
        } else {
            //upload images firstly
            if (images != null) {
                progressDialog.show();
                newRequestViewModel.uploadImageToFirebaseStorage(Uri.parse(images.get(uploadedImageIndex).getUrl()));
            }
        }
    }
}