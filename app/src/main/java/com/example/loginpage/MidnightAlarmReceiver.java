package com.example.loginpage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MidnightAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "MidnightAlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm received, starting service...");
        Intent serviceIntent = new Intent(context, MyBackgroundService.class);
        context.startService(serviceIntent);
    }
}