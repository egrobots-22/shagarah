package com.egrobots.shagarah.presentation;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

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
import butterknife.OnTextChanged;

public class AnalysisBottomSheetDialog extends BottomSheetDialog {

    private final List<TreeType> treeTypeList;
    private AnalysisQuestionsCallback analysisQuestionsCallback;
    private Context context;

    @BindView(R.id.tree_type_list_view)
    ListView treeTypeListView;
    @BindView(R.id.tree_category_list_view)
    ListView treeCategoriesListView;
    @BindView(R.id.tree_type_value_edit_text)
    EditText treeTypeEditText;
    @BindView(R.id.tree_category_value_edit_text)
    EditText treeCategoryEditText;
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
    private boolean isTreeTypesListVisible;
    private boolean isTreeCategoryListVisible;
    private ArrayAdapter<String> categoryAdapter;


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

    private ArrayAdapter<String> typeAdapter;

    private void setTreeTypesSpinner() {
        typeAdapter = new ArrayAdapter<String>(context, R.layout.spinner_list_item_layout);
        ArrayList<String> typesList = new ArrayList<>();
        for (TreeType treeType : treeTypeList) {
            typesList.add(treeType.getType());
        }
        typeAdapter.addAll(typesList);
        treeTypeListView.setAdapter(typeAdapter);
        treeTypeListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedTreeType = typeAdapter.getItem(position);
            treeTypeEditText.setText(selectedTreeType);
            treeTypeListView.setVisibility(View.GONE);
            setCategorySpinner(position);
        });
        treeTypeEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (isTreeTypesListVisible) {
                    treeTypeListView.setVisibility(View.GONE);
                    isTreeTypesListVisible = false;
                } else {
                    treeTypeListView.setVisibility(View.VISIBLE);
                    isTreeTypesListVisible = true;
                }
            }
            return false;
        });
        treeTypeEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                treeTypeListView.setVisibility(View.GONE);
            }
        });
    }

    @OnTextChanged(R.id.tree_type_value_edit_text)
    public void onTreeTypeTextChanged(CharSequence s) {
        typeAdapter.getFilter().filter(s);
        treeTypeListView.setVisibility(View.VISIBLE);
    }

    @OnTextChanged(R.id.tree_category_value_edit_text)
    public void onTreeCategoryTextChanged(CharSequence s) {
        categoryAdapter.getFilter().filter(s);
        treeCategoriesListView.setVisibility(View.VISIBLE);
    }

    private void setCategorySpinner(int position) {
        categoryAdapter = new ArrayAdapter<String>(context, R.layout.spinner_list_item_layout);
        List<String> categoryList = treeTypeList.get(position).getCategory();
        categoryAdapter.addAll(categoryList);
        treeCategoriesListView.setAdapter(categoryAdapter);
        treeCategoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categoryAdapter.getItem(position);
                treeCategoryEditText.setText(selectedCategory);
                treeCategoriesListView.setVisibility(View.GONE);
            }
        });
        treeCategoryEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (isTreeCategoryListVisible) {
                    treeCategoriesListView.setVisibility(View.GONE);
                    isTreeCategoryListVisible = false;
                } else {
                    treeCategoriesListView.setVisibility(View.VISIBLE);
                    isTreeCategoryListVisible = true;
                }
            }
            return false;
        });
        treeCategoryEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                treeCategoriesListView.setVisibility(View.GONE);
            }
        });
    }

    @OnClick(R.id.done_button)
    public void onDoneClicked() {
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
