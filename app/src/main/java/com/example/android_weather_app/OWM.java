package com.example.android_weather_app;

public class OWM {
    private static String city_link;
    private static String forecast_link;
    private static String icon_link;

    public OWM() {
        this.city_link = "";
        this.forecast_link = "";
        this.icon_link = "";
    }

    public static String getCity_link() {
        return city_link;
    }
    public static String getForecast_link() {
        return forecast_link;
    }
    public static String getIcon_link() {
        return icon_link;
    }

    public static void setCity_link(String cityName, String key) {
        city_link = "https://api.openweathermap.org/data/2.5/weather?&lang=pl&units=metric&q=" + cityName + "&appid=" + key;
    }

    public static void setIcon_link(String icon_id) {
        icon_link = "https://openweathermap.org/img/wn/"+icon_id+"@2x.png";
    }

    public static void setForecast_link(String lat, String lon, String key) {
        forecast_link = "https://api.openweathermap.org/data/2.5/onecall?exclude=minutely&lang=pl&units=metric&lat="
                + lat + "&lon=" + lon + "&appid=" + key;
    }
}
