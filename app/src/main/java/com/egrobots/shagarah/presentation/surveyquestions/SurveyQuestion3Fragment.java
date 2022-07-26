package com.egrobots.shagarah.presentation.surveyquestions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.RequestSurveyQuestion;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SurveyQuestion3Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SurveyQuestion3Fragment extends Fragment {

    @BindView(R.id.q2_radio_group)
    RadioGroup q2RadioGroupOptions;
    @BindView(R.id.q2_option1_radio_button)
    RadioButton option1RadioButton;
    @BindView(R.id.q2_option2_radio_button)
    RadioButton option2RadioButton;
    @BindView(R.id.q2_option3_radio_button)
    RadioButton option3RadioButton;

    public SurveyQuestion3Fragment() {
    }

    public static SurveyQuestion3Fragment newInstance() {
        SurveyQuestion3Fragment fragment = new SurveyQuestion3Fragment();
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
        View view = inflater.inflate(R.layout.q2_survey_layout, container, false);;
        ButterKnife.bind(this, view);
        return view;
    }

    public RequestSurveyQuestion getAnswer() {
        List<String> answerList = new ArrayList<>();
        switch (q2RadioGroupOptions.getCheckedRadioButtonId()) {
            case R.id.q2_option1_radio_button:
                answerList.add(option1RadioButton.getText().toString());
                break;
            case R.id.q2_option2_radio_button:
                answerList.add(option2RadioButton.getText().toString());
                break;
            case R.id.q2_option3_radio_button:
                answerList.add(option3RadioButton.getText().toString());
                break;
        }
        if (answerList.isEmpty()) {
            return null;
        } else {
            RequestSurveyQuestion question = new RequestSurveyQuestion();
            question.setId(RequestSurveyQuestion.SurveyQuestionId.Q2.id);
            question.setQuestion(RequestSurveyQuestion.SurveyQuestionId.Q2.question);
            question.setAnswer(answerList);
            return question;
        }
    }
}