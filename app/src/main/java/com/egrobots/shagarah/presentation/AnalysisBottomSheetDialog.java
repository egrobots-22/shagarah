package com.egrobots.shagarah.presentation;

import android.content.Context;
import android.widget.EditText;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.QuestionAnalysis;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnalysisBottomSheetDialog extends BottomSheetDialog {

    private AnalysisQuestionsCallback analysisQuestionsCallback;
    private Context context;

    @BindView(R.id.tree_type_value_edit_text)
    EditText treeTypeEditText;
    @BindView(R.id.tree_code_value_edit_text)
    EditText treeCodeEditText;
    @BindView(R.id.tree_status_value_edit_text)
    EditText treeStatusEditText;
    @BindView(R.id.diseases_value_edit_text)
    EditText diseasesEditText;
    @BindView(R.id.tasmed_value_edit_text)
    EditText tasmedEditText;
    @BindView(R.id.alray_value_edit_text)
    EditText alrayEditText;
    @BindView(R.id.operations_value_edit_text)
    EditText operationsEditText;
    @BindView(R.id.althemar_value_edit_text)
    EditText althemarEditText;

    public AnalysisBottomSheetDialog(@NonNull Context context, AnalysisQuestionsCallback analysisQuestionsCallback) {
        super(context);
        this.analysisQuestionsCallback = analysisQuestionsCallback;
        this.context = context;
        setContentView(R.layout.questions_bottom_sheet_dialog);
        setTitle(context.getString(R.string.answer_following_questions));
        ButterKnife.bind(this);
    }

    @OnClick(R.id.done_button)
    public void onDoneClicked() {
        String treeType = treeTypeEditText.getText().toString();
        String treeCode = treeCodeEditText.getText().toString();
        String treeStatus = treeStatusEditText.getText().toString();
        String diseases = diseasesEditText.getText().toString();
        String tasmed = tasmedEditText.getText().toString();
        String alray = alrayEditText.getText().toString();
        String operations = operationsEditText.getText().toString();
        String althemar = althemarEditText.getText().toString();

        if (treeType.isEmpty() || treeCode.isEmpty()
                || treeStatus.isEmpty() || diseases.isEmpty()
                || tasmed.isEmpty() || alray.isEmpty()
                || operations.isEmpty() || althemar.isEmpty()) {
            analysisQuestionsCallback.onError("يجب اجابة جميع الأسئلة");
        } else {
            QuestionAnalysis questionAnalysis = new QuestionAnalysis(treeType, treeCode, treeStatus
                    , diseases, tasmed, alray, operations, althemar);
            analysisQuestionsCallback.onDone(questionAnalysis);
        }
    }

    @OnClick(R.id.cancel_button)
    public void onCancelClicked() {
        analysisQuestionsCallback.onCancel();
    }

    public interface AnalysisQuestionsCallback {
        void onDone(QuestionAnalysis questionAnalysis);

        void onError(String error);

        void onCancel();
    }
}
