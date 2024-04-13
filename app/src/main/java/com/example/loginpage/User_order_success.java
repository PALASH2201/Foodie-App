package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class User_order_success extends AppCompatActivity {

    Animation topAnim , bottomAnim;
    AlertDialog dialog;
    TextView orderSuccessHeader, enjoyFoodFooter;
    ImageView successImg;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_order_success);

        AlertDialog.Builder builder = new AlertDialog.Builder(User_order_success.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        Intent intent = getIntent();
        String total_bill = intent.getStringExtra("total bill");
        String restaurant_name = intent.getStringExtra("restaurant_name");
        String restaurant_id = intent.getStringExtra("restaurant_id");
        String time_slot_selected = intent.getStringExtra("time_slot_selected");
        String day = intent.getStringExtra("day");
        String dateTime = getCurrentDateTime();
        addToOrderHistory(total_bill,dateTime,time_slot_selected,day,restaurant_name,restaurant_id);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        orderSuccessHeader = findViewById(R.id.orderSuccessHeader);
        enjoyFoodFooter = findViewById(R.id.enjoyFoodFooter);
        successImg = findViewById(R.id.successImg);

        orderSuccessHeader.setAnimation(topAnim);
        enjoyFoodFooter.setAnimation(bottomAnim);
        successImg.setAnimation(bottomAnim);

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void addToOrderHistory(String totalbill , String curDate,String time_slot_selected,String day,String restaurant_name,String restaurant_id) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference cartRef = rootRef.child("users").child(userId).child("cart");
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                        DatabaseReference orderHistoryRef = rootRef.child("users").child(userId)
                                .child("order_history").child(curDate);
                        String orderId = orderHistoryRef.push().getKey();
                        orderHistoryRef.child("Order Id").setValue(orderId);
                        DatabaseReference billRef = orderHistoryRef.child("Total Bill");
                        billRef.setValue(totalbill);
                        DatabaseReference time_slot_ref = orderHistoryRef.child("Time Slot");
                        time_slot_ref.setValue(time_slot_selected);
                        DatabaseReference day_ref = orderHistoryRef.child("Day");
                        day_ref.setValue(day);
                        DatabaseReference dishRef = orderHistoryRef.child(cartSnapshot.getKey());
                        dishRef.setValue(cartSnapshot.getValue())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            cartSnapshot.getRef().removeValue();
                                            updateSlots(day,restaurant_name,time_slot_selected);
                                            dialog.dismiss();
                                        }
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(User_order_success.this,"Database error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateSlots(String day, String restaurant_name,String slot){
        DatabaseReference slotRef = FirebaseDatabase.getInstance().getReference("time_slots").child(day).child(restaurant_name).child(slot);
        slotRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String old_slots = snapshot.child("available_slots").getValue(String.class);
                int new_slots = Integer.parseInt(old_slots) - 1;
                if(new_slots >= 0){
                    slotRef.child("available_slots").setValue(String.valueOf(new_slots));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                 Toast.makeText(User_order_success.this,"Database error has occured",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

}