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
    private String requestUserId;
    private List<Image> images;
    private int status;
    private long timestamp;
    private long timestampToOrder;
    private String audioQuestion;
    private String textQuestion;
    private QuestionAnalysis questionAnalysis;
    private String flag;

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

    public String getRequestUserId() {
        return requestUserId;
    }

    public void setRequestUserId(String requestUserId) {
        this.requestUserId = requestUserId;
    }

    @Exclude
    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("E - d MMM, yyyy - hh:mm aa", new Locale("ar"));
        return formatter.format(timestamp);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestampToOrder() {
        return timestampToOrder;
    }

    public void setTimestampToOrder(long timestampToOrder) {
        this.timestampToOrder = timestampToOrder;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Exclude
    public String getShownStatus() {
        if (status == RequestStatus.ANSWERED.value) {
            return "تم الرد";
        } else {
            return "تحت المراجعة";
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public enum RequestStatus {
        ANSWERED(1),
        IN_PROGRESS(0);

        public int value;
        RequestStatus(int i) {
            value = i;
        }
    }

}
