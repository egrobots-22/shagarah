package com.egrobots.shagarah.presentation;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.QuestionAnalysis;
import com.egrobots.shagarah.data.models.TreeType;
import com.egrobots.shagarah.presentation.helpers.SearchableList;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnalysisBottomSheetDialog extends BottomSheetDialog {

    private List<TreeType> treeTypeList;
    private List<String> diseasesList;
    private AnalysisQuestionsCallback analysisQuestionsCallback;
    private Context context;

    @BindView(R.id.tree_type_list_view)
    ListView treeTypeListView;
    @BindView(R.id.tree_category_list_view)
    ListView treeCategoriesListView;
    @BindView(R.id.diseases_list_view)
    ListView diseasesListView;
    @BindView(R.id.tree_type_value_edit_text)
    EditText treeTypeEditText;
    @BindView(R.id.tree_category_value_edit_text)
    EditText treeCategoryEditText;
    @BindView(R.id.diseases_value_edit_text)
    EditText diseasesEditText;
    @BindView(R.id.selected_diseases_layout)
    LinearLayout selectedDiseasesLayout;
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
    private boolean isDiseasesListVisible;
    private ArrayAdapter<String> categoryAdapter;
    private ArrayAdapter<String> typeAdapter;
    private ArrayAdapter<String> diseasesAdapter;
    private List<String> selectedDiseases = new ArrayList<>();

    public AnalysisBottomSheetDialog(@NonNull Context context,
                                     List<TreeType> treeTypeList,
                                     List<String> diseasesList,
                                     AnalysisQuestionsCallback analysisQuestionsCallback) {
        super(context);
        this.treeTypeList = treeTypeList;
        this.diseasesList = diseasesList;
        this.analysisQuestionsCallback = analysisQuestionsCallback;
        this.context = context;
        setContentView(R.layout.questions_bottom_sheet_dialog);
        setTitle(context.getString(R.string.answer_following_questions));
        ButterKnife.bind(this);

        FrameLayout bottomSheet = findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        setTreeTypesSpinner();
        setDiseasesSpinner();
    }

    private void setTreeTypesSpinner() {
        ArrayList<String> typesList = new ArrayList<>();
        for (TreeType treeType : treeTypeList) {
            typesList.add(treeType.getName());
        }
        SearchableList treeTypesSearchableList = new SearchableList();
        treeTypesSearchableList.setItemsList(getContext(), typesList, treeTypeListView, treeTypeEditText, false, null,
                (position, selectedItem, selectedItemsList) -> {
                    selectedTreeType = selectedItem;
                    setCategoriesSpinner(position);
                    setDiseasesSpinner();
                });
    }

    private void setCategoriesSpinner(int position) {
        List<String> catsList = treeTypeList.get(position).getCats();
        SearchableList categoriesSearchableList = new SearchableList();
        categoriesSearchableList.setItemsList(getContext(),
                catsList, treeCategoriesListView, treeCategoryEditText, false, null,
                (position1, selectedItem, selectedItemsList) -> selectedCategory = selectedItem);
    }

    private void setDiseasesSpinner() {
        SearchableList diseasesSearchableList = new SearchableList();
        diseasesSearchableList.setItemsList(getContext(),
                diseasesList, diseasesListView, diseasesEditText, true, selectedDiseasesLayout,
                (position, selectedItem, selectedItemsList) -> selectedDiseases = selectedItemsList);
    }


    @OnClick(R.id.done_button)
    public void onDoneClicked() {
        String diseases = diseasesEditText.getText().toString();
        String tasmed = tasmedEditText.getText().toString();
        String alray = alrayEditText.getText().toString();
        String operations = operationsEditText.getText().toString();
        String althemar = althemarEditText.getText().toString();

        if (selectedTreeType.isEmpty() || selectedCategory.isEmpty()
                || selectedDiseases.isEmpty() || tasmed.isEmpty() || alray.isEmpty()
                || operations.isEmpty() || althemar.isEmpty()) {
            analysisQuestionsCallback.onError(context.getString(R.string.all_questions_required_msg));
        } else {
            QuestionAnalysis questionAnalysis = new QuestionAnalysis(selectedTreeType, selectedCategory
                    , selectedDiseases, tasmed, alray, operations, althemar);
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
