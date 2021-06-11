package com.example.ourmoody;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "oncreate: MainActivity");

        moodImageView = findViewById(R.id.my_moody);
        parentRelativeLayout = findViewById(R.id.parent_relative_layout);
        addCommentButton = findViewById(R.id.btn_addComment);
        moodHistoryButton = findViewById(R.id.btn_ourMoody_entries);

        mDetector = new GestureDetectorCompat(this, this);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        currentMoodIndex = mPreferences.getInt(SharedPreferencesHelp.KEY_CURRENT_MOODY, 3);

        //Adding Comments to describe mood better
        addCommentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final EditText editText= new EditText(MainActivity.this);

                builder.setMessage("Why do you feel this way?").setView(editText).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!editText.getText().toString().isEmpty()){
                            //SharedPreferences
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
                }) .create().show();
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


    private void changeMoody (int currentMoodIndex){
    moodImageView.setImageResource(Constants.moodImagesArray[currentMoodIndex]);
    parentRelativeLayout.setBackgroundResource(Constants.moodColorsArray[currentMoodIndex]);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return mDetector.onTouchEvent(event);
    }

}