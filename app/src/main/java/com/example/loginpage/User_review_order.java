package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class User_review_order extends AppCompatActivity {
    String updated_available_slots,restaurant_name ,time_slot,day,totalBill,availableSlots;
    AlertDialog dialog;
    boolean isSlotAvailable=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_review_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(User_review_order.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        Intent intent = getIntent();
         restaurant_name = intent.getStringExtra("restaurant_name");
         time_slot = intent.getStringExtra("Selected time slot");
         day = intent.getStringExtra("day");
         totalBill = intent.getStringExtra("total bill");
         availableSlots = intent.getStringExtra("Available slots");

        findSlots(day,restaurant_name,time_slot,availableSlots);

    }
    public void findSlots(String day , String restaurant_name ,String timeSlot,String availableSlots){
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("time_slots").child(day).child(restaurant_name).child(timeSlot);
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                   updated_available_slots  = snapshot.child("available_slots").getValue(String.class);
                   Log.d("Updated available slots",updated_available_slots);
                    assert updated_available_slots != null;
                    if(Integer.parseInt(updated_available_slots) >= Integer.parseInt(availableSlots)){
                       isSlotAvailable = true;
                        Log.d("Updated available slots",updated_available_slots);
                        updateUI(isSlotAvailable);
                   }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(User_review_order.this,"Could not get not number of slots!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void updateUI(boolean isSlotAvailable){
        TextView payButton = findViewById(R.id.payButton);
        TextView restName = findViewById(R.id.restName);
        TextView timeSlot = findViewById(R.id.timeslot);
        TextView available_slots =findViewById(R.id.availableSlots);
        TextView total_bill = findViewById(R.id.totalBill_info);
        if(isSlotAvailable){
            restName.setText(restaurant_name);
            timeSlot.setText(time_slot);
            available_slots.setText(updated_available_slots);
            total_bill.setText(totalBill);
            String temp = "Pay "+totalBill;
            payButton.setText(temp);
            payButton.setClickable(true);
            dialog.dismiss();

            payButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(User_review_order.this,"Order successfully placed! Thank you for using the app.... Enjoy your food",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(User_review_order.this,"No slots available in current time slot,please choose another",Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            payButton.setClickable(false);
        }
    }
}