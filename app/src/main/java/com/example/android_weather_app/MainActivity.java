package com.example.android_weather_app;

import static android.text.TextUtils.isEmpty;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private NetworkChangeReceiver networkChangeReceiver;
    private long update_time;
    private String updated_at;
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
    private TextView sunrise_txt;
    private TextView sunset_txt;
    private ImageView condition_img;
    private ImageView pressure_img;
    private ImageView wind_img;
    private ImageView humidity_img;
    private ImageView temp_min_img;
    private ImageView temp_max_img;
    private RelativeLayout temp_layout;
    private LinearLayout air_layout;
    private LinearLayout forecast_layout;
    private ArrayList<Weather> forecast;
    private EditText editText;
    private ImageView refresh;
    private ImageView api_key;
    private Weather currentWeather = new Weather();
    private RecyclerView small_day_forecast;
    private boolean InternetConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        networkChangeReceiver = new NetworkChangeReceiver();

        InitializeUI();

        SwitchUIVisibility(false);

        SetupListeners();

        String api_key = SharedPreferencesManager.getInstance(getApplicationContext()).getAPIKey();
        if(isEmpty(api_key))
            ShowKeyDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkChangeReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterNetworkChangeReceiver();
    }

    private void registerNetworkChangeReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    private void unregisterNetworkChangeReceiver() {
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
            networkChangeReceiver = null;
        }
    }

    private void getCurrentWeather(String cityName)
    {

        OWM.setCity_link(cityName, SharedPreferencesManager.getInstance(getApplicationContext()).getAPIKey());
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, OWM.getCity_link().toString(), null, response -> {
            try {
                City.lat = response.getJSONObject("coord").getString("lat");
                City.lon = response.getJSONObject("coord").getString("lon");

                OWM.setForecast_link(City.lat, City.lon, SharedPreferencesManager.getInstance(getApplicationContext()).getAPIKey());

                currentWeather.setSunrise(response.getJSONObject("sys").getLong("sunrise"));
                currentWeather.setSunset(response.getJSONObject("sys").getLong("sunset"));

                currentWeather.setWind_speed(response.getJSONObject("wind").getString("speed"));
                currentWeather.setWind_direction(response.getJSONObject("wind").getDouble("deg"));

                currentWeather.setHumidity(response.getJSONObject("main").getString("humidity"));
                currentWeather.setPressure(response.getJSONObject("main").getString("pressure"));
                currentWeather.setTemp(String.format("%.1f", response.getJSONObject("main").getDouble("temp")));
                currentWeather.setTemp_min(String.format("%.1f", response.getJSONObject("main").getDouble("temp_min")));
                currentWeather.setTemp_max(String.format("%.1f", response.getJSONObject("main").getDouble("temp_max")));

                currentWeather.setDesc(response.getJSONArray("weather").getJSONObject(0).getString("description"));
                currentWeather.setIcon(response.getJSONArray("weather").getJSONObject(0).getString("icon"));

                //fetch an icon from OWM
                OWM.setIcon_link(currentWeather.getIcon());

                update_time = (System.currentTimeMillis());
                updated_at = new SimpleDateFormat("EEEE HH:mm", new Locale("pl", "PL")).format(new Date(update_time));

                //After a valid city has been passed, clear the search bar and hide the keyboard
                EditText editText = findViewById(R.id.id_searched_city);
                editText.setText("");

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                City.name = cityName;

                UpdateInfo();
                SwitchUIVisibility(true);
                getWeatherForecast();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "Nie znaleziono takiego miasta!", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }

    private boolean searchCity(String cityName) {
        if(networkChangeReceiver == null || !networkChangeReceiver.getConnStatus(getApplicationContext())){
            Toast.makeText(this, "Brak połączenia z internetem!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (cityName == null || cityName.isEmpty()) {
            Toast.makeText(this, "Proszę podaj miasto", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            getCurrentWeather(cityName);
            return true;
        }
    }

    @SuppressLint("DefaultLocale")
    private void getWeatherForecast()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, OWM.getForecast_link(), null, response -> {
            try{
                forecast = new ArrayList<>();
                JSONArray dayArray = response.getJSONArray("daily");

                for(int i = 1; i < dayArray.length(); i++) {

                    Weather day = new Weather();
                    JSONObject dayObject = dayArray.getJSONObject(i);

                    day.setTemp(String.format("%.1f",dayObject.getJSONObject("temp").getDouble("day")));
                    day.setTemp_min(String.format("%.1f",dayObject.getJSONObject("temp").getDouble("min")));
                    day.setTemp_max(String.format("%.1f",dayObject.getJSONObject("temp").getDouble("max")));
                    day.setUpdated_at(dayObject.getLong("dt"));
                    day.setPressure(dayObject.getString("pressure"));
                    day.setHumidity(dayObject.getString("humidity"));
                    day.setSunrise(dayObject.getLong("sunrise"));
                    day.setSunset(dayObject.getLong("sunset"));
                    day.setWind_speed(dayObject.getString("wind_speed"));
                    day.setWind_direction(dayObject.getDouble("wind_deg"));
                    day.setIcon(dayObject.getJSONArray("weather").getJSONObject(0).getString("icon"));
                    day.setDesc(dayObject.getJSONArray("weather").getJSONObject(0).getString("description"));
                    day.setWeather_id(dayObject.getJSONArray("weather").getJSONObject(0).getInt("id"));

                    forecast.add(day);
                }
                small_day_forecast.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
                small_day_forecast.setAdapter(new ForecastAdapter(getApplicationContext(), forecast));
                forecast_layout.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
                //optional toast about the error
            }
        }, error -> Toast.makeText(this, "Wystąpił błąd przy pobieraniu danych pogodowych!", Toast.LENGTH_SHORT).show());
        requestQueue.add(jsonObjectRequest);
    }
    private void UpdateInfo()
    {
        city_txt.setText(City.name);
        wind_txt.setText(currentWeather.getWind_speed() + "m/s");
        temp_txt.setText(currentWeather.getTemp() + "\u2103");
        temp_min_txt.setText(currentWeather.getTemp_min() +"\u2103");
        temp_max_txt.setText(currentWeather.getTemp_max() +"\u2103");
        pressure_txt.setText(currentWeather.getPressure() + "hPa");
        humid_txt.setText(currentWeather.getHumidity() + "%");
        condition_txt.setText(currentWeather.getDesc().substring(0,1).toUpperCase()+ currentWeather.getDesc().substring(1));
        updated_at_txt.setText(updated_at);
        wind_img.setRotation(Math.round(currentWeather.getWind_direction()));
        sunrise_txt.setText(new SimpleDateFormat("HH:mm", new Locale("pl", "PL")).format(new Date(currentWeather.getSunrise() * 1000)));
        sunset_txt.setText(new SimpleDateFormat("HH:mm", new Locale("pl", "PL")).format(new Date(currentWeather.getSunset() * 1000)));
        Picasso.with(this).load(OWM.getIcon_link()).into(condition_img);
    }

    @SuppressLint("WrongViewCast")
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
        sunrise_txt = findViewById(R.id.id_sunrise_txt);
        sunset_txt = findViewById(R.id.id_sunset_txt);
        api_key = findViewById(R.id.ic_api_key);
        small_day_forecast = findViewById(R.id.forecast);
        forecast_layout = findViewById(R.id.id_forecast_bar);
    }
    private void SwitchUIVisibility(boolean state){
        if(!state) {
            temp_layout.setVisibility(View.INVISIBLE);
            air_layout.setVisibility(View.INVISIBLE);
            forecast_layout.setVisibility(View.INVISIBLE);
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

    private void ShowKeyDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Klucz API OpenWeather");
        builder.setMessage("Aby móc korzystać z aplikacji, konieczne jest wprowadzenie swojego klucza API, który mozna wygenerować udając się na stronę OWM");

        final EditText editText = new EditText(MainActivity.this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT); // Typ pola tekstowego (opcjonalne)
        builder.setView(editText);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String userInput = editText.getText().toString();
                if(userInput.length() == 32) {
                    SharedPreferencesManager.getInstance(getApplicationContext()).setAPIKey(userInput);
                    Toast.makeText(getApplicationContext(), "klucz API został zapisany!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Nieprawidłowy klucz API!", Toast.LENGTH_SHORT).show();
                    ShowKeyDialog();
                }
            }
        });

        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        dialog.show();
    }

    private void SetupListeners(){
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
                refresh.startAnimation(rotation);
                if(searchCity(City.name)){
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

        api_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Klucz API OpenWeather");
                // create an input in the dialog
                final EditText editText = new EditText(MainActivity.this);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(editText);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userInput = editText.getText().toString();
                        if(userInput.length() == 32) {
                            SharedPreferencesManager.getInstance(getApplicationContext()).setAPIKey(userInput);
                            Toast.makeText(getApplicationContext(), "klucz API został zapisany!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Nieprawidłowy klucz API!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    // Process the city after clicking Enter
                    String text = editText.getText().toString();
                    searchCity(text);
                    return true;
                }
                return false;
            }
        });

    }
}