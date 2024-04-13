package com.example.loginpage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

public class MidnightAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "MidnightAlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Received alarm at: " + new Date());
        Intent serviceIntent = new Intent(context, MyBackgroundService.class);
        context.startService(serviceIntent);
    }
}