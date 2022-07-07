package com.egrobots.shagarah.data.models;

import com.google.firebase.database.Exclude;

import java.util.List;
import java.util.Random;

public class QuestionAnalysis {

    private String id;
    private String treeType;
    private String treeCategory;
    private String treeCode;
    private List<String> alafat;
    private List<String> amrad_3odwia;
    private List<String> amrad_bikteria;
    private List<String> amrad_fetrya;
    private List<String> amrad_viruses;
    private String tasmeed;
    private String alrai;
    private String operations;
    private String elthemar;
    private String questionAnswer;
    private float rating;

    public QuestionAnalysis() {
    }

    public QuestionAnalysis(String treeType
            , String treeCategory
            , List<String> alafat
            , List<String> amrad_3odwia
            , List<String> amrad_bikteria
            , List<String> amrad_fetrya
            , List<String> amrad_viruses
            , String tasmeed
            , String alrai
            , String operations
            , String elthemar
            , String questionAnswer) {
        this.treeType = treeType;
        this.treeCategory = treeCategory;
        this.treeCode = String.valueOf(new Random().nextInt(1000000));
        this.alafat = alafat;
        this.amrad_3odwia = amrad_3odwia;
        this.amrad_bikteria = amrad_bikteria;
        this.amrad_fetrya = amrad_fetrya;
        this.amrad_viruses = amrad_viruses;
        this.tasmeed = tasmeed;
        this.alrai = alrai;
        this.operations = operations;
        this.elthemar = elthemar;
        this.questionAnswer = questionAnswer;
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

    public List<String> getAlafat() {
        return alafat;
    }

    @Exclude
    public String getAlafatAsString() {
        StringBuilder alafatListAsString = new StringBuilder();
        if (alafat != null) {
            int i = 0;
            for (String disease : alafat) {
                if (i < alafat.size() - 1) {
                    alafatListAsString.append(disease);
                    alafatListAsString.append(" - ");
                } else {
                    alafatListAsString.append(disease);
                }
                i++;
            }
        }
        return alafatListAsString.toString();
    }

    public void setAlafat(List<String> alafat) {
        this.alafat = alafat;
    }

    public List<String> getAmrad_3odwia() {
        return amrad_3odwia;
    }

    @Exclude
    public String getAmarad3odwiaAsString() {
        StringBuilder amrad3odwiaAsString = new StringBuilder();
        if (amrad_3odwia != null) {
            int i = 0;
            for (String disease : amrad_3odwia) {
                if (i < amrad_3odwia.size() - 1) {
                    amrad3odwiaAsString.append(disease);
                    amrad3odwiaAsString.append(" - ");
                } else {
                    amrad3odwiaAsString.append(disease);
                }
                i++;
            }
        }
        return amrad3odwiaAsString.toString();
    }

    public void setAmrad_3odwia(List<String> amrad_3odwia) {
        this.amrad_3odwia = amrad_3odwia;
    }

    public List<String> getAmrad_bikteria() {
        return amrad_bikteria;
    }

    @Exclude
    public String getAmradBikteriaAsString() {
        StringBuilder amradBikteriaAsString = new StringBuilder();
        if (amrad_bikteria != null) {
            int i = 0;
            for (String disease : amrad_bikteria) {
                if (i < amrad_bikteria.size() - 1) {
                    amradBikteriaAsString.append(disease);
                    amradBikteriaAsString.append(" - ");
                } else {
                    amradBikteriaAsString.append(disease);
                }
                i++;
            }
        }
        return amradBikteriaAsString.toString();
    }

    public void setAmrad_bikteria(List<String> amrad_bikteria) {
        this.amrad_bikteria = amrad_bikteria;
    }

    public List<String> getAmrad_fetrya() {
        return amrad_fetrya;
    }

    @Exclude
    public String getAmradFetryaAsString() {
        StringBuilder amradFetryaAsString = new StringBuilder();
        if (amrad_fetrya != null) {
            int i = 0;
            for (String disease : amrad_fetrya) {
                if (i < amrad_fetrya.size() - 1) {
                    amradFetryaAsString.append(disease);
                    amradFetryaAsString.append(" - ");
                } else {
                    amradFetryaAsString.append(disease);
                }
                i++;
            }
        }
        return amradFetryaAsString.toString();
    }

    public void setAmrad_fetrya(List<String> amrad_fetrya) {
        this.amrad_fetrya = amrad_fetrya;
    }

    public List<String> getAmrad_viruses() {
        return amrad_viruses;
    }

    @Exclude
    public String getAmradVirusesAsString() {
        StringBuilder virusesListAsString = new StringBuilder();
        if (amrad_viruses != null) {
            int i = 0;
            for (String disease : amrad_viruses) {
                if (i < amrad_viruses.size() - 1) {
                    virusesListAsString.append(disease);
                    virusesListAsString.append(" - ");
                } else {
                    virusesListAsString.append(disease);
                }
                i++;
            }
        }
        return virusesListAsString.toString();
    }

    public void setAmrad_viruses(List<String> amrad_viruses) {
        this.amrad_viruses = amrad_viruses;
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

    public String getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(String questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
