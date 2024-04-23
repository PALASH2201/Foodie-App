package com.example.loginpage;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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

import io.github.muddz.styleabletoast.StyleableToast;

public class Vendor_update_timeslot extends AppCompatActivity {

    String selectedDay ;
    String selectedTimeSlot;
    String default_available_slots;
    String restaurant_name ,restaurant_id;
    private boolean update_checkBoxSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vendor_update_timeslot);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner daySpinner = findViewById(R.id.updated_day_spinner);
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        EditText default_availableSlots = findViewById(R.id.updated_availableSlots);
        Spinner slotSpinner = findViewById(R.id.updated_slot_spinner);
        String[] slots = {"8:00 - 9:00 am" , "10:30 - 11:30 am" , "1:00 - 2:00 pm" , "4:00 - 5:00 pm" , "7:00 - 8:00 pm" , "8:00 - 9:00 pm"};

        Intent intent = getIntent();
        selectedDay = intent.getStringExtra("Selected Day");
        selectedTimeSlot = intent.getStringExtra("Selected Time Slot");
        default_available_slots = intent.getStringExtra("Available slots");
        restaurant_id = intent.getStringExtra("restaurant_id");
        restaurant_name = intent.getStringExtra("restaurant_name");

        default_availableSlots.setText(default_available_slots);

        int dayIndex = -1;
        for (int i = 0; i < daysOfWeek.length; i++) {
            if (daysOfWeek[i].equals(selectedDay)) {
                dayIndex = i;
                break;
            }
        }
        int timeSlotIndex = -1;
        for (int i = 0; i < slots.length; i++) {
            if (slots[i].equals(selectedTimeSlot)) {
                timeSlotIndex = i;
                break;
            }
        }
        daySpinner.setSelection(dayIndex);
        slotSpinner.setSelection(timeSlotIndex);

        ArrayAdapter<String> DayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysOfWeek);
        DayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(DayAdapter);

        ArrayAdapter<String> SlotAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, slots);
        SlotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        slotSpinner.setAdapter(SlotAdapter);

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDay = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                StyleableToast.makeText(Vendor_update_timeslot.this,"Please select a day!", Toast.LENGTH_SHORT,R.style.warningToast).show();
            }
        });
        slotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTimeSlot = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                StyleableToast.makeText(Vendor_update_timeslot.this,"Please select a time slot!", Toast.LENGTH_SHORT,R.style.warningToast).show();
            }
        });
        CheckBox same_time_everyday_checkbox = findViewById(R.id.same_time_everyday_checkbox);
        same_time_everyday_checkbox.setOnClickListener(v -> update_checkBoxSelected = true);

        Button update_slot_btn = findViewById(R.id.update_slot_button);
        update_slot_btn.setOnClickListener(v -> {
            default_available_slots = default_availableSlots.getText().toString();
            if(same_time_everyday_checkbox.isChecked() && update_checkBoxSelected){
                updateTimeSlotEveryDay(daysOfWeek,selectedTimeSlot,default_available_slots,restaurant_name);
            }
            else{
                updateTimeSlot(selectedDay,selectedTimeSlot,default_available_slots,restaurant_name);
            }
        });
    }
    public void updateTimeSlot(String day , String timeSlot , String default_available_slots,String restaurant_name){

        DatabaseReference dayRef = FirebaseDatabase.getInstance().getReference("time_slots").child(day).child(restaurant_name);
        DatabaseReference timeSlotRef = dayRef.child(timeSlot);

        AlertDialog.Builder builder = new AlertDialog.Builder(Vendor_update_timeslot.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        timeSlotRef.child("default_available_slots").setValue(default_available_slots).addOnSuccessListener(unused -> {
            dialog.dismiss();
            StyleableToast.makeText(Vendor_update_timeslot.this,default_available_slots+" slots updated for "+timeSlot+" for "+day,Toast.LENGTH_SHORT,R.style.successToast).show();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            StyleableToast.makeText(Vendor_update_timeslot.this,timeSlot+" slot not updated for "+day+", TRY AGAIN!",Toast.LENGTH_SHORT,R.style.failureToast).show();
        });
    }
    public void updateTimeSlotEveryDay(String[] days,String timeSlot , String default_available_slots,String restaurant_name){
        for (String day : days) {
            updateTimeSlot(day, timeSlot, default_available_slots,restaurant_name);
        }
    }
}