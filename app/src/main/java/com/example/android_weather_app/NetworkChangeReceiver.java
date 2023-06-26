package com.example.android_weather_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (isInternetConnected(context)) {
            Toast.makeText(context, "Połączono z internetem", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    public boolean getConnStatus(Context context) {
        return isInternetConnected(context);
    }
}
