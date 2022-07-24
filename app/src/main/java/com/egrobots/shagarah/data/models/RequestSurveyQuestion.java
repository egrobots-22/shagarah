package com.egrobots.shagarah.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class RequestSurveyQuestion implements Parcelable {

    private String question;
    private List<String> answer;

    public RequestSurveyQuestion() {
    }

    protected RequestSurveyQuestion(Parcel in) {
        question = in.readString();
        answer = in.createStringArrayList();
    }

    public static final Creator<RequestSurveyQuestion> CREATOR = new Creator<RequestSurveyQuestion>() {
        @Override
        public RequestSurveyQuestion createFromParcel(Parcel in) {
            return new RequestSurveyQuestion(in);
        }

        @Override
        public RequestSurveyQuestion[] newArray(int size) {
            return new RequestSurveyQuestion[size];
        }
    };

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeStringList(answer);
    }
}
