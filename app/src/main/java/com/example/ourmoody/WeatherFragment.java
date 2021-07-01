package com.example.ourmoody;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.ourmoody.util.Constants;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.PendingIntent.getActivities;
import static android.app.PendingIntent.getActivity;
import static com.example.ourmoody.R.string.place_not_found;
import static java.lang.String.valueOf;
import static java.security.AccessController.getContext;

public class WeatherFragment extends Fragment {
    private TextView cityField;
    private ImageView weatherIcon;
    private TextView updatedField;
    private TextView currentTemperatureField;
    private TextView detailsField;

    Handler handler;

    public WeatherFragment(){
        handler = new Handler();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.weather_fragment, container, true);

        updatedField = (TextView)rootView.findViewById(R.id.updated_field);

        cityField = (TextView)rootView.findViewById(R.id.city);

        currentTemperatureField = (TextView)rootView.findViewById(R.id.temp);

        weatherIcon = (ImageView)rootView.findViewById(R.id.weather_icon);

        detailsField = (TextView)rootView.findViewById(R.id.details);

        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateWeatherData(new CityPreference(getActivity()).getCity());
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

                setWeatherIcon(details.getInt("id"),
               json.getJSONObject("weather").getString("main"),
               json.getJSONObject("weather").getString("description"));


            } catch (Exception e) {
                Log.e("SimpleWeather", "Field not present in JSON Received");
            }
        }

    private void setWeatherIcon(int actualId, String main, String description){
        int id = actualId / 100;
        String icon = "";
        /*
        if(actualId == 800){

            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
        */

            switch(id) {
                case 2 : weatherIcon.setImageResource(Constants.weatherIconArray[0]);
                    break;
                case 3 : weatherIcon.setImageResource(Constants.weatherIconArray[4]);
                    break;
                case 5 : weatherIcon.setImageResource(Constants.weatherIconArray[5]);
                    break;
                case 6 : weatherIcon.setImageResource(Constants.weatherIconArray[7]);
                    break;
                case 7 : weatherIcon.setImageResource(Constants.weatherIconArray[8]);
                    break;
                case 8 : weatherIcon.setImageResource(Constants.weatherIconArray[3]);
                    break;
                default: weatherIcon.setImageResource(Constants.weatherIconArray[9]);
            }
        }


    public void changeCity(String city){
        updateWeatherData(city);
    }

    }
