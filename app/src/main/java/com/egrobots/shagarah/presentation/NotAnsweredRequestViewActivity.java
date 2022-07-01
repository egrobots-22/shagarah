package com.egrobots.shagarah.presentation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.models.Request;
import com.egrobots.shagarah.presentation.adapters.ImagesAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotAnsweredRequestViewActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_answered_request_view);
        ButterKnife.bind(this);
        setTitle(getString(R.string.tree_analysis_request_title));

        String requestId = getIntent().getStringExtra("request_id");
        String requestUserId = getIntent().getStringExtra("request_user_id");

        ImagesAdapter imagesAdapter = new ImagesAdapter();
        requestImagesViewPager.setAdapter(imagesAdapter);
        FirebaseDatabase.getInstance()
                .getReference("requests")
                .child(requestUserId)
                .child(requestId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Request request = snapshot.getValue(Request.class);
                        imagesAdapter.setImages(request.getImages());
                        imagesAdapter.notifyDataSetChanged();
                        requestStatusTextView.setText(request.getStatus());
                        timestampTextView.setText(request.getTimestamp());
                        requestQuestionTextView.setText(request.getTextQuestion().isEmpty()?"لا يوجد سؤال نصي" : request.getTextQuestion());
                        if (request.getAudioQuestion()==null || request.getAudioQuestion().isEmpty()) {
                            audioQuestionView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @OnClick(R.id.answer_question_button)
    public void onAnswerQuestionButtonClicked() {
        Toast.makeText(this, "Question will be answered", Toast.LENGTH_SHORT).show();
        //showing fragment with questions to answer them
    }
}