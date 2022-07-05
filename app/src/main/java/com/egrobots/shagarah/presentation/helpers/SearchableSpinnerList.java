package com.egrobots.shagarah.presentation.helpers;

import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class SearchableSpinnerList {

    private AppCompatActivity activity;
    private List<String> itemsList;
    private ListView listView;
    private EditText searchableEditText;

    public SearchableSpinnerList(AppCompatActivity activity, List<String> itemsList, ListView listView, EditText searchableEditText) {
        this.activity = activity;
        this.itemsList = itemsList;
        this.listView = listView;
        this.searchableEditText = searchableEditText;
    }


}
