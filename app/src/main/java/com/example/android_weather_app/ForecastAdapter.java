package com.example.android_weather_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<DayViewHolder> {

    Context context;
    ArrayList<Weather> days;

    public ForecastAdapter(Context context, ArrayList<Weather> days) {
        this.context = context;
        this.days = days;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DayViewHolder(LayoutInflater.from(context).inflate(R.layout.small_day_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        holder.day_name.setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date(days.get(position).getUpdated_at() * 1000)));
        holder.temp.setText(days.get(position).getTemp());
        OWM.setIcon_link(days.get(position).getIcon());
        Picasso.with(context).load(OWM.getIcon_link()).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return days.size();
    }
}
