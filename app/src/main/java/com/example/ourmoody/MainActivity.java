package com.example.ourmoody;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ourmoody.util.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.media.RingtoneManager.*;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final String TAG = "MainActivity";
    int PERMISSION_ID = 44;

    private ImageView moodImageView;
    private ImageButton moodHistoryButton;
    private ImageButton addCommentButton;
    private GestureDetectorCompat mDetector;
    private RelativeLayout parentRelativeLayout;

    private SharedPreferences mPreferences;
    private int currentDate;
    private int currentMoodIndex;
    private String currentComment;
    private String LAT, LON;

    FusedLocationProviderClient mFusedLocationClient;
    TextView addressTxt, updated_atTxt, statusTxt, tempTxt;

    private static shownotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: MainActivity");


        moodImageView = findViewById(R.id.my_moody);
        parentRelativeLayout = findViewById(R.id.parent_relative_layout);
        addCommentButton = findViewById(R.id.btn_addComment);
        moodHistoryButton = findViewById(R.id.btn_ourMoody_entries);

        mDetector = new GestureDetectorCompat(this, this);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        currentDate = mPreferences.getInt(SharedPreferencesHelp.KEY_CURRENT_DAY,1);
        currentMoodIndex = mPreferences.getInt(SharedPreferencesHelp.KEY_CURRENT_MOODY, 3);
        currentComment = mPreferences.getString(SharedPreferencesHelp.KEY_CURRENT_COMMENT," ");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        addressTxt = findViewById(R.id.city);
        updated_atTxt = findViewById(R.id.updated_field);
        statusTxt = findViewById(R.id.details);
        tempTxt = findViewById(R.id.temp);

        changeMoody(currentMoodIndex);

        getLastLocation();

        new weatherTask().execute();


        //Adding Comments to describe mood better
        addCommentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final EditText editText= new EditText(MainActivity.this);

                builder.setMessage(R.string.why).setView(editText).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!editText.getText().toString().isEmpty()){
                            SharedPreferencesHelp.saveComment(editText.getText().toString(), currentDate, mPreferences);
                        }
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Comment saved", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(MainActivity.this, "Comment deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                        .create().show();
            }
        });

        // Zeigt alle bisherigen Mood-Eintraege an
        moodHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this, Entries.class);
                startActivity(intent);
            }
        });

    }



    /**
    folgende Methoden entstehen automatisch nach GestureDetector implementation
     */
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    /** Stimmung auswählen durch rauf-/runterswipen
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getY()-e2.getY()>50){
            if(currentMoodIndex<4){
                currentMoodIndex++;
                changeMoody(currentMoodIndex);
                SharedPreferencesHelp.saveMood(currentMoodIndex,currentDate,mPreferences);
            }
        }
        if(e1.getY()-e2.getY()<50){
            if (currentMoodIndex>0){
                currentMoodIndex--;
                changeMoody(currentMoodIndex);
                SharedPreferencesHelp.saveMood(currentMoodIndex,currentDate,mPreferences);
            }

        } return true;

    }

    /** Location Methoden */
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
                                } else {
                                    LAT = location.getLatitude()+"";
                                    LON = location.getLongitude()+"";
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
            Location mLastLocation = locationResult.getLastLocation();
         LAT =   mLastLocation.getLatitude()+"";
         LON = mLastLocation.getLongitude()+"";
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

    //Change Mood Methode
    private void changeMoody (int currentMoodIndex){
    moodImageView.setImageResource(Constants.moodImagesArray[currentMoodIndex]);
    parentRelativeLayout.setBackgroundResource(Constants.moodColorsArray[currentMoodIndex]);
    }

    /** Benachrichtigungen */
   /* private void scheduleAlarm(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent (this, DayReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(alarmManager!=null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }

        }*/



    public static void scheduleAlarm (Context context, int hour, int min){
        Calendar calendar = Calendar.getInstance();
        Calendar setCalendar = Calendar.getInstance();
        setCalendar.set(Calendar.HOUR_OF_DAY, 14);
        setCalendar.set(Calendar.MINUTE, 00);
        setCalendar.set(Calendar.SECOND, 00);

        if(setCalendar.before(calendar))
            setCalendar.add(Calendar.DATE, 1);

        AlarmManager alarmMgr;

        /**Receiver freischalten */
        ComponentName receiver = new ComponentName(context, DayReciever.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        /**das Pendingintent dient zum Starten einer Activity -> hier: activity_main zum eintragen der mood */
        Intent intent1 = new Intent(context, DayReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,1, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        //hier wird die methode taeglich bzw. alle 24 Stunden aufgerufen
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


        //zum Abbrechen von bereits geplanten Erinnerungen/Reminder/Benachrichtigungen
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent  intent2 = new Intent(String.valueOf(DayReciever.class));
        PendingIntent   pm2 = PendingIntent.getBroadcast(
                context.getApplicationContext(), 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        am.cancel(pendingIntent);
    }



    /** Alarm-/Benachrichtigungs-Trigger */
    public class AlarmReceiver extends BroadcastReceiver{
        String TAG = "AlarmReceiver";
        Intent intentTrigger = new Intent(getApplicationContext(), DayReciever.class);
        PendingIntent BenachrichtigungsTrigger = PendingIntent.getBroadcast(getApplicationContext(),
                0, intentTrigger, PendingIntent.FLAG_UPDATE_CURRENT);

        @Override
        public void onReceive(Context context, Intent intent) {
         JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            /** methode sollte notification-triggern
         //   jobScheduler.shownotify(context, MainActivity.class, "ourMoody-Reminder",
          //          "Wie fühlst du dich gerade? Trage deinen derzeitigen Zustand ein :)"); */

            jobScheduler.shownotify(getApplicationContext(), MainActivity.class, "ourMoody-Reminder",
                          "Wie fühlst du dich gerade? Trage deinen derzeitigen Zustand ein :)");

        }
    }
*/


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void shownotify(Context context, Class<?> cls, String title, String content){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notificationIntent = new Intent(context, cls);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                1, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                R.string.default_notification_channel_id);
        Notification notification = builder.setContentTitle(title)
                .setContentText(content).setAutoCancel(true)
                .setSound(alarmSound).setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();


        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.weather, menu);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return mDetector.onTouchEvent(event);
    }


    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /* Showing the ProgressBar, Making the main design GONE */
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&units=metric&appid=" + R.string.open_weather_maps_app_id);
            return response;
        }
/** Wetter updaten */
        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.GERMAN).format(new Date(updatedAt * 1000));
                String temp = main.getString("temp") + "°C";

                String weatherDescription = weather.getString("description");
                String address = jsonObj.getString("name") + ", " + sys.getString("country");



                addressTxt.setText(address);
                updated_atTxt.setText(updatedAtText);
                statusTxt.setText(weatherDescription.toUpperCase());
                tempTxt.setText(temp);


                /* Views populated, Hiding the loader, Showing the main design */
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }
    }

}