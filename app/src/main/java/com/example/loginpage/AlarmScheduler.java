package com.example.loginpage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class AlarmScheduler {

    private static final int MIDNIGHT_ALARM_REQUEST_CODE = 1002;

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleMidnightUpdate(Context context) {
        Log.d("Inside Schedule Midnight Update","true");
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            Log.d(String.valueOf(context),"Permission not granted");
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, MIDNIGHT_ALARM_REQUEST_CODE);
            return;
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MidnightAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, MIDNIGHT_ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar targetTime = Calendar.getInstance();
        try {
            targetTime.set(Calendar.HOUR_OF_DAY, 11);
            targetTime.set(Calendar.MINUTE, 44);
            targetTime.set(Calendar.SECOND, 0);
            targetTime.set(Calendar.MILLISECOND, 0);

            if (targetTime.before(Calendar.getInstance())) {
                targetTime.add(Calendar.DAY_OF_YEAR, 1);
            }

            Log.d("AlarmScheduler", "Scheduling alarm for: " + targetTime.getTime());
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetTime.getTimeInMillis(), pendingIntent);
            Log.d("Exact time:", String.valueOf(targetTime.getTime()));
        } catch (Exception e) {
            Log.e("AlarmScheduler", "Error scheduling alarm:", e);
        }
    }
}


