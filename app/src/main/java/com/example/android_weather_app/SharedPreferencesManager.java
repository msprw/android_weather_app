package com.example.android_weather_app;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String APP_PREFS = "AppPrefsFile";
    private SharedPreferences preferences;
    private static SharedPreferencesManager instance;

    private SharedPreferencesManager(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesManager getInstance(Context context){
        if(instance == null)
            instance = new SharedPreferencesManager(context);

        return instance;
    }

    public void setAPIKey(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("api_key", key);
        editor.apply();
    }

    public String getAPIKey() {
        return preferences.getString("api_key", "");
    }
}
