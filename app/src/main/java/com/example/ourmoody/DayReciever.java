package com.example.ourmoody;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DayReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       SharedPreferences mPreferences;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int currentDay = mPreferences.getInt(SharedPreferencesHelp.KEY_CURRENT_DAY,1);
        currentDay++;
        mPreferences.edit().putInt(SharedPreferencesHelp.KEY_CURRENT_DAY,currentDay).apply();
    }
}
