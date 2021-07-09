package com.example.ourmoody;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
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
import android.app.TimePickerDialog;
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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{

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


    @Override
    public boolean onTouchEvent(MotionEvent event){
        return mDetector.onTouchEvent(event);
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
    //Change Mood Methode
    private void changeMoody (int currentMoodIndex){
        moodImageView.setImageResource(Constants.moodImagesArray[currentMoodIndex]);
        parentRelativeLayout.setBackgroundResource(Constants.moodColorsArray[currentMoodIndex]);
    }
 // Stimmung auswählen durch rauf-/runterswipen
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

    }}













