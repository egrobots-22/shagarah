package com.egrobots.shagarah.data.models;

public class QuestionAnalysis {

    private String id;
    private String treeType;
    private String treeCode;
    private String treeStatus;
    private String diseases;
    private String tasmeed;
    private String alrai;
    private String operations;
    private String elthemar;

    public QuestionAnalysis() {
    }

    public QuestionAnalysis(String treeType
            , String treeCode
            , String treeStatus
            , String diseases
            , String tasmeed
            , String alrai
            , String operations
            , String elthemar) {
        this.treeType = treeType;
        this.treeCode = treeCode;
        this.treeStatus = treeStatus;
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

    public String getTreeCode() {
        return treeCode;
    }

    public void setTreeCode(String treeCode) {
        this.treeCode = treeCode;
    }

    public String getTreeStatus() {
        return treeStatus;
    }

    public void setTreeStatus(String treeStatus) {
        this.treeStatus = treeStatus;
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
}
