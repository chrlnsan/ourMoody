package com.example.ourmoody;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;

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
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ourmoody.util.Constants;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.media.RingtoneManager.*;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final String TAG = "MainActivity";

    private ImageView moodImageView;
    private ImageButton moodHistoryButton;
    private ImageButton addCommentButton;
    private GestureDetectorCompat mDetector;
    private RelativeLayout parentRelativeLayout;

    private SharedPreferences mPreferences;
    private int currentDate;
    private int currentMoodIndex;
    private String currentComment;


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

        changeMoody(currentMoodIndex);




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

        // See history of mood entries
        moodHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this, Entries.class);
                startActivity(intent);
            }
        });

    }



    /*
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
 // Choose a mood by swiping up/down
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

    //Change Mood Methode
    private void changeMoody (int currentMoodIndex){
    moodImageView.setImageResource(Constants.moodImagesArray[currentMoodIndex]);
    parentRelativeLayout.setBackgroundResource(Constants.moodColorsArray[currentMoodIndex]);
    }

    // Benachrichtigungen
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

    public static void scheduleAlarm(Context context, DayReciever.class, int hour, int min){
        Calendar calendar = Calendar.getInstance();
        Calendar setCalendar = Calendar.getInstance();
        setCalendar.set(Calendar.HOUR_OF_DAY, 14);
        setCalendar.set(Calendar.MINUTE, 00);
        setCalendar.set(Calendar.SECOND, 00);

        if(setCalendar.before(calendar))
        setCalendar.add(Calendar.DATE, 1);

        AlarmManager alarmMgr;

        //Receiver freischalten
        ComponentName receiver = new ComponentName(context, DayReciever.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

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

    // Alarm-/Benachrichtigungs-Trigger
    public class AlarmReceiver extends BroadcastReceiver{
        String TAG = "AlarmReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            //Triggered die notification
            jobScheduler.shownotify(context, MainActivity.class, "ourMoody-Reminder",
                    "Wie fühlst du dich gerade? Trage deinen derzeitigen Zustand ein :)");

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void shownotify(Context context, Class<?> cls, String title, String content){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notificationIntent = new Intent(context, cls);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                1, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                getString(R.string.default_notification_channel_id));
        Notification notification = builder.setContentTitle(title)
                .setContentText(content).setAutoCancel(true)
                .setSound(alarmSound).setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();

        /*
        ----------
        */


/*
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setContentTitle(title)
                .setContentText(content).setAutoCancel(true)
                .setSound(alarmSound).setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
        */


    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        return mDetector.onTouchEvent(event);
    }


}