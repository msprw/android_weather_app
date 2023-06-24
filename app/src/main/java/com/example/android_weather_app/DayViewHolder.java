package com.example.android_weather_app;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DayViewHolder extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView day_name;
    TextView temp;

    public DayViewHolder(@NonNull View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.ic_small_icon);
        day_name = itemView.findViewById(R.id.id_small_day_name);
        temp = itemView.findViewById(R.id.id_small_temp);
    }
}
