package com.egrobots.shagarah.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationRequest {

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA138Q-qc:APA91bGytl4G7QtYgsAf_nr1iZOiSlXFZDILkA42AFhyTG-m_UJ_bSh3O20HAgjdPxiqNO-i8q9Au2s8Z_RWXGW7BFylpQWJXcifA7-186Uzfcz1CALAxS40WSTmSz0kn8KSD2ItGlhv";
    final private String contentType = "application/json";
    static final String TAG = "NOTIFICATION TAG";

    private Context context;

    public NotificationRequest(Context context) {
        this.context = context;
    }

    public void sendNotificationRequest(String toToken) {
        String to = toToken; //topic must match with what the receiver subscribed to

        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", "اجابة سؤال");
            notificationBody.put("body", "تم اجابة السؤال الخاص بك");
            notification.put("to", to);
            notification.put("notification", notificationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
        }
        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }
}
