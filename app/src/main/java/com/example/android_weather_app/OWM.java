package com.example.android_weather_app;

public class OWM {
    private static String city_link;
    private String weather_link;
    private static String icon_link;

    public OWM() {
        this.city_link = "";
        this.weather_link = "https://api.openweathermap.org/data/2.5/onecall?exclude=minutely&lang=pl&units=metric&lat="
                + City.lat + "&lon=" + City.lon + "&appid=eeb8b40367eee691683e5a079e2fa695";
        this.icon_link = "";
    }

    public static String getCity_link() {
        return city_link;
    }

    public String getWeather_link() {
        return weather_link;
    }

    public static void setCity_link(String cityName) {
        city_link = "https://api.openweathermap.org/data/2.5/weather?&lang=pl&q=" + cityName + "&appid=eeb8b40367eee691683e5a079e2fa695";

    }

    public static void setIcon_link(String icon_id)
    {
        icon_link = "https://openweathermap.org/img/wn/"+icon_id+"@2x.png";
    }

    public static String getIcon_link() {
        return icon_link;
    }

    public void setWeather_link(String weather_link) {
        this.weather_link = weather_link;
    }
}
