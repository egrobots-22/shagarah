package com.egrobots.shagarah.data.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.encoders.annotations.Encodable;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Request {

    private String id;
    private String userId;
    private List<Image> images;
    private String status;
    private long timestamp;
    private String audioQuestion;
    private String textQuestion;
    private QuestionAnalysis questionAnalysis;

    public Request() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Image> getImages() {
        return images;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Exclude
    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("E - d MMM, yyyy", new Locale("ar"));
        return formatter.format(timestamp);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Exclude
    public String getShownStatus() {
        if (status.equals(RequestStatus.ANSWERED.toString())) {
            return "تم الرد";
        } else {
            return "تحت المراجعة";
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAudioQuestion() {
        return audioQuestion;
    }

    public void setAudioQuestion(String audioQuestion) {
        this.audioQuestion = audioQuestion;
    }

    public String getTextQuestion() {
        return textQuestion;
    }

    public void setTextQuestion(String textQuestion) {
        this.textQuestion = textQuestion;
    }

    public QuestionAnalysis getQuestionAnalysis() {
        return questionAnalysis;
    }

    public void setQuestionAnalysis(QuestionAnalysis questionAnalysis) {
        this.questionAnalysis = questionAnalysis;
    }

    public enum RequestStatus {
        ANSWERED,
        IN_PROGRESS
    }

}
