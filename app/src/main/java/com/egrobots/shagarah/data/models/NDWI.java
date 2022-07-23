package com.egrobots.shagarah.data.models;

public class NDWI {

    private String status;
    private double value;

    public NDWI(double value) {
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getStatus() {
        if (value >= -1 && value <= -.08) {
            return "منطقة جافة";
        } else if (value > -0.8 && value <= -0.4) {
            return "منطقة مغطاة بالظل";
        } else if (value > -.04 && value <= -0.2) {
            return "منطقة مبللة";
        } else if (value > -0.2 && value <= 0.4) {
            return "منطقة ذات محتوى مائي منخفض";
        } else if (value > 0.4 && value <= 0.6) {
            return "منطقة ذات محتوى مائي متوسط";
        } else if (value > 0.6 && value <= 0.8) {
            return "منطقة ذات محتوى مائي عالي";
        } else if (value > 0.8 && value <= 1) {
            return "منطقة ذات محتوى مائي مرتفع جدا";
        } else
            return "حالة غير معروفة";
    }
}
