package com.egrobots.shagarah.presentation.surveyquestions;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.Planet;
import com.egrobots.shagarah.data.models.RequestSurveyQuestion;
import com.egrobots.shagarah.presentation.adapters.PlanetsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SurveyQuestion1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SurveyQuestion1Fragment extends Fragment {

    @BindView(R.id.plants_recycler_view)
    RecyclerView plantsRecyclerView;
    @BindView(R.id.selected_planet_text_view)
    TextView selectedPlanetTextView;
    @BindView(R.id.search_edit_text)
    EditText searchView;

    private String selectedPlanet = "";

    public SurveyQuestion1Fragment() {
        // Required empty public constructor
    }

    public static SurveyQuestion1Fragment newInstance() {
        SurveyQuestion1Fragment fragment = new SurveyQuestion1Fragment();
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
        View view = inflater.inflate(R.layout.q4_survey_layout, container, false);
        ButterKnife.bind(this, view);
        setViews();
        return view;
    }

    private void setViews() {
        List<Planet> plantsList = new ArrayList<>();
        List<String> plantsNames = Arrays.asList(getResources().getStringArray(R.array.plants_names));
        List<String> plantsImages = Arrays.asList(getResources().getStringArray(R.array.plants_images));

        int i = 0;
        for (String planetName : plantsNames) {
            plantsList.add(new Planet(planetName, plantsImages.get(i)));
            i++;
        }

        PlanetsAdapter planetsAdapter = new PlanetsAdapter(plantsList, selectedPlanet -> {
            SurveyQuestion1Fragment.this.selectedPlanet = selectedPlanet;
            selectedPlanetTextView.setVisibility(View.VISIBLE);
            selectedPlanetTextView.setText(selectedPlanet);
        });
        plantsRecyclerView.setAdapter(planetsAdapter);
        plantsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                planetsAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public RequestSurveyQuestion getAnswer() {
        if (selectedPlanet.isEmpty()) {
            return null;
        } else {
            RequestSurveyQuestion question = new RequestSurveyQuestion();
            question.setId(RequestSurveyQuestion.SurveyQuestionId.Q4.id);
            question.setQuestion(RequestSurveyQuestion.SurveyQuestionId.Q4.question);
            question.setAnswer(Collections.singletonList(selectedPlanet));
            return question;
        }
    }
}