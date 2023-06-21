package com.example.android_weather_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private long update_time, sunrise, sunset = 0;
    private int weather_id = 0;
    private String icon, updated_at, desc, temp, temp_min, temp_max, pressure, wind_speed, humidity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText editText = findViewById(R.id.id_searched_city);
        ImageView refresh = findViewById(R.id.id_refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
                refresh.startAnimation(rotation);
                searchCity(City.name);
                refresh.clearAnimation();
                Toast.makeText(getApplicationContext(), "Dane zaktualizowane o " + updated_at, Toast.LENGTH_SHORT).show();
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    // Process the city after clicking Enter
                    String text = editText.getText().toString();
                    getCityLatLon(text);
                    return true;
                }
                return false;
            }
        });
    }


    private void getCityLatLon(String cityName)
    {
        City.name = cityName;
        OWM.setCity_link(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
//        Toast.makeText(this, "Getcitylatlon", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "city: "+City.name, Toast.LENGTH_SHORT).show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, OWM.getCity_link().toString(), null, response -> {
            try {
                City.lat = response.getJSONObject("coord").getString("lat");
                City.lon = response.getJSONObject("coord").getString("lon");

                //After a valid city has been passed, clear the search bar and hide the keyboard
                EditText editText = findViewById(R.id.id_searched_city);
                editText.setText("");

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                getCurrentWeather(City.name);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "Nie znaleziono takiego miasta!", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }

    private void searchCity(String cityName) {
        if (cityName == null || cityName.isEmpty()) {
            Toast.makeText(this, "Proszę wpisz miasto", Toast.LENGTH_SHORT).show();
        } else {
            getCityLatLon(cityName);
        }
    }

    @SuppressLint("DefaultLocale")
    private void getCurrentWeather(String cityName)
    {
        OWM url = new OWM();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
         JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url.getWeather_link(), null, response -> {
            try{
                City.name = cityName;
                update_time = response.getJSONObject("current").getLong("dt");
                updated_at = new SimpleDateFormat("EEEE hh:mm a", Locale.getDefault()).format(new Date(update_time * 1000));
                sunrise = response.getJSONArray("daily").getJSONObject(0).getLong("sunrise");
                sunset = response.getJSONArray("daily").getJSONObject(0).getLong("sunset");
                desc = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("description");
                icon = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("icon");
                OWM.setIcon_link(icon);

                temp = String.format("%.1f", response.getJSONObject("current").getDouble("temp"));
                temp_min = String.format("%.1f", response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getDouble("min"));
                temp_max = String.format("%.1f", response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getDouble("max"));
                pressure = response.getJSONArray("daily").getJSONObject(0).getString("pressure");
                wind_speed = response.getJSONArray("daily").getJSONObject(0).getString("wind_speed");
                humidity = response.getJSONArray("daily").getJSONObject(0).getString("humidity");
                UpdateInfo();
//                Toast.makeText(this, "Updated at: "+ updated_at, Toast.LENGTH_SHORT).show();
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
        ImageView condition_img = findViewById(R.id.id_condition_img);

        city_txt.setText(City.name);
        wind_txt.setText(wind_speed+"m/s");
        temp_txt.setText(temp + "\u2103");
        temp_min_txt.setText(temp_min +"\u2103");
        temp_max_txt.setText(temp_max +"\u2103");
        pressure_txt.setText(pressure + "hPa");
        humid_txt.setText(humidity + "%");
        condition_txt.setText(desc.substring(0,1).toUpperCase()+ desc.substring(1));
        updated_at_txt.setText(updated_at);
        Picasso.with(this).load(OWM.getIcon_link()).into(condition_img);

    }
}