package com.example.ourmoody;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Entries extends AppCompatActivity{
    private static final String TAG = "Entries";

    private RecyclerView moodyRecyclerview;

    private ourMoodyAdapter ourMoodyAdapter;
    private SharedPreferences mPreferences;
    private ArrayList<Integer> moods = new ArrayList<>();
    private ArrayList<String> comments = new ArrayList<>();
    private int currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entries);
        Log.d(TAG, "onCreate: Entries");


        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentDate = mPreferences.getInt(SharedPreferencesHelp.KEY_CURRENT_DAY, 1);

        moodyRecyclerview = findViewById(R.id.recycler_ourMoodys);
        moodyRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));

        for(int i=0; i<currentDate; i++){
            moods.add(mPreferences.getInt("KEY_MOOD" + i, 3));
            comments.add(mPreferences.getString("KEY_COMMENT"+ i, ""));
        }

        ourMoodyAdapter = new ourMoodyAdapter(this, currentDate, moods, comments);
        moodyRecyclerview.setAdapter(ourMoodyAdapter);

    }

}