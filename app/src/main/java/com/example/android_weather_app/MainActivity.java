package com.example.android_weather_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private long update_time, sunrise, sunset = 0;
    private int weather_id = 0;
    private String updated_at, desc, temp, temp_min, temp_max, pressure, wind_speed, humidity = "";

//    private EditText

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getCityLatLon("Zabrze");
        EditText editText = findViewById(R.id.id_searched_city);
//
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    // Wywołaj funkcję lub wykonaj akcję po naciśnięciu przycisku Enter

                    return true;
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = editText.getText().toString();
                Toast.makeText(getApplicationContext(), "City: "+text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
            // Implementacja interfejsu TextWatcher
        });
    }


    private void getCityLatLon(String cityName)
    {
        City.name = cityName;
        API_URL.setCity_link(cityName);
        Toast.makeText(this, API_URL.getCity_link(), Toast.LENGTH_SHORT).show();
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL.getCity_link().toString(), null, response -> {
            try {
                City.lat = response.getJSONObject("coord").getString("lat");
                City.lon = response.getJSONObject("coord").getString("lon");
                Toast.makeText(this, "City: lat: "+City.lat+" lon: "+City.lon, Toast.LENGTH_SHORT).show();
                getCurrentWeather(cityName);
                // After the successfully city search the cityEt(editText) is Empty.
                //binding.layout.cityEt.setText("");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "City not found!", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }

    private void searchCity(String cityName) {
        if (cityName == null || cityName.isEmpty()) {
            Toast.makeText(this, "Please enter a city", Toast.LENGTH_SHORT).show();
        } else {
            getCityLatLon(cityName);
        }
    }

    @SuppressLint("DefaultLocale")
    private void getCurrentWeather(String cityName)
    {
        API_URL url = new API_URL();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
         JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url.getWeather_link(), null, response -> {
            try{
                City.name = cityName;
                update_time = response.getJSONObject("current").getLong("dt");
                updated_at = new SimpleDateFormat("EEEE hh:mm a", Locale.ENGLISH).format(new Date(update_time * 1000));
                sunrise = response.getJSONArray("daily").getJSONObject(0).getLong("sunrise");
                sunset = response.getJSONArray("daily").getJSONObject(0).getLong("sunset");
                desc = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("main");

                temp = String.valueOf(Math.round(response.getJSONObject("current").getDouble("temp")));
                temp_min = String.format("%.0f", response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getDouble("min"));
                temp_max = String.format("%.0f", response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getDouble("max"));
                pressure = response.getJSONArray("daily").getJSONObject(0).getString("pressure");
                wind_speed = response.getJSONArray("daily").getJSONObject(0).getString("wind_speed");
                humidity = response.getJSONArray("daily").getJSONObject(0).getString("humidity");
                UpdateInfo();
                Toast.makeText(this, "Temp: "+temp+" desc: "+desc, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, null);
        requestQueue.add(jsonObjectRequest);
    }
    private void UpdateInfo()
    {
        TextView city_txt = findViewById(R.id.id_city_name);
        TextView wind_txt = findViewById(R.id.id_wind);
        TextView temp_txt = findViewById(R.id.id_temp);
        TextView temp_min_txt = findViewById(R.id.id_min_temp);
        TextView temp_max_txt = findViewById(R.id.id_max_temp);
        TextView pressure_txt = findViewById(R.id.id_pressure);
        TextView humid_txt = findViewById(R.id.id_humidity);
        TextView condition_txt = findViewById(R.id.id_condition);
        TextView updated_at_txt = findViewById(R.id.updated_at_tv);

        city_txt.setText(City.name);
        wind_txt.setText(wind_speed+"m/s");
        temp_txt.setText(temp + "\u2103");
        temp_min_txt.setText(temp_min +"\u2103");
        temp_max_txt.setText(temp_max +"\u2103");
        pressure_txt.setText(pressure + "hPa");
        humid_txt.setText(humidity + "%");
        condition_txt.setText(desc);
        updated_at_txt.setText(updated_at);
//        id_wind
//        updated_at_tv
    }
}