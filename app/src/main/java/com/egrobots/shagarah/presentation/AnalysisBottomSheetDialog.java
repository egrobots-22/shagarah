package com.egrobots.shagarah.presentation;

import android.content.Context;
import android.view.View;
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
    private AnalysisQuestionsCallback analysisQuestionsCallback;
    private Context context;

    @BindView(R.id.tree_type_list_view)
    ListView treeTypeListView;
    @BindView(R.id.tree_type_value_edit_text)
    EditText treeTypeEditText;
//    @BindView(R.id.other_type_edit_text)
//    LinearLayout otherTypeLayout;

    @BindView(R.id.tree_category_list_view)
    ListView treeCategoriesListView;
    @BindView(R.id.tree_category_value_edit_text)
    EditText treeCategoryEditText;
    @BindView(R.id.other_category_edit_text)
    LinearLayout otherCategoryLayout;

    @BindView(R.id.afat_list_view)
    ListView afatListView;
    @BindView(R.id.afat_value_edit_text)
    EditText afatEditText;
    @BindView(R.id.selected_afat_layout)
    LinearLayout selectedAfatLayout;
    @BindView(R.id.other_afat_edit_text)
    LinearLayout otherAfatLayout;

    @BindView(R.id.amrad_3odwia_list_view)
    ListView amrad3odwiaListView;
    @BindView(R.id.amrad_3odwia_value_edit_text)
    EditText amrad3odwiaEditText;
    @BindView(R.id.selected_amrad_3odwia_layout)
    LinearLayout selectedAmrad3odwiaLayout;
    @BindView(R.id.other_3odwia_edit_text)
    LinearLayout other3odwiaLayout;

    @BindView(R.id.amrad_bikteria_list_view)
    ListView amradBikteriaListView;
    @BindView(R.id.amrad_bikteria_value_edit_text)
    EditText amradBikteriaEditText;
    @BindView(R.id.selected_amrad_bikteria_layout)
    LinearLayout selectedAmradBikteriaLayout;
    @BindView(R.id.other_bikteria_edit_text)
    LinearLayout otherBikteriaLayout;

    @BindView(R.id.amrad_fetrya_list_view)
    ListView amradFetryaListView;
    @BindView(R.id.amrad_fetrya_value_edit_text)
    EditText amradFetryaEditText;
    @BindView(R.id.selected_amrad_fetrya_layout)
    LinearLayout selectedAmradFetryaLayout;
    @BindView(R.id.other_fetrya_edit_text)
    LinearLayout otherFetryaLayout;

    @BindView(R.id.amrad_viruses_list_view)
    ListView amradVirusesListView;
    @BindView(R.id.amrad_viruses_value_edit_text)
    EditText amradVirusesEditText;
    @BindView(R.id.selected_amrad_viruses_layout)
    LinearLayout selectedAmradVirusesLayout;
    @BindView(R.id.other_viruses_edit_text)
    LinearLayout otherVirusesLayout;

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
    private List<String> selectedAfat = new ArrayList<>();
    private List<String> selectedAmrad3odwia = new ArrayList<>();
    private List<String> selectedAmraBikteria = new ArrayList<>();
    private List<String> selectedAmradFetrya = new ArrayList<>();
    private List<String> selectedAmradViruses = new ArrayList<>();
    private SearchableList categoriesSearchableList, afatSearchableList, amrad3odwiaSearchableList,
            amradBikteriaSearchableList, amradFetryaSearchableList, amradVirusesSearchableList;

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

        FrameLayout bottomSheet = findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setDraggable(false);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        setTreeTypesSpinner();
    }

    private void setTreeTypesSpinner() {
        ArrayList<String> typesList = new ArrayList<>();
        for (TreeType treeType : treeTypeList) {
            typesList.add(treeType.getName());
        }
        SearchableList treeTypesSearchableList = new SearchableList();
        treeTypesSearchableList.setItemsList(getContext(), typesList, treeTypeListView, treeTypeEditText, false, null, false, null,
                (position, selectedItem, selectedItemsList) -> {
                    selectedTreeType = selectedItem;
                    clearOldSelections();
                    setCategoriesList(position);
                    setAfatList(position);
                    setAmrad3odwiaList(position);
                    setAmradBikteriaList(position);
                    setAmradFetryaList(position);
                    setAmradVirusesList(position);
                });
    }

    private void clearOldSelections() {
        selectedCategory = "";
        treeCategoryEditText.setText("");
        afatEditText.setText("");
        amrad3odwiaEditText.setText("");
        amradBikteriaEditText.setText("");
        amradFetryaEditText.setText("");
        amradVirusesEditText.setText("");

        selectedAmrad3odwia.clear();
        selectedAfat.clear();
        selectedAmraBikteria.clear();
        selectedAmradFetrya.clear();
        selectedAmradViruses.clear();

        selectedAfatLayout.removeAllViews();
        selectedAmrad3odwiaLayout.removeAllViews();
        selectedAmradBikteriaLayout.removeAllViews();
        selectedAmradFetryaLayout.removeAllViews();
        selectedAmradVirusesLayout.removeAllViews();

        treeCategoriesListView.setVisibility(View.GONE);
        afatListView.setVisibility(View.GONE);
        amrad3odwiaListView.setVisibility(View.GONE);
        amradBikteriaListView.setVisibility(View.GONE);
        amradFetryaListView.setVisibility(View.GONE);
        amradVirusesListView.setVisibility(View.GONE);
    }

    private void setCategoriesList(int position) {
        List<String> catsList = treeTypeList.get(position).getCats();
        categoriesSearchableList = new SearchableList();
        categoriesSearchableList.setItemsList(getContext(),
                catsList, treeCategoriesListView, treeCategoryEditText, false, null, true, otherCategoryLayout,
                (selectedPosition, selectedItem, selectedItemsList) -> selectedCategory = selectedItem);
    }

    private void setAfatList(int position) {
        List<String> afatList = treeTypeList.get(position).getAlafat();
        afatSearchableList = new SearchableList();
        afatSearchableList.setItemsList(getContext(),
                afatList, afatListView, afatEditText, true, selectedAfatLayout, true, otherAfatLayout,
                (selectedPosition, selectedItem, selectedItemsList) -> selectedAfat = selectedItemsList);
    }

    private void setAmrad3odwiaList(int position) {
        List<String> amrad3odwiaList = treeTypeList.get(position).getAmrad_3odwia();
        amrad3odwiaSearchableList = new SearchableList();
        amrad3odwiaSearchableList.setItemsList(getContext(),
                amrad3odwiaList, amrad3odwiaListView, amrad3odwiaEditText, true, selectedAmrad3odwiaLayout, true, other3odwiaLayout,
                (selectedPosition, selectedItem, selectedItemsList) -> selectedAmrad3odwia = selectedItemsList);
    }

    private void setAmradBikteriaList(int position) {
        List<String> amradBikteriaList = treeTypeList.get(position).getAmrad_bikteria();
        amradBikteriaSearchableList = new SearchableList();
        amradBikteriaSearchableList.setItemsList(getContext(),
                amradBikteriaList, amradBikteriaListView, amradBikteriaEditText, true, selectedAmradBikteriaLayout, true, otherBikteriaLayout,
                (selectedPosition, selectedItem, selectedItemsList) -> selectedAmraBikteria = selectedItemsList);
    }

    private void setAmradFetryaList(int position) {
        List<String> amradFetryaList = treeTypeList.get(position).getAmrad_fetrya();
        amradFetryaSearchableList = new SearchableList();
        amradFetryaSearchableList.setItemsList(getContext(),
                amradFetryaList, amradFetryaListView, amradFetryaEditText, true, selectedAmradFetryaLayout, true, otherFetryaLayout,
                (selectedPosition, selectedItem, selectedItemsList) -> selectedAmradFetrya = selectedItemsList);
    }

    private void setAmradVirusesList(int position) {
        List<String> amradVirusesList = treeTypeList.get(position).getAmrad_viruses();
        amradVirusesSearchableList = new SearchableList();
        amradVirusesSearchableList.setItemsList(getContext(),
                amradVirusesList, amradVirusesListView, amradVirusesEditText, true, selectedAmradVirusesLayout, true, otherVirusesLayout,
                (selectedPosition, selectedItem, selectedItemsList) -> selectedAmradViruses = selectedItemsList);
    }

    @OnClick(R.id.done_button)
    public void onDoneClicked() {
        String diseases = afatEditText.getText().toString();
        String tasmed = tasmedEditText.getText().toString();
        String alray = alrayEditText.getText().toString();
        String operations = operationsEditText.getText().toString();
        String althemar = althemarEditText.getText().toString();

        if (!categoriesSearchableList.getOtherEditText().getText().toString().isEmpty())
            selectedCategory = categoriesSearchableList.getOtherEditText().getText().toString();
        if (!afatSearchableList.getOtherEditText().getText().toString().isEmpty())
            selectedAfat.add(afatSearchableList.getOtherEditText().getText().toString());
        if (!amrad3odwiaSearchableList.getOtherEditText().getText().toString().isEmpty())
            selectedAmrad3odwia.add(amrad3odwiaSearchableList.getOtherEditText().getText().toString());
        if (!amradBikteriaSearchableList.getOtherEditText().getText().toString().isEmpty())
            selectedAmraBikteria.add(amradBikteriaSearchableList.getOtherEditText().getText().toString());
        if (!amradVirusesSearchableList.getOtherEditText().getText().toString().isEmpty())
            selectedAmradViruses.add(amradVirusesSearchableList.getOtherEditText().getText().toString());
        if (!amradFetryaSearchableList.getOtherEditText().getText().toString().isEmpty())
            selectedAmradFetrya.add(amradFetryaSearchableList.getOtherEditText().getText().toString());

        if (selectedTreeType.isEmpty() || selectedCategory.isEmpty()
                || selectedAfat.isEmpty() || selectedAmrad3odwia.isEmpty()
                || selectedAmraBikteria.isEmpty() || selectedAmradViruses.isEmpty() || selectedAmradFetrya.isEmpty()
                || tasmed.isEmpty() || alray.isEmpty() || operations.isEmpty()
                || althemar.isEmpty()) {
            analysisQuestionsCallback.onError(context.getString(R.string.all_questions_required_msg));
        } else {
            QuestionAnalysis questionAnalysis = new QuestionAnalysis(selectedTreeType, selectedCategory
                    , selectedAfat, selectedAmrad3odwia, selectedAmraBikteria
                    , selectedAmradFetrya, selectedAmradViruses, tasmed, alray, operations, althemar);
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
