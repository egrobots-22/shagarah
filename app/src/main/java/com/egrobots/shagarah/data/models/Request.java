package com.egrobots.shagarah.data.models;

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
    private HashMap<String, Object> answer;

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

    public String getTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("E - d MMM, yyyy", new Locale("ar"));
        return formatter.format(timestamp);
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setImages(List<Image> images) {
        this.images = images;
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

    public HashMap<String, Object> getAnswer() {
        return answer;
    }

    public void setAnswer(HashMap<String, Object> answer) {
        this.answer = answer;
    }

    public enum RequestStatus {
        ANSWERED,
        IN_PROGRESS
    }

}
