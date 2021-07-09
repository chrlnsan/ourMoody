package com.example.ourmoody;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;


import java.text.DateFormat;
import java.util.Calendar;

public class TimePicker extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    private Button timepicker_btn;
    private TextView tv;
    private Button cancel;
    private long dateInMillis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timepicker);
        timepicker_btn = findViewById(R.id.timepicker_btn);
        tv = findViewById(R.id.tv_notification);
        cancel = findViewById(R.id.cancel_btn);
        Calendar c = Calendar.getInstance();
        int hour=(Calendar.HOUR_OF_DAY);
        int min = (Calendar.MINUTE);
        android.widget.TimePicker view;
        view = findViewById(R.id.timepicker);
        dateInMillis = Calendar.getInstance().getTimeInMillis();
        Context context = getApplicationContext();

        timepicker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }

                                          });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
            }
        });

    }
    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        updateTimeText(c);
        startAlarm(c);

    }



    private void updateTimeText(Calendar c) {
        String timeText = "Benachrichtigung gesetzt für: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        tv.setText(timeText);
    }
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateInMillis, pendingIntent);

        //hier wird die methode taeglich bzw. alle 24 Stunden aufgerufen
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }
    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 2, intent, 0);
        alarmManager.cancel(pendingIntent);
        tv.setText("Benachrichtigung abgebrochen");
    }



}
