package com.egrobots.shagarah.presentation.surveyquestions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.RequestSurveyQuestion;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SurveyQuestion2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SurveyQuestion2Fragment extends Fragment {

    @BindView(R.id.q3_option1_checkbox)
    CheckBox option1CheckBox;
    @BindView(R.id.q3_option2_checkbox)
    CheckBox option2CheckBox;
    @BindView(R.id.q3_option3_checkbox)
    CheckBox option3CheckBox;
    @BindView(R.id.q3_option4_checkbox)
    CheckBox option4CheckBox;

    public SurveyQuestion2Fragment() {
    }

    public static SurveyQuestion2Fragment newInstance() {
        SurveyQuestion2Fragment fragment = new SurveyQuestion2Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.q3_survey_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public RequestSurveyQuestion getAnswer() {
        List<String> answerList = new ArrayList<>();
        if (option1CheckBox.isChecked()) answerList.add(option1CheckBox.getText().toString());
        if (option2CheckBox.isChecked()) answerList.add(option2CheckBox.getText().toString());
        if (option3CheckBox.isChecked()) answerList.add(option3CheckBox.getText().toString());
        if (option4CheckBox.isChecked()) answerList.add(option3CheckBox.getText().toString());

        if (answerList.isEmpty()) {
            return null;
        } else {
            RequestSurveyQuestion question = new RequestSurveyQuestion();
            question.setId(RequestSurveyQuestion.SurveyQuestionId.Q3.id);
            question.setQuestion(RequestSurveyQuestion.SurveyQuestionId.Q3.question);
            question.setAnswer(answerList);
            return question;
        }
    }
}