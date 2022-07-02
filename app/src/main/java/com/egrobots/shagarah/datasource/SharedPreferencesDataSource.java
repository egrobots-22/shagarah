package com.egrobots.shagarah.datasource;

import android.content.SharedPreferences;


import com.egrobots.shagarah.utils.Constants;

import javax.inject.Inject;

public class SharedPreferencesDataSource {

    private final SharedPreferences sharedPreferences;

    @Inject
    public SharedPreferencesDataSource(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }


    public String getDeviceToken() {
        return sharedPreferences.getString(Constants.DEVICE_TOKEN, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(Constants.USER_NAME, null);
    }
}
