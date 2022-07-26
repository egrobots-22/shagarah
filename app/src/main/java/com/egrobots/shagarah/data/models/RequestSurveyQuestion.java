package com.egrobots.shagarah.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class RequestSurveyQuestion implements Parcelable {

    private int id;
    private String question;
    private List<String> answer;

    public RequestSurveyQuestion() {
    }

    protected RequestSurveyQuestion(Parcel in) {
        id = in.readInt();
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
        dest.writeInt(id);
        dest.writeString(question);
        dest.writeStringList(answer);
    }

    public enum SurveyQuestionId{
        Q1(1, "ما تم رشه في الشهر الماضي ؟"),
        Q2(2, "كم من المحاصيل الخاصة بك وتعاني من هذه المشكلة؟"),
        Q3(3, "أي جزء من النبات يظهر في الصورة؟"),
        Q4(4, "اختيار المحاصيل الخاصة بك");

        public int id;
        public String question;
        SurveyQuestionId(int i, String q) {
            id = i;
            question = q;
        }
    }
}
