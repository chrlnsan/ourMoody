package com.example.ourmoody;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ourmoody.util.ScreenItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "IntroActivity";

    private ViewPager screenPager;
    StartViewPagerAdapter startViewPagerAdapter;
    TabLayout tabIndicator;
    Button btn_weiter;
    int position = 0;
    Button btn_einfuehrung;
    Animation btn_animation;
    TextView tv_skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: IntroActivity");

        //zeigt Activity im Vollbild-Modus an
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //prüft, bei Launch dieser Activity, ob die App davor bereits geoeffnet wurde oder nicht
        if(restorePrefData()){
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
            finish();
        }

        setContentView(R.layout.activity_start);

        //versteckt die action-bar
        getSupportActionBar().hide();

        //ini views
        btn_weiter = findViewById(R.id.btn_weiter);
        btn_einfuehrung = findViewById(R.id.btn_einfuehrung);
        tabIndicator = findViewById(R.id.tabIndicator);
        btn_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_animation);

        //List-Screen Inhalt
        final List<ScreenItem> omList = new ArrayList<>();
        omList.add(new ScreenItem("ourMoody-Willkommen", "Hallo :) Willkommen zu ourMoody", R.drawable.happy));
        omList.add(new ScreenItem("ourMoody-Start", "Wir möchten dich mit ourMoody dabei unterstützen," +
                " dass du mindestens 1x täglich einen Eintrag über Gefühlszustand gibst und darüber hinaus einen Überblick dazu bekommst. Zur einfachen Handhabung gibt es 5 moodies zur Auswahl", R.drawable.moodies));
        omList.add(new ScreenItem("ourMoody-Entry", "Über diesen Screen trägst du deinen aktuellen Gefühlszustand ein.",));
        omList.add(new ScreenItem("ourMoody-Comment", "Mittels einem Kommentar kannst du deinen Eintrag genauer definieren",));
        omList.add(new ScreenItem("ourMoody-Teilen", "Wenn du magst kannst du deinen Eintrag auch mit jemandem über Soziale Medien, etc. teilen",));
        omList.add(new ScreenItem("ourMoody-Entries","Auf diesem Entries-Screen findest du eine Sammlung deiner bisherigen Einträge." +
                " Zudem gibt es mit jedem ourMoody-Eintrag einen kurzen Wetterberichts-Icon",));

        //Viewpager-Setup
        screenPager = findViewById(R.id.screen_viewpager);
        startViewPagerAdapter = new StartViewPagerAdapter(this, omList);
        screenPager.setAdapter(startViewPagerAdapter);

        //Tab-Layout mit Viewpager
        tabIndicator.setupWithViewPager(screenPager);

        //ein click-Listener dem btn_weiter hinzufuegen
        btn_weiter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                position = screenPager.getCurrentItem();
                if(position < omList.size()){
                    int position = 0;
                    position++;
                    screenPager.setCurrentItem(position);
                }
                if (position == omList.size() - 1){
                    loadLetztesScreen();
                }
            }
        });

        //btn_einfuehrung click-listener
        btn_einfuehrung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open activity_main
                Intent activity_main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(activity_main);

                savePrefsData();
                finish();
            }
        });

        //tv_skip click-listener
        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(omList.size());
            }
        });

    }


    private boolean restorePrefData(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("myPreferences", MODE_PRIVATE);
        Boolean wasIntroActivityOpenedBefore = preferences.getBoolean("wasIntroOpened", false);
        return wasIntroActivityOpenedBefore;
    }

    private void savePrefsData(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("myPreferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IntroWasOpened", true);
        editor.commit();
    }

    private void loadLetztesScreen(){
        btn_weiter.setVisibility(View.INVISIBLE);
        btn_einfuehrung.setVisibility(View.VISIBLE);
        tv_skip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btn_einfuehrung.setAnimation(btn_animation);
    }

}
