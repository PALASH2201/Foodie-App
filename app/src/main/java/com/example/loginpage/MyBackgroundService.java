package com.example.loginpage;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyBackgroundService extends Service {

    private static final String TAG = "MyBackgroundService";
    public MyBackgroundService() {
    }

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate");
    }

    private final Runnable myTask = new Runnable() {
        @Override
        public void run() {
            Log.d("Background Service","Started");
            DatabaseReference timeSlotsRef = FirebaseDatabase.getInstance().getReference("time_slots");
            timeSlotsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot daySnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot restaurantSnapshot : daySnapshot.getChildren()) {
                            for (DataSnapshot timeSlotSnapshot : restaurantSnapshot.getChildren()) {
                                String defaultSlots = timeSlotSnapshot.child("default_available_slots").getValue(String.class);
                                timeSlotSnapshot.child("available_slots").getRef().setValue(defaultSlots);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                }
            });
            stopSelf();
        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand");
        myTask.run();
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy");
    }
}