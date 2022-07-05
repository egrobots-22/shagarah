package com.egrobots.shagarah.data.models;

import com.google.firebase.database.Exclude;

import java.util.List;
import java.util.Random;

public class QuestionAnalysis {

    private String id;
    private String treeType;
    private String treeCategory;
    private String treeCode;
    private List<String> diseasesList;
    private String tasmeed;
    private String alrai;
    private String operations;
    private String elthemar;
    private float rating;

    public QuestionAnalysis() {
    }

    public QuestionAnalysis(String treeType
            , String treeCategory
            , List<String> diseasesList
            , String tasmeed
            , String alrai
            , String operations
            , String elthemar) {
        this.treeType = treeType;
        this.treeCategory = treeCategory;
        this.treeCode = String.valueOf(new Random().nextInt(1000000));
        this.diseasesList = diseasesList;
        this.tasmeed = tasmeed;
        this.alrai = alrai;
        this.operations = operations;
        this.elthemar = elthemar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTreeType() {
        return treeType;
    }

    public void setTreeType(String treeType) {
        this.treeType = treeType;
    }

    public String getTreeCategory() {
        return treeCategory;
    }

    public void setTreeCategory(String treeCategory) {
        this.treeCategory = treeCategory;
    }

    public String getTreeCode() {
        return treeCode;
    }

    public void setTreeCode(String treeCode) {
        this.treeCode = treeCode;
    }

    public List<String> getDiseasesList() {
        return diseasesList;
    }

    @Exclude
    public String getDiseasesListAsString() {
        StringBuilder diseasesListAsString = new StringBuilder();
        for (String disease : diseasesList) {
            diseasesListAsString.append(disease);
            diseasesListAsString.append(" - ");
        }
        return diseasesListAsString.toString();
    }

    public void setDiseasesList(List<String> diseasesList) {
        this.diseasesList = diseasesList;
    }

    public String getTasmeed() {
        return tasmeed;
    }

    public void setTasmeed(String tasmeed) {
        this.tasmeed = tasmeed;
    }

    public String getAlrai() {
        return alrai;
    }

    public void setAlrai(String alrai) {
        this.alrai = alrai;
    }

    public String getOperations() {
        return operations;
    }

    public void setOperations(String operations) {
        this.operations = operations;
    }

    public String getElthemar() {
        return elthemar;
    }

    public void setElthemar(String elthemar) {
        this.elthemar = elthemar;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
