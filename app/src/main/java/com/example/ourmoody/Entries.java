package com.example.ourmoody;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Entries extends AppCompatActivity{
    private static final String TAG = "Entries";

    private RecyclerView moodyRecyclerview;

    private ourMoodyAdapter ourMoodyAdapter;
    private ArrayList<Integer> moods = new ArrayList<>();
    private int currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_entries);
        Log.d(TAG, "onCreate: Entries");

        moodyRecyclerview = findViewById(R.id.recycler_ourMoodyMoods);
        moodyRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));


        ourMoodyAdapter = new ourMoodyAdapter(this, currentDate, moods);
        moodyRecyclerview.setAdapter(ourMoodyAdapter);

    }

}