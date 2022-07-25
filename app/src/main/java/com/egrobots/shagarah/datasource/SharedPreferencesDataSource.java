package com.egrobots.shagarah.datasource;

import android.content.SharedPreferences;


import com.egrobots.shagarah.utils.Constants;

import javax.inject.Inject;

public class SharedPreferencesDataSource {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    @Inject
    public SharedPreferencesDataSource(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        editor = sharedPreferences.edit();
    }

    public void saveUserIdOnDevice(String userId) {
        editor.putString(Constants.USER_ID, userId);
        editor.apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(Constants.USER_ID, null);
    }

    public String getDeviceToken() {
        return sharedPreferences.getString(Constants.DEVICE_TOKEN, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(Constants.USER_NAME, null);
    }
}
