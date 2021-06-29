package com.example.ourmoody;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.PendingIntent.getActivity;
import static com.example.ourmoody.R.string.place_not_found;
import static java.lang.String.valueOf;
import static java.security.AccessController.getContext;

public class WeatherFragment {
    private TextView cityField;
    private TextView weatherIcon;
    private TextView updatedField;
    private TextView currentTemperatureField;
    private TextView detailsField;

    Handler handler;

    public WeatherFragment(){
        handler = new Handler();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.item_mood, container, false);

        updatedField = (TextView)rootView.findViewById(R.id.updated_field);

        cityField = (TextView)rootView.findViewById(R.id.city);

        currentTemperatureField = (TextView)rootView.findViewById(R.id.temp);

        weatherIcon = rootView.findViewById(R.id.weather_icon);

        detailsField = (TextView)rootView.findViewById(R.id.details);


        return rootView;
    }

        private void updateWeatherData(final String city){

            new Thread(){
                public void run(){
                    final JSONObject json = RemoteFetch.getJSON(this, city);
                    if(json == null){
                        handler.post(new Runnable(){
                            public void run(){
                                Toast.makeText(getActivity() , place_not_found , Toast.LENGTH_LONG).show();
                            }

                        });
                    } else {
                        handler.post(new Runnable(){
                            public void run(){
                                renderWeather(json);
                            }
                        });
                    }
                }
            }.start();

        }
        private void renderWeather(JSONObject json) {
            try {
                cityField.setText(json.getString("name").toUpperCase() +
                        ", " +
                        json.getJSONObject("sys").getString("country"));

                JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                JSONObject main = json.getJSONObject("main");
                detailsField.setText(
                        details.getString("description").toUpperCase() +
                                "\n" + "Humidity: " + main.getString("humidity") + "%" +
                                "\n" + "Pressure: " + main.getString("pressure") + " hPa");

                currentTemperatureField.setText(
                        String.format("%.2f", main.getDouble("temp")) + " ℃");

                DateFormat df = DateFormat.getDateTimeInstance();
                String updatedOn = df.format(new Date(json.getLong("dt") * 1000));
                updatedField.setText("Last update: " + updatedOn);

       /*setWeatherIcon(details.getInt("id"),
               json.getJSONObject("sys").getLong("sunrise") * 1000,
               json.getJSONObject("sys").getLong("sunset") * 1000);
        */
            } catch (Exception e) {
                Log.e("SimpleWeather", "Field not present in JSON Received");
            }
        }
        /*
    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }
    */
    public void changeCity(String city){
        updateWeatherData(city);
    }

    }
