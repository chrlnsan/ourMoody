package com.example.ourmoody.util;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ourmoody.MainActivity;
import com.google.android.material.tabs.TabLayout;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";

    private ViewPager2 screenpager;
    TabLayout tabIndicator;
    Button btn_weiter;
    int position = 0;
    Button getBtn_weiter;
    Animation btn_anim;
    TextView tvSkip;
/* PAUSIERT, weil andere Aktivitaeten zuerst kreiiert werden muessen
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: StartActivity");

        //Ueberprueft, ob diese activity vor App-launch bereits verwendet wurde oder nicht
        public boolean isForeground(String)
*/
    }



