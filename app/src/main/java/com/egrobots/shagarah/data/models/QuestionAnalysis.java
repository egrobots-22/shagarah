package com.egrobots.shagarah.data.models;

import java.util.Random;

public class QuestionAnalysis {

    private String id;
    private String treeType;
    private String treeCategory;
    private String treeCode;
    private String diseases;
    private String tasmeed;
    private String alrai;
    private String operations;
    private String elthemar;
    private float rating;

    public QuestionAnalysis() {
    }

    public QuestionAnalysis(String treeType
            , String treeCategory
            , String diseases
            , String tasmeed
            , String alrai
            , String operations
            , String elthemar) {
        this.treeType = treeType;
        this.treeCategory = treeCategory;
        this.treeCode = String.valueOf(new Random().nextInt(1000000));
        this.diseases = diseases;
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

    public String getDiseases() {
        return diseases;
    }

    public void setDiseases(String diseases) {
        this.diseases = diseases;
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
