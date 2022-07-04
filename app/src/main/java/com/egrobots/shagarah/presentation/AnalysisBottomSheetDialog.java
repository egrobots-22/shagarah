package com.egrobots.shagarah.presentation;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.QuestionAnalysis;
import com.egrobots.shagarah.data.models.TreeType;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnalysisBottomSheetDialog extends BottomSheetDialog {

    private final List<TreeType> treeTypeList;
    private AnalysisQuestionsCallback analysisQuestionsCallback;
    private Context context;

    @BindView(R.id.tree_type_value_edit_text)
    EditText treeTypeEditText;
    @BindView(R.id.tree_type_value_spinner)
    Spinner treeTypeSpinner;
    @BindView(R.id.tree_category_value_edit_text)
    EditText treeCodeEditText;
    @BindView(R.id.tree_category_value_spinner)
    Spinner treeCategorySpinner;
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
    private String selectedTreeType = "";
    private String selectedCategory = "";


    public AnalysisBottomSheetDialog(@NonNull Context context,
                                     List<TreeType> treeTypeList,
                                     AnalysisQuestionsCallback analysisQuestionsCallback) {
        super(context);
        this.treeTypeList = treeTypeList;
        this.analysisQuestionsCallback = analysisQuestionsCallback;
        this.context = context;
        setContentView(R.layout.questions_bottom_sheet_dialog);
        setTitle(context.getString(R.string.answer_following_questions));
        ButterKnife.bind(this);

        setTreeTypesSpinner();
    }

    private void setTreeTypesSpinner() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item);
        ArrayList<String> typesList = new ArrayList<>();
        for (TreeType treeType : treeTypeList) {
            typesList.add(treeType.getType());
        }
        typeAdapter.addAll(typesList);
        treeTypeSpinner.setAdapter(typeAdapter);
        treeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTreeType = treeTypeList.get(position).getType();
                setCategorySpinner(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setCategorySpinner(int position) {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item);
        List<String> categoryList = treeTypeList.get(position).getCategory();
        categoryAdapter.addAll(categoryList);
        treeCategorySpinner.setAdapter(categoryAdapter);
        treeCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categoryList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.done_button)
    public void onDoneClicked() {
        String treeType = treeTypeEditText.getText().toString();
        String treeCode = treeCodeEditText.getText().toString();
        String diseases = diseasesEditText.getText().toString();
        String tasmed = tasmedEditText.getText().toString();
        String alray = alrayEditText.getText().toString();
        String operations = operationsEditText.getText().toString();
        String althemar = althemarEditText.getText().toString();

        if (selectedTreeType.isEmpty() || selectedCategory.isEmpty()
                || diseases.isEmpty() || tasmed.isEmpty() || alray.isEmpty()
                || operations.isEmpty() || althemar.isEmpty()) {
            analysisQuestionsCallback.onError(context.getString(R.string.all_questions_required_msg));
        } else {
            QuestionAnalysis questionAnalysis = new QuestionAnalysis(selectedTreeType, selectedCategory
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
