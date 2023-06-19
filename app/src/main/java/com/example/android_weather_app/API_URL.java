package com.example.android_weather_app;

public class API_URL {
    private static String city_link;
    private String weather_link;

    public API_URL() {
        this.city_link = "";
        this.weather_link = "https://api.openweathermap.org/data/2.5/onecall?exclude=minutely&units=metric&lat="
                + City.lat + "&lon=" + City.lon + "&appid=eeb8b40367eee691683e5a079e2fa695";
    }

    public static String getCity_link() {
        return city_link;
    }

    public String getWeather_link() {
        return weather_link;
    }

    public static void setCity_link(String cityName) {
        city_link = "https://api.openweathermap.org/data/2.5/weather?&q=" + cityName + "&appid=eeb8b40367eee691683e5a079e2fa695";

    }

    public void setWeather_link(String weather_link) {
        this.weather_link = weather_link;
    }
}
