package com.example.android_weather_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

    private TextView city_txt;
    private TextView wind_txt;
    private TextView temp_txt;
    private TextView temp_min_txt;
    private TextView temp_max_txt;
    private TextView pressure_txt;
    private TextView humid_txt;
    private TextView condition_txt;
    private TextView updated_at_txt;
    private TextView pressure_static;
    private TextView wind_static;
    private TextView humidity_static;
    private TextView updated_at_static;
    private ImageView condition_img;
    private ImageView pressure_img;
    private ImageView wind_img;
    private ImageView humidity_img;
    private ImageView temp_min_img;
    private ImageView temp_max_img;
    private RelativeLayout temp_layout;
    private LinearLayout air_layout;

    private EditText editText;
    private ImageView refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitializeUI();

        SwitchUIVisibility(false);

        SetupListeners();

    }


    private void getCurrentWeather(String cityName)
    {
        OWM.setCity_link(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, OWM.getCity_link().toString(), null, response -> {
            try {
                City.lat = response.getJSONObject("coord").getString("lat");
                City.lon = response.getJSONObject("coord").getString("lon");

                sunrise = response.getJSONObject("sys").getLong("sunrise");
                sunset = response.getJSONObject("sys").getLong("sunset");

                wind_speed = response.getJSONObject("wind").getString("speed");
                pressure = response.getJSONObject("main").getString("pressure");
                humidity = response.getJSONObject("main").getString("humidity");
                temp = String.format("%.1f", response.getJSONObject("main").getDouble("temp"));
                temp_min = String.format("%.1f", response.getJSONObject("main").getDouble("temp_min"));
                temp_max = String.format("%.1f", response.getJSONObject("main").getDouble("temp_max"));
                desc = response.getJSONArray("weather").getJSONObject(0).getString("description");
                icon = response.getJSONArray("weather").getJSONObject(0).getString("icon");
                //fetch an icon from OWM
                OWM.setIcon_link(icon);

                update_time = (System.currentTimeMillis());
                updated_at = new SimpleDateFormat("EEEE hh:mm a", Locale.getDefault()).format(new Date(update_time));

                //After a valid city has been passed, clear the search bar and hide the keyboard
                EditText editText = findViewById(R.id.id_searched_city);
                editText.setText("");

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                City.name = cityName;
                UpdateInfo();
                SwitchUIVisibility(true);

//                getCurrentWeather(City.name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "Nie znaleziono takiego miasta!", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }

    private boolean searchCity(String cityName) {
        if (cityName == null || cityName.isEmpty()) {
            Toast.makeText(this, "Proszę podaj miasto", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            getCurrentWeather(cityName);
            return true;
        }
    }

//    @SuppressLint("DefaultLocale")
//    private void getCurrentWeather(String cityName)
//    {
//        OWM url = new OWM();
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//         JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url.getWeather_link(), null, response -> {
//            try{
//                update_time = (System.currentTimeMillis() / 1000);
//                updated_at = new SimpleDateFormat("EEEE hh:mm a", Locale.getDefault()).format(new Date(update_time * 1000));
//                sunrise = response.getJSONArray("daily").getJSONObject(0).getLong("sunrise");
//                sunset = response.getJSONArray("daily").getJSONObject(0).getLong("sunset");
//                desc = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("description");
//                icon = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("icon");
//                OWM.setIcon_link(icon);
//
//                temp = String.format("%.1f", response.getJSONObject("current").getDouble("temp"));
//                temp_min = String.format("%.1f", response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getDouble("min"));
//                temp_max = String.format("%.1f", response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getDouble("max"));
//                pressure = response.getJSONArray("daily").getJSONObject(0).getString("pressure");
//                wind_speed = response.getJSONArray("daily").getJSONObject(0).getString("wind_speed");
//                humidity = response.getJSONArray("daily").getJSONObject(0).getString("humidity");
//                City.name = cityName;
//                UpdateInfo();
//                SwitchUIVisibility(true);
////                Toast.makeText(this, "Updated at: "+ updated_at, Toast.LENGTH_SHORT).show();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }, error -> Toast.makeText(this, "Wystąpił błąd przy pobieraniu danych pogodowych!", Toast.LENGTH_SHORT).show());
//        requestQueue.add(jsonObjectRequest);
//    }
    private void UpdateInfo()
    {
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

    private void InitializeUI(){
        temp_layout = findViewById(R.id.temp_layout);
        air_layout = findViewById(R.id.air_layout);
        editText = findViewById(R.id.id_searched_city);
        refresh = findViewById(R.id.id_refresh);
        city_txt = findViewById(R.id.id_city_name);
        wind_txt = findViewById(R.id.id_wind);
        temp_txt = findViewById(R.id.id_temp);
        temp_min_txt = findViewById(R.id.id_min_temp);
        temp_max_txt = findViewById(R.id.id_max_temp);
        pressure_txt = findViewById(R.id.id_pressure);
        humid_txt = findViewById(R.id.id_humidity);
        condition_txt = findViewById(R.id.id_condition);
        updated_at_txt = findViewById(R.id.updated_at_tv);
        condition_img = findViewById(R.id.id_condition_img);
        pressure_img = findViewById(R.id.ic_pressure);
        wind_img = findViewById(R.id.ic_wind);
        humidity_img = findViewById(R.id.ic_humidity);
        temp_min_img = findViewById(R.id.ic_min_temp);
        temp_max_img = findViewById(R.id.ic_max_temp);
        pressure_static = findViewById(R.id.id_pressure_static);
        wind_static = findViewById(R.id.id_wind_static);
        humidity_static = findViewById(R.id.id_humidity_static);
        updated_at_static = findViewById(R.id.id_updated_at_static);
    }
    private void SwitchUIVisibility(boolean state){
        if(!state) {
            temp_layout.setVisibility(View.INVISIBLE);
            air_layout.setVisibility(View.INVISIBLE);
            city_txt.setVisibility(View.INVISIBLE);
            wind_txt.setVisibility(View.INVISIBLE);
            temp_txt.setVisibility(View.INVISIBLE);
            temp_min_txt.setVisibility(View.INVISIBLE);
            temp_max_txt.setVisibility(View.INVISIBLE);
            pressure_txt.setVisibility(View.INVISIBLE);
            humid_txt.setVisibility(View.INVISIBLE);
            condition_txt.setVisibility(View.INVISIBLE);
            updated_at_txt.setVisibility(View.INVISIBLE);
            condition_img.setVisibility(View.INVISIBLE);
            pressure_img.setVisibility(View.INVISIBLE);
            wind_img.setVisibility(View.INVISIBLE);
            humidity_img.setVisibility(View.INVISIBLE);
            temp_min_img.setVisibility(View.INVISIBLE);
            temp_max_img.setVisibility(View.INVISIBLE);
            pressure_static.setVisibility(View.INVISIBLE);
            wind_static.setVisibility(View.INVISIBLE);
            humidity_static.setVisibility(View.INVISIBLE);
            updated_at_static.setVisibility(View.INVISIBLE);
        } else {
            temp_layout.setVisibility(View.VISIBLE);
            air_layout.setVisibility(View.VISIBLE);
            city_txt.setVisibility(View.VISIBLE);
            wind_txt.setVisibility(View.VISIBLE);
            temp_txt.setVisibility(View.VISIBLE);
            temp_min_txt.setVisibility(View.VISIBLE);
            temp_max_txt.setVisibility(View.VISIBLE);
            pressure_txt.setVisibility(View.VISIBLE);
            humid_txt.setVisibility(View.VISIBLE);
            condition_txt.setVisibility(View.VISIBLE);
            updated_at_txt.setVisibility(View.VISIBLE);
            condition_img.setVisibility(View.VISIBLE);
            pressure_img.setVisibility(View.VISIBLE);
            wind_img.setVisibility(View.VISIBLE);
            humidity_img.setVisibility(View.VISIBLE);
            temp_min_img.setVisibility(View.VISIBLE);
            temp_max_img.setVisibility(View.VISIBLE);
            pressure_static.setVisibility(View.VISIBLE);
            wind_static.setVisibility(View.VISIBLE);
            humidity_static.setVisibility(View.VISIBLE);
            updated_at_static.setVisibility(View.VISIBLE);
        }
    }

    private void SetupListeners(){
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
                refresh.startAnimation(rotation);
                if(searchCity(City.name)){
//                    UpdateInfo();
                    Toast.makeText(getApplicationContext(), "Dane zostały zaktualizowane!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Nie udało się zaktualizować danych", Toast.LENGTH_SHORT).show();
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh.clearAnimation();
                    }
                }, 1000); //clear animation after a second
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    // Process the city after clicking Enter
                    String text = editText.getText().toString();
                    getCurrentWeather(text);
//                    SwitchUIVisibility(true);
                    return true;
                }
                return false;
            }
        });
    }
}