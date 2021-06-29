package com.example.ourmoody;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {
    SharedPreferences mPreference;

    public CityPreference(Activity activity){
        mPreference = activity.getPreferences(Activity.MODE_PRIVATE);
    }


    public String getCity(){

        return mPreference.getString("city", "Graz, AUT");
    }

    void setCity(String city){
        mPreference.edit().putString("city", city).commit();
    }
}
