package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class User_time_slot_viewer extends AppCompatActivity {
    private TimeSlotDetails_myAdapter adapter;
    private AlertDialog dialog;
    private TextView timeSlotCheckoutBtn;

    List<TimeSlotDataClass> dataList ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_time_slot_viewer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String restaurant_name = intent.getStringExtra("restaurant_name");
        String restaurant_id = intent.getStringExtra("restaurant_id");
        String totalbill = intent.getStringExtra("total bill");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        AlertDialog.Builder builder = new AlertDialog.Builder(User_time_slot_viewer.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        timeSlotCheckoutBtn = findViewById(R.id.timeSlotCheckoutBtn);

        dataList = new ArrayList<>();
        adapter = new TimeSlotDetails_myAdapter(User_time_slot_viewer.this , dataList , false,timeSlotCheckoutBtn,totalbill);
        recyclerView.setAdapter(adapter);

        String day = findDay();
        HandleDatabase(day,restaurant_name,restaurant_id);
    }
    public void HandleDatabase(String day,String restaurant_name,String restaurant_id){
        DatabaseReference timeSlotsRef = FirebaseDatabase.getInstance().getReference("time_slots").child(day).child(restaurant_name);
        timeSlotsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot timeSlotSnapshot : dataSnapshot.getChildren()) {
                    String timeSlot = timeSlotSnapshot.getKey();
                    Log.d("Time Slot", timeSlot);
                    String default_availableSlots = timeSlotSnapshot.child("default_available_slots").getValue(String.class);
                    String availableSlots = timeSlotSnapshot.child("available_slots").getValue(String.class);
                    Log.d("Available Slots", availableSlots);
                    TimeSlotDataClass timeSlotDataClass = new TimeSlotDataClass(day,timeSlot,availableSlots,restaurant_id,restaurant_name,default_availableSlots);
                    dataList.add(timeSlotDataClass);
                    adapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
    public String findDay(){
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dayName;
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                dayName = "Sunday";
                break;
            case Calendar.MONDAY:
                dayName = "Monday";
                break;
            case Calendar.TUESDAY:
                dayName = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayName = "Wednesday";
                break;
            case Calendar.THURSDAY:
                dayName = "Thursday";
                break;
            case Calendar.FRIDAY:
                dayName = "Friday";
                break;
            case Calendar.SATURDAY:
                dayName = "Saturday";
                break;
            default:
                dayName = "Unknown";
                break;
        }
        return dayName;
    }
}