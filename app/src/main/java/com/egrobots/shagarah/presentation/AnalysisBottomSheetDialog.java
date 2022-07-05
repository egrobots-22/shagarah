package com.egrobots.shagarah.presentation;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.QuestionAnalysis;
import com.egrobots.shagarah.data.models.TreeType;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

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

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

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
            typeAdapter.getFilter().filter("");
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
                categoryAdapter.getFilter().filter("");
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

    @OnTextChanged(R.id.tree_category_value_edit_text)
    public void onTreeCategoryTextChanged(CharSequence s) {
        categoryAdapter.getFilter().filter(s);
        treeCategoriesListView.setVisibility(View.VISIBLE);
    }

    private void setDiseasesSpinner() {
        diseasesAdapter = new ArrayAdapter<String>(context, R.layout.spinner_list_item_layout);
        diseasesAdapter.addAll(diseasesList);
        diseasesListView.setAdapter(diseasesAdapter);
        diseasesListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDisease = diseasesAdapter.getItem(position);
            selectedDiseases.add(selectedDisease);
            addSelectedDiseaseTextView(selectedDisease);
            diseasesEditText.setText(selectedDisease);
            diseasesListView.setVisibility(View.GONE);
            diseasesAdapter.getFilter().filter("");
        });
        diseasesEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (isDiseasesListVisible) {
                    diseasesListView.setVisibility(View.GONE);
                    isDiseasesListVisible = false;
                } else {
                    diseasesListView.setVisibility(View.VISIBLE);
                    isDiseasesListVisible = true;
                }
            }
            return false;
        });
        diseasesEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                diseasesListView.setVisibility(View.GONE);
            }
        });
    }

    private void addSelectedDiseaseTextView(String selectedDisease) {
        selectedDiseasesLayout.setVisibility(View.VISIBLE);
        View selectedDiseaseTextViewLayout =
                 LayoutInflater.from(getContext()).inflate(R.layout.selected_disease_textview_layout, selectedDiseasesLayout, false);
        TextView selectedDiseaseTextView = selectedDiseaseTextViewLayout.findViewById(R.id.selected_disease_text_view);
        selectedDiseaseTextView.setText(selectedDisease);
        selectedDiseaseTextView.setOnClickListener(v -> {
            selectedDiseases.remove(selectedDisease);
            selectedDiseasesLayout.removeView(selectedDiseaseTextView);
            if (selectedDiseases.size() == 0) {
                selectedDiseasesLayout.setVisibility(View.GONE);
            }
        });
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(2, 0, 2, 0);
        selectedDiseasesLayout.addView(selectedDiseaseTextView, layoutParams);
    }

    @OnTextChanged(R.id.diseases_value_edit_text)
    public void onDiseasesTextChanged(CharSequence s) {
        diseasesAdapter.getFilter().filter(s);
        diseasesListView.setVisibility(View.VISIBLE);
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
