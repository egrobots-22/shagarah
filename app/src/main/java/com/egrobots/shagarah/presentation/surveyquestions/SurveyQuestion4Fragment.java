package com.egrobots.shagarah.presentation.surveyquestions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.RequestSurveyQuestion;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SurveyQuestion4Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SurveyQuestion4Fragment extends Fragment {

    @BindView(R.id.q1_option1_checkbox)
    CheckBox option1CheckBox;
    @BindView(R.id.q1_option2_checkbox)
    CheckBox option2CheckBox;
    @BindView(R.id.q1_option3_checkbox)
    CheckBox option3CheckBox;
    @BindView(R.id.q1_no_selected_option_radiobutton)
    RadioButton noSelectedRadioButton;


    public SurveyQuestion4Fragment() {
    }

    public static SurveyQuestion4Fragment newInstance() {
        SurveyQuestion4Fragment fragment = new SurveyQuestion4Fragment();
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
        View view = inflater.inflate(R.layout.q1_survey_layout, container, false);
        ButterKnife.bind(this, view);
        setViews();
        return view;
    }

    private void setViews() {
        noSelectedRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                option1CheckBox.setChecked(false);
                option2CheckBox.setChecked(false);
                option3CheckBox.setChecked(false);
            }
        });

        option1CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) noSelectedRadioButton.setChecked(false);
        });
        option2CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) noSelectedRadioButton.setChecked(false);
        });
        option3CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) noSelectedRadioButton.setChecked(false);
        });
    }

    public RequestSurveyQuestion getAnswer() {
        List<String> answerList = new ArrayList<>();
        if (noSelectedRadioButton.isChecked()) {
            answerList.add("لا شئ");
        } else {
            if (option1CheckBox.isChecked()) answerList.add(option1CheckBox.getText().toString());
            if (option2CheckBox.isChecked()) answerList.add(option2CheckBox.getText().toString());
            if (option3CheckBox.isChecked()) answerList.add(option3CheckBox.getText().toString());
        }

        if (answerList.isEmpty()) {
            return null;
        } else {
            RequestSurveyQuestion question = new RequestSurveyQuestion();
            question.setId(RequestSurveyQuestion.SurveyQuestionId.Q1.id);
            question.setQuestion(RequestSurveyQuestion.SurveyQuestionId.Q1.question);
            question.setAnswer(answerList);
            return question;
        }
    }
}