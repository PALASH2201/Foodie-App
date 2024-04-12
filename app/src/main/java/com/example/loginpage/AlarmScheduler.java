package com.example.loginpage;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmScheduler {

    private static final int MIDNIGHT_ALARM_REQUEST_CODE = 1002;

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleMidnightUpdate(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MidnightAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, MIDNIGHT_ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 2);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        // Check if time has already passed for today
        if (midnight.before(Calendar.getInstance())) {
            midnight.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, midnight.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}


