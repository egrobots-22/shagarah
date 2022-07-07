package com.egrobots.shagarah.presentation.helpers;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.egrobots.shagarah.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class SearchableList {

    private Context context;
    private List<String> itemsList;
    private ArrayAdapter<String> listAdapter;
    private ListView listView;
    private boolean canAddMultipleItems;
    private boolean canAddOtherValue;
    private EditText searchableEditText;
    private List<String> selectedItemsList = new ArrayList<>();
    private LinearLayout selectedItemsLayout;
    private LinearLayout otherLayout;
    private SearchableListCallback searchableListCallback;
    private boolean isListVisible;

    public void setItemsList(Context context,
                             List<String> itemsList,
                             ListView listView,
                             EditText searchableEditText,
                             boolean canAddMultipleItems,
                             @Nullable LinearLayout selectedItemsLayout,
                             boolean canAddOtherValue,
                             LinearLayout otherLayout,
                             @NotNull SearchableListCallback searchableListCallback) {
        this.context = context;
        this.itemsList = itemsList;
        this.listView = listView;
        this.searchableEditText = searchableEditText;
        this.canAddMultipleItems = canAddMultipleItems;
        this.selectedItemsLayout = selectedItemsLayout;
        this.canAddOtherValue = canAddOtherValue;
        this.otherLayout = otherLayout;
        this.searchableListCallback = searchableListCallback;

        listAdapter = new ArrayAdapter<>(context, R.layout.spinner_list_item_layout);
        if (canAddOtherValue) {
            itemsList.add(context.getString(R.string.other_string));
        }
        listAdapter.addAll(itemsList);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position == listAdapter.getCount() - 1 && listAdapter.getItem(position).equals(context.getResources().getString(R.string.other_string))) {
                otherLayout.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                if (selectedItemsLayout != null) {
                    selectedItemsLayout.setVisibility(View.GONE);
                    selectedItemsList.clear();
                    selectedItemsLayout.removeAllViews();
                }
            } else {
                if (otherLayout!=null) {
                    otherLayout.setVisibility(View.GONE);
                    getOtherEditText().setText("");
                }
                String selectedItem = listAdapter.getItem(position);
                searchableEditText.setText(selectedItem);
                if (canAddMultipleItems) {
                    searchableEditText.setText("");
                    if (!isItemExistInList(selectedItem)) {
                        selectedItemsList.add(selectedItem);
                        addSelectedItemTextView(selectedItem);
                    }
                }
                listView.setVisibility(View.GONE);
                listAdapter.getFilter().filter("");
                if (canAddMultipleItems) {
                    searchableListCallback.onItemSelected(position, null, selectedItemsList);
                } else {
                    searchableListCallback.onItemSelected(position, selectedItem, null);
                }
            }
        });
        searchableEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                toggleListVisibility();
            }
            return false;
        });
        searchableEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                listView.setVisibility(View.GONE);
            }
        });
        searchableEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listAdapter.getFilter().filter(s);
                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean isItemExistInList(String selectedItem) {
        for (String item : selectedItemsList) {
            if (selectedItem.equals(item)) {
                return true;
            }
        }
        return false;
    }

    private void addSelectedItemTextView(String selectedItem) {
        selectedItemsLayout.setVisibility(View.VISIBLE);
        View selectedDiseaseTextViewLayout =
                LayoutInflater.from(context).inflate(R.layout.selected_disease_textview_layout, selectedItemsLayout, false);
        TextView selectedDiseaseTextView = selectedDiseaseTextViewLayout.findViewById(R.id.selected_disease_text_view);
        selectedDiseaseTextView.setText(selectedItem);
        selectedDiseaseTextView.setOnClickListener(v -> {
            selectedItemsList.remove(selectedItem);
            selectedItemsLayout.removeView(selectedDiseaseTextView);
            if (selectedItemsList.size() == 0) {
                selectedItemsLayout.setVisibility(View.GONE);
            }
        });
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(2, 0, 2, 0);
        selectedItemsLayout.addView(selectedDiseaseTextView, layoutParams);
    }

    public EditText getOtherEditText() {
        return otherLayout.findViewById(R.id.other_edit_text);
    }

    private void toggleListVisibility() {
        if (isListVisible) {
            listView.setVisibility(View.GONE);
            isListVisible = false;
        } else {
            listView.setVisibility(View.VISIBLE);
            isListVisible = true;
        }
    }

    public interface SearchableListCallback {
        void onItemSelected(int position, String selectedItem, List<String> selectedItemsList);
    }
}
