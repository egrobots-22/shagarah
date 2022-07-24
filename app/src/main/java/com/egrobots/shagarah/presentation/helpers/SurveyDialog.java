package com.egrobots.shagarah.presentation.helpers;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.RequestSurveyQuestion;
import com.egrobots.shagarah.presentation.adapters.FruitsAdapter;
import com.egrobots.shagarah.presentation.adapters.SelectedFruitsAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SurveyDialog extends DialogFragment {

    private int currentQuestionPosition = 1;
    private List<RequestSurveyQuestion> surveyQuestions = new ArrayList<>();
    private SurveyDialogCallback surveyDialogCallback;

    @BindView(R.id.question_layout_1)
    View questionLayout1;
    @BindView(R.id.question_layout_2)
    View questionLayout2;
    @BindView(R.id.question_layout_3)
    View questionLayout3;
    @BindView(R.id.question_layout_4)
    View questionLayout4;
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
    }

    public void setSurveyDialogCallback(SurveyDialogCallback surveyDialogCallback){
        this.surveyDialogCallback = surveyDialogCallback;
    }

    @OnClick(R.id.skip_button)
    public void onSkipClicked() {
        dismiss();
//        onNextClicked();
    }

    @OnClick(R.id.next_button)
    public void onNextClicked() {
        if (currentQuestionPosition == 4) {
            if (getQuestion4Answer()) {
                surveyDialogCallback.onSurveyCompleted(surveyQuestions);
                dismiss();
            }
        } else {
            switch (currentQuestionPosition) {
                case 1:
                    if (getQuestion1Answer()) {
                        setQuestion2();
                        currentQuestionPosition++;
                    }
                    break;
                case 2:
                    if (getQuestion2Answer()) {
                        setQuestion3();
                        currentQuestionPosition++;
                    }
                    break;
                case 3:
                    if (getQuestion3Answer()) {
                        setQuestion4();
                        currentQuestionPosition++;
                    }
                    break;
            }
        }
    }

    private boolean getQuestion1Answer() {
        CheckBox option1CheckBox = questionLayout1.findViewById(R.id.q1_option1_checkbox);
        CheckBox option2CheckBox = questionLayout1.findViewById(R.id.q1_option2_checkbox);
        CheckBox option3CheckBox = questionLayout1.findViewById(R.id.q1_option3_checkbox);
        RadioButton noSelectedRadioButton = questionLayout1.findViewById(R.id.q1_no_selected_option_radiobutton);
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

        List<String> answerList = new ArrayList<>();
        if (noSelectedRadioButton.isChecked()) {
            answerList.add("لا شئ");
        } else {
            if (option1CheckBox.isChecked()) answerList.add(option1CheckBox.getText().toString());
            if (option2CheckBox.isChecked()) answerList.add(option2CheckBox.getText().toString());
            if (option3CheckBox.isChecked()) answerList.add(option3CheckBox.getText().toString());
        }
        if (answerList.isEmpty()) {
            Toast.makeText(getContext(), "يحب أن تختار إجابة", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            RequestSurveyQuestion question = new RequestSurveyQuestion();
            question.setQuestion("ما تم رشه في الشهر الماضي ؟");
            question.setAnswer(answerList);

            surveyQuestions.add(question);
            return true;
        }
    }

    private boolean getQuestion2Answer() {
        RadioGroup q2RadioGroupOptions = questionLayout2.findViewById(R.id.q2_radio_group);
        q2RadioGroupOptions.setOnCheckedChangeListener((group, checkedId) -> {

        });
        if (q2RadioGroupOptions.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "يحب أن تختار إجابة", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            List<String> answerList = new ArrayList<>();
            switch (q2RadioGroupOptions.getCheckedRadioButtonId()) {
                case R.id.q2_option1_radio_button:
                    answerList.add(((RadioButton) questionLayout2.findViewById(R.id.q2_option1_radio_button)).getText().toString());
                    break;
                case R.id.q2_option2_radio_button:
                    answerList.add(((RadioButton) questionLayout2.findViewById(R.id.q2_option2_radio_button)).getText().toString());
                    break;
                case R.id.q2_option3_radio_button:
                    answerList.add(((RadioButton) questionLayout2.findViewById(R.id.q2_option3_radio_button)).getText().toString());
                    break;
            }
            RequestSurveyQuestion question = new RequestSurveyQuestion();
            question.setQuestion("مكم من المحاصيل الخاصة بك وتعاني من هذه المشكلة؟");
            question.setAnswer(answerList);

            surveyQuestions.add(question);
            return true;
        }
    }

    private boolean getQuestion3Answer() {
        CheckBox option1CheckBox = questionLayout3.findViewById(R.id.q3_option1_checkbox);
        CheckBox option2CheckBox = questionLayout3.findViewById(R.id.q3_option2_checkbox);
        CheckBox option3CheckBox = questionLayout3.findViewById(R.id.q3_option3_checkbox);
        CheckBox option4CheckBox = questionLayout3.findViewById(R.id.q3_option4_checkbox);

        List<String> answerList = new ArrayList<>();
        if (option1CheckBox.isChecked()) answerList.add(option1CheckBox.getText().toString());
        if (option2CheckBox.isChecked()) answerList.add(option2CheckBox.getText().toString());
        if (option3CheckBox.isChecked()) answerList.add(option3CheckBox.getText().toString());
        if (option4CheckBox.isChecked()) answerList.add(option3CheckBox.getText().toString());

        if (answerList.isEmpty()) {
            Toast.makeText(getContext(), "يحب أن تختار إجابة", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            RequestSurveyQuestion question = new RequestSurveyQuestion();
            question.setQuestion("أي أجزاء من الأعراض تظهر مصنع ؟");
            question.setAnswer(answerList);

            surveyQuestions.add(question);
            return true;
        }
    }


    private SelectedFruitsAdapter selectedFruitsAdapter;

    private boolean getQuestion4Answer() {

        if (selectedFruitsAdapter.getSelectedFruits().isEmpty()) {
            Toast.makeText(getContext(), "يحب أن تختار إجابة", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            RequestSurveyQuestion question = new RequestSurveyQuestion();
            question.setQuestion("اختيار المحاصيل الخاصة بك");
            question.setAnswer(selectedFruitsAdapter.getSelectedFruits());

            surveyQuestions.add(question);
            return true;
        }
    }

    private void setQuestion2() {
        questionLayout1.setVisibility(View.GONE);
        questionLayout2.setVisibility(View.VISIBLE);
        questionLayout3.setVisibility(View.GONE);
        questionLayout4.setVisibility(View.GONE);
        q2SelectedView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cornered_accent_bg));
    }

    private void setQuestion3() {
        questionLayout1.setVisibility(View.GONE);
        questionLayout2.setVisibility(View.GONE);
        questionLayout3.setVisibility(View.VISIBLE);
        questionLayout4.setVisibility(View.GONE);
        q3SelectedView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cornered_accent_bg));
    }

    private void setQuestion4() {
        questionLayout1.setVisibility(View.GONE);
        questionLayout2.setVisibility(View.GONE);
        questionLayout3.setVisibility(View.GONE);
        questionLayout4.setVisibility(View.VISIBLE);
        q4SelectedView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cornered_accent_bg));
        nextButton.setText("انهاء");

        RecyclerView selectedFruitsRecyclerView = questionLayout4.findViewById(R.id.selected_fruits_recycler_view);
        selectedFruitsAdapter = new SelectedFruitsAdapter();
        selectedFruitsRecyclerView.setAdapter(selectedFruitsAdapter);
        selectedFruitsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView fruitsRecyclerView = questionLayout4.findViewById(R.id.fruit_recycler_view);
        List<String> fruits = new ArrayList<>();
        fruits.add("تفاحة");
        fruits.add("موز");
        fruits.add("ريحان");
        fruits.add("أفوكادو");
        fruits.add("فراولة");
        fruits.add("جوافة");
        fruits.add("كمثرى");
        FruitsAdapter fruitsAdapter = new FruitsAdapter(fruits, new FruitsAdapter.FruitsAdapterCallback() {
            @Override
            public void onFruitItemClicked(String selectedFruit) {
                selectedFruitsAdapter.addItem(selectedFruit);
            }
        });
        fruitsRecyclerView.setAdapter(fruitsAdapter);
        fruitsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        EditText searchView = questionLayout4.findViewById(R.id.search_edit_text);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fruitsAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public interface SurveyDialogCallback {
        void onSurveyCompleted(List<RequestSurveyQuestion> surveyQuestions);
    }
}
