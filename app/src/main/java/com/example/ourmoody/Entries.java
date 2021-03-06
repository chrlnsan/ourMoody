package com.example.ourmoody;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Entries extends AppCompatActivity {
    private static final String TAG = "Entries";

    private RecyclerView moodyRecyclerview;

    private ourMoodyAdapter ourMoodyAdapter;
    private SharedPreferences mPreferences;
    private ArrayList<Integer> moods = new ArrayList<>();
    private ArrayList<String> comments = new ArrayList<>();
    private int currentDate;
    private static String API_KEY = "a8de24b6eafaefc7e599fa726832d039";

    //f??r Location
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    private String LAT, LON;

    // Elemente von Wetterfunktion
    private TextView addressTxt, updated_atTxt, statusTxt, tempTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entries);
        //setContentView(R.layout.it);
        Log.d(TAG, "onCreate: Entries");




        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentDate = mPreferences.getInt(SharedPreferencesHelp.KEY_CURRENT_DAY, 1);

        moodyRecyclerview = findViewById(R.id.recycler_ourMoodys);
        moodyRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));




/*
        updated_atTxt = (TextView) findViewById(R.id.updated_field);
        statusTxt = (TextView)findViewById(R.id.details);
        tempTxt = (TextView)findViewById(R.id.temp);
        addressTxt = (TextView) findViewById(R.id.address);



 */


        for(int i=0; i<currentDate; i++){
            moods.add(mPreferences.getInt("KEY_MOOD" + i, 3));
            comments.add(mPreferences.getString("KEY_COMMENT"+ i, ""));
        }



        // Elemente von item_mood bef??llen
        ourMoodyAdapter = new ourMoodyAdapter(this, currentDate, moods, comments);
        moodyRecyclerview.setAdapter(ourMoodyAdapter);

        // Location abrufen
        getLastLocation();

        // Wetter von Location abrufen
        //new weatherTask().execute();
        //System.out.println(new weatherTask().execute());

        //System.out.println("weatherTask API call");
        //String response = new weatherTask().doInBackground();
        //System.out.println(response);
        // String resp = doInBackground();
        // onPostexecute(resp);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.settings){
            Intent intent =new Intent(Entries.this, com.example.ourmoody.TimePicker.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                    System.out.println("HERE");
                                } else {
                                    LAT = location.getLatitude()+"";
                                    LON = location.getLongitude()+"";
                                    System.out.println(LAT +" "+ " "+LON);
                                    System.out.println("weatherTask API call");
                                     new weatherTask().execute();

                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            System.out.println("##### callback lat lon");

            Location mLastLocation = locationResult.getLastLocation();
            LAT =   mLastLocation.getLatitude()+"";
            LON = mLastLocation.getLongitude()+"";

            System.out.println("##### callback lat lon");

            // TODO .execute
            //System.out.println("weatherTask API call");
            //String response = new weatherTask().doInBackground();
            //System.out.println(response);
        }
    };
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }


    class weatherTask extends AsyncTask<String, Void, String> {

        weatherTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        // TODO Response
        protected String doInBackground(String... args) {


            System.out.println("##### .execute calls doInBackground");
            System.out.println("https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&units=metric&appid=" + API_KEY);


            String response;
            try {AsyncHttpClient client = Dsl.asyncHttpClient();
                BoundRequestBuilder getRequest = client.prepareGet("https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&units=metric&appid=" + API_KEY);
                Future<Response> responseFuture = getRequest.execute();
              response =  responseFuture.get().getResponseBody();
              System.out.println(response);
              System.out.println("making call");
              return response;
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //  String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&units=metric&appid=" + API_KEY);



            //System.out.println(response);

            return null;
        }
        // Wetter updaten

        protected void onPostExecute(String result) {




            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.GERMAN).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp") + "??C";

                String weatherDescription = weather.getString("description");
                String address = jsonObj.getString("name") + ", " + sys.getString("country");



                //Hier w??rden die Elemente von item_mood mit Wetterdaten bef??llt werden
                /*
                addressTxt.setText(address);
                updated_atTxt.setText(updatedAtText);
                statusTxt.setText(weatherDescription.toUpperCase());
                tempTxt.setText(temp);

                 */







            } catch (JSONException e) {
               /*loader.setVisibility(View.GONE);
                errorTxt.setVisibility(View.VISIBLE);
                */

            }

        }
    }



}