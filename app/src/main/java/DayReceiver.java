import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.ourmoody.SharedPreferencesHelp;

public class DayReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int currentDay = mPreferences.getInt(SharedPreferencesHelp.KEY_CURRENT_DAY,1);
        currentDay++;
        mPreferences.edit().putInt(SharedPreferencesHelp.KEY_CURRENT_DAY,currentDay).apply();
    }
}
