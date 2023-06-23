package com.example.android_weather_app;

public class Weather {
    private long sunrise, sunset;
    private int weather_id;
    private double wind_direction;
    private String icon, updated_at, desc, temp, temp_min, temp_max, pressure, wind_speed, humidity;

    public Weather() {
        this.sunrise = 0;
        this.sunset = 0;
        this.weather_id = 0;
        this.wind_direction = 0.0;
        this.icon = "";
        this.updated_at = "";
        this.desc = "";
        this.temp = "";
        this.temp_min = "";
        this.temp_max = "";
        this.pressure = "";
        this.wind_speed = "";
        this.humidity = "";
    }

    public Weather(long sunrise, long sunset, int weather_id, double wind_direction, String icon, String updated_at, String desc, String temp, String temp_min, String temp_max, String pressure, String wind_speed, String humidity) {
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.weather_id = weather_id;
        this.wind_direction = wind_direction;
        this.icon = icon;
        this.updated_at = updated_at;
        this.desc = desc;
        this.temp = temp;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
        this.pressure = pressure;
        this.wind_speed = wind_speed;
        this.humidity = humidity;
    }

    public long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }

    public int getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(int weather_id) {
        this.weather_id = weather_id;
    }

    public double getWind_direction() {
        return wind_direction;
    }

    public void setWind_direction(double wind_direction) {
        this.wind_direction = wind_direction;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(String temp_min) {
        this.temp_min = temp_min;
    }

    public String getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(String temp_max) {
        this.temp_max = temp_max;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(String wind_speed) {
        this.wind_speed = wind_speed;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
}
