package com.egrobots.shagarah.presentation.helpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.RequestSurveyQuestion;
import com.egrobots.shagarah.presentation.adapters.SurveyQuestionsFragmentsAdapter;
import com.egrobots.shagarah.presentation.surveyquestions.SurveyQuestion1Fragment;
import com.egrobots.shagarah.presentation.surveyquestions.SurveyQuestion3Fragment;
import com.egrobots.shagarah.presentation.surveyquestions.SurveyQuestion4Fragment;
import com.egrobots.shagarah.presentation.surveyquestions.SurveyQuestion2Fragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SurveyDialog extends DialogFragment {

    private int currentQuestionPosition = 0;
    private SurveyQuestionsFragmentsAdapter adapter;
    List<RequestSurveyQuestion> surveyQuestions = new ArrayList<>();
    private SurveyQuestion1Fragment question1Fragment;
    private SurveyQuestion2Fragment question2Fragment;
    private SurveyQuestion3Fragment question3Fragment;
    private SurveyQuestion4Fragment question4Fragment;

    @BindView(R.id.questions_viewPager)
    ViewPager2 surveyQuestionsViewPager;
    @BindView(R.id.q1_view)
    View q1SelectedView;
    @BindView(R.id.q2_view)
    View q2SelectedView;
    @BindView(R.id.q3_view)
    View q3SelectedView;
    @BindView(R.id.q4_view)
    View q4SelectedView;
    @BindView(R.id.next_button)
    Button nextButton;

    public static SurveyDialog newInstance() {
        Bundle args = new Bundle();
        SurveyDialog fragment = new SurveyDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.survey_dialog_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        ButterKnife.bind(this, getDialog());
        setQuestionsViewPager();
    }

    private void setQuestionsViewPager() {
        adapter = new SurveyQuestionsFragmentsAdapter(getActivity().getSupportFragmentManager(), getLifecycle());
        question1Fragment = SurveyQuestion1Fragment.newInstance();
        question2Fragment = SurveyQuestion2Fragment.newInstance();
        question3Fragment = SurveyQuestion3Fragment.newInstance();
        question4Fragment = SurveyQuestion4Fragment.newInstance();
        adapter.addFragment(question1Fragment);
        adapter.addFragment(question2Fragment);
        adapter.addFragment(question3Fragment);
        adapter.addFragment(question4Fragment);

        surveyQuestionsViewPager.setAdapter(adapter);
        surveyQuestionsViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0 :
                        q1SelectedView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cornered_accent_bg));
                        q2SelectedView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cornered_gray_bg));
                        q3SelectedView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cornered_gray_bg));
                        q4SelectedView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cornered_gray_bg));
                        nextButton.setText(R.string.next);
                        currentQuestionPosition = 0;
                        break;
                    case 1 :
                        q2SelectedView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cornered_accent_bg));
                        q3SelectedView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cornered_gray_bg));
                        q4SelectedView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cornered_gray_bg));
                        nextButton.setText(R.string.next);
                        currentQuestionPosition = 1;
                        break;
                    case 2 :
                        q3SelectedView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cornered_accent_bg));
                        q4SelectedView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cornered_gray_bg));
                        nextButton.setText(R.string.next);
                        currentQuestionPosition = 2;
                        break;
                    case 3 :
                        q4SelectedView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cornered_accent_bg));
                        nextButton.setText(R.string.finish);
                        currentQuestionPosition = 3;
                        break;
                }
            }
        });
    }

    public List<RequestSurveyQuestion> getSurveyQuestionsAnswers() {
        return surveyQuestions;
    }

    @OnClick(R.id.next_button)
    public void onNextClicked() {
        currentQuestionPosition++;
        if (currentQuestionPosition == adapter.getItemCount()) {
            dismiss();
            //get survey questions answers
            getSurveyQuestions();
        } else {
            surveyQuestionsViewPager.setCurrentItem(currentQuestionPosition);
        }
    }

    private void getSurveyQuestions() {
        surveyQuestions.add(question1Fragment.getAnswer());
        surveyQuestions.add(question2Fragment.getAnswer());
        surveyQuestions.add(question3Fragment.getAnswer());
        surveyQuestions.add(question4Fragment.getAnswer());
    }

}
