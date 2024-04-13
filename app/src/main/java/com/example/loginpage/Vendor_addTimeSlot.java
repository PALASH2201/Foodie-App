package com.example.loginpage;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Vendor_addTimeSlot extends AppCompatActivity {

   String selectedDay ;
   String selectedTimeSlot;
   String default_available_slots;
   String restaurant_name ,restaurant_id;
   boolean checkBoxSelected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vendor_add_time_slot);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner daySpinner = findViewById(R.id.day_spinner);
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        ArrayAdapter<String> Dayadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysOfWeek);
        Dayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(Dayadapter);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDay = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Vendor_addTimeSlot.this,"Please select a day!", Toast.LENGTH_SHORT).show();
            }
        });

        Spinner slotSpinner = findViewById(R.id.slot_spinner);
        String[] slots = {"8:00 - 9:00 am" , "10:30 - 11:30 am" , "1:00 - 2:00 pm" , "4:00 - 5:00 pm" , "7:00 - 8:00 pm" , "8:00 - 9:00 pm"};

        ArrayAdapter<String> Slotadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, slots);
        Slotadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        slotSpinner.setAdapter(Slotadapter);

        slotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTimeSlot = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Vendor_addTimeSlot.this,"Please select a time slot!", Toast.LENGTH_SHORT).show();
            }
        });
        CheckBox same_time_everyday_checkbox = findViewById(R.id.same_time_everyday_checkbox);
        same_time_everyday_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkBoxSelected = true;
            }
        });

        Intent intent = getIntent();
        restaurant_id = intent.getStringExtra("restaurant_id");
        Log.d("Got rest id",restaurant_id);
        restaurant_name = intent.getStringExtra("restaurant_name");
        Log.d("Got rest name",restaurant_name);

        Button add_slot_btn = findViewById(R.id.add_slot_button);
        add_slot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText default_availableSlots = findViewById(R.id.availableSlots);
                default_available_slots = default_availableSlots.getText().toString();
                if(same_time_everyday_checkbox.isChecked() && checkBoxSelected){
                    Log.d("sending rest name",restaurant_name);
                      addTimeSlotEveryDay(daysOfWeek,selectedTimeSlot,default_available_slots,restaurant_id,restaurant_name);
                 }
                 else{
                    Log.d("sending rest name",restaurant_name);
                     addTimeSlot(selectedDay,selectedTimeSlot,default_available_slots,restaurant_id,restaurant_name);
                 }
            }
        });
    }
    public void addTimeSlot(String day , String timeSlot , String default_available_slots,String restaurant_id,String restaurant_name){
        Log.d("Inside add func","day: "+day);
        Log.d("Inside add func","restaurant-name: "+restaurant_name);
        Log.d("Inside add func","time-slot: "+timeSlot);

        DatabaseReference dayRef = FirebaseDatabase.getInstance().getReference("time_slots").child(day).child(restaurant_name);
        DatabaseReference timeSlotRef = dayRef.child(timeSlot);

        AlertDialog.Builder builder = new AlertDialog.Builder(Vendor_addTimeSlot.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        timeSlotRef.child("default_available_slots").setValue(default_available_slots).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dialog.dismiss();
                Toast.makeText(Vendor_addTimeSlot.this,default_available_slots+" slots added for "+timeSlot+" for "+day,Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(Vendor_addTimeSlot.this,timeSlot+" slot not added for "+day+", TRY AGAIN!",Toast.LENGTH_SHORT).show();
            }
        });
        timeSlotRef.child("available_slots").setValue(default_available_slots).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(Vendor_addTimeSlot.this,timeSlot+" slot not added for "+day+", TRY AGAIN!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void addTimeSlotEveryDay(String[] days,String timeSlot , String default_available_slots,String restaurant_id,String restaurant_name){
        for (String day : days) {
            addTimeSlot(day, timeSlot, default_available_slots, restaurant_id, restaurant_name);
        }
    }
}
