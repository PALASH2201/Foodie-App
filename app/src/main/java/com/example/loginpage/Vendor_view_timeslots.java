package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Vendor_view_timeslots extends AppCompatActivity {

    private TimeSlotDetails_myAdapter adapter;
    private AlertDialog dialog;

    private String day;
    List<TimeSlotDataClass> dataList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vendor_view_timeslots);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String restaurant_name = intent.getStringExtra("restaurant_name");
        String restaurant_id = intent.getStringExtra("restaurant_id");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        AlertDialog.Builder builder = new AlertDialog.Builder(Vendor_view_timeslots.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();
        adapter = new TimeSlotDetails_myAdapter(Vendor_view_timeslots.this , dataList , true,null,null);
        recyclerView.setAdapter(adapter);


        Spinner daySpinner = findViewById(R.id.day_spinner);
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        ArrayAdapter<String> Dayadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysOfWeek);
        Dayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(Dayadapter);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dialog.show();
                day = parent.getItemAtPosition(position).toString();
                HandleDatabase(day,restaurant_name,restaurant_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Vendor_view_timeslots.this,"Please select a day!", Toast.LENGTH_SHORT).show();
            }
        });

        if(restaurant_name!=null && restaurant_id != null)
        {
            FloatingActionButton addTimeSlot = findViewById(R.id.addTimeSlot);
            addTimeSlot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Vendor_view_timeslots.this,Vendor_addTimeSlot.class);
                    intent.putExtra("restaurant_name",restaurant_name);
                    Log.d("sending rest name",restaurant_name);
                    intent.putExtra("restaurant_id",restaurant_id);
                    Log.d("sending rest id",restaurant_name);
                    startActivity(intent);
                }
            });
        }
    }
    public void HandleDatabase(String day,String restaurant_name,String restaurant_id){
        Log.d("Day",day);
        DatabaseReference timeSlotsRef = FirebaseDatabase.getInstance().getReference("time_slots").child(day).child(restaurant_name);
        timeSlotsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataList.clear();
                for (DataSnapshot timeSlotSnapshot : dataSnapshot.getChildren()) {
                    String timeSlot = timeSlotSnapshot.getKey();
                    Log.d("Time Slot", timeSlot);
                    String default_availableSlots = timeSlotSnapshot.child("default_available_slots").getValue(String.class);
                    String availableSlots = timeSlotSnapshot.child("available_slots").getValue(String.class);
                    Log.d("Default Available Slots", default_availableSlots);
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
}