package com.egrobots.shagarah.presentation;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.QuestionAnalysis;
import com.egrobots.shagarah.managers.AudioPlayer;
import com.egrobots.shagarah.presentation.adapters.ImagesAdapter;
import com.egrobots.shagarah.presentation.helpers.ViewModelProviderFactory;
import com.egrobots.shagarah.presentation.viewmodels.SelectedRequestViewModel;
import com.egrobots.shagarah.utils.Constants;
import com.egrobots.shagarah.utils.Utils;

import javax.inject.Inject;

public class NotAnsweredRequestViewActivity extends DaggerAppCompatActivity {

    public static final int TIME_UNIT = 500;

    @BindView(R.id.request_images_viewpager)
    ViewPager2 requestImagesViewPager;
    @BindView(R.id.request_status_value_text_view)
    TextView requestStatusTextView;
    @BindView(R.id.timestamp_value_text_view)
    TextView timestampTextView;
    @BindView(R.id.request_question_value_text_view)
    TextView requestQuestionTextView;
    @BindView(R.id.play_audio_item_layout)
    View audioQuestionView;
    @BindView(R.id.playButton)
    ImageButton playButton;
    @BindView(R.id.pauseButton)
    ImageButton pauseButton;
    @BindView(R.id.audio_progress_textview)
    TextView audioProgressTextView;
    @BindView(R.id.answer_question_button)
    Button answerQuestionButton;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    @BindView(R.id.audio_length_textview)
    TextView audioLengthTextView;
    @Inject
    ViewModelProviderFactory providerFactory;

    private SelectedRequestViewModel selectedRequestViewModel;
    private ImagesAdapter imagesAdapter;
    private AudioPlayer audioPlayer;
    private Handler handler = new Handler();
    private Runnable mUpdateTimeTask;
    private String requestId;
    private String requestUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_answered_request_view);
        ButterKnife.bind(this);
        setTitle(getString(R.string.tree_analysis_request_title));

        requestId = getIntent().getStringExtra(Constants.REQUEST_ID);
        requestUserId = getIntent().getStringExtra(Constants.REQUEST_USER_ID);
        boolean isAdmin = getIntent().getBooleanExtra(Constants.IS_ADMIN, false);

        imagesAdapter = new ImagesAdapter();
        requestImagesViewPager.setAdapter(imagesAdapter);

        selectedRequestViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(SelectedRequestViewModel.class);
        selectedRequestViewModel.getRequest(requestId);
        observeRequests();
        observeError();
        if (isAdmin) {
            answerQuestionButton.setVisibility(View.VISIBLE);
            observeAnalysisAnswer();
        } else {
            answerQuestionButton.setVisibility(View.GONE);
        }
    }

    private void observeRequests() {
        selectedRequestViewModel.observeRequest().observe(this, request -> {
            if (request != null) {
                imagesAdapter.setImages(request.getImages());
                imagesAdapter.notifyDataSetChanged();
                requestStatusTextView.setText(request.getShownStatus());
                timestampTextView.setText(request.getFormattedDate());
                requestQuestionTextView.setText(request.getTextQuestion().isEmpty()?getString(R.string.no_current_text_question) : request.getTextQuestion());
                if (request.getAudioQuestion()==null || request.getAudioQuestion().isEmpty()) {
                    audioQuestionView.setVisibility(View.GONE);
                } else {
                    audioQuestionView.setVisibility(View.VISIBLE);
                    setAudioPlayer(request.getAudioQuestion());
                }
            }
        });
    }

    private void observeAnalysisAnswer() {
        selectedRequestViewModel.observeAnalysisAnswer().observe(this, success -> {
            if (success) {
                Toast.makeText(NotAnsweredRequestViewActivity.this, R.string.analysis_answer_added_successufly, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(NotAnsweredRequestViewActivity.this, R.string.unkown_error, Toast.LENGTH_SHORT).show();
            }
            analysisBottomSheetDialog.dismiss();
        });
    }

    private void observeError() {
        selectedRequestViewModel.observeError().observe(this, error -> {
            Toast.makeText(NotAnsweredRequestViewActivity.this, error, Toast.LENGTH_SHORT).show();
        });
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
            seekBar.setMax(audioDuration/TIME_UNIT);
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

    private AnalysisBottomSheetDialog analysisBottomSheetDialog;

    @OnClick(R.id.answer_question_button)
    public void onAnswerQuestionButtonClicked() {
        analysisBottomSheetDialog = new AnalysisBottomSheetDialog(this, new AnalysisBottomSheetDialog.AnalysisQuestionsCallback() {
            @Override
            public void onDone(QuestionAnalysis questionAnalysis) {
                selectedRequestViewModel.addAnalysisAnswersToQuestion(requestId, questionAnalysis);
            }

            @Override
            public void onCancel() {
                analysisBottomSheetDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(NotAnsweredRequestViewActivity.this, error, Toast.LENGTH_SHORT).show();
                analysisBottomSheetDialog.dismiss();
            }
        });
        analysisBottomSheetDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (audioPlayer != null) {
            audioPlayer.pauseAudio();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (audioPlayer != null) {
            audioPlayer.stopAudio();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioPlayer != null) {
            audioPlayer.stopAudio();
        }
    }

    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

}