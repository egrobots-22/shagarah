package com.egrobots.shagarah.data.models;

public class NDVI {

    private String status;
    private String desc;
    private double value;

    public NDVI(double value) {
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getStatus() {
        if (value >= -1 && value <= 0) {
            return "نبات ميت";
        } else if (value > 0 && value <= 0.33) {
            return "نبات مصاب";
        } else if (value > 0.33 && value <= 0.66) {
            return "نبات شبه سليم";
        } else if (value > 0.66 && value <= 1) {
            return "نبات سليم";
        } else {
            return "حالة غير معروفة";
        }
    }

    public String getDesc() {
        if (value >= -1 && value <= 0) {
            return "نبات غير حي";
        } else if (value > 0 && value <= 0.33) {
            return "نبات يحتاج تدخل";
        } else if (value > 0.33 && value <= 0.66) {
            return "نبات في بداية الإصابة";
        } else if (value > 0.66 && value <= 1) {
            return "نبات سليم";
        } else {
            return "";
        }
    }
}
