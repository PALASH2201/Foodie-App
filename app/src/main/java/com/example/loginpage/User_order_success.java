package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


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
        String customerName = intent.getStringExtra("customer Name");
        String customerContact = intent.getStringExtra("customer Contact");
        String dateTime = getCurrentDateTime();
        addToOrderHistory(total_bill,dateTime,time_slot_selected,day,restaurant_name,restaurant_id,customerName,customerContact);

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

    public void addToOrderHistory(String totalbill , String curDate,String time_slot_selected,String day,String restaurant_name,String restaurant_id,String customerName,String customerContact) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = rootRef.child("users").child(userId);
        DatabaseReference contactRef = userRef.child("contact_number");
        contactRef.setValue(customerContact);
        DatabaseReference nameRef = userRef.child("customer_name");
        nameRef.setValue(customerName);
        DatabaseReference cartRef = userRef.child("cart");
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                        DatabaseReference rest_id = orderHistoryRef.child("Restaurant_id");
                        rest_id.setValue(restaurant_id);
                        DatabaseReference dishRef = orderHistoryRef.child(Objects.requireNonNull(cartSnapshot.getKey()));
                        dishRef.setValue(cartSnapshot.getValue())
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        updateLiveOrders(restaurant_id,curDate,userId);
                                        updateSlots(day,restaurant_name,time_slot_selected);
                                        dialog.dismiss();
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
                assert old_slots != null;
                int new_slots = Integer.parseInt(old_slots) - 1;
                if(new_slots >= 0){
                    slotRef.child("available_slots").setValue(String.valueOf(new_slots));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                 Toast.makeText(User_order_success.this,"Database error has occurred",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void updateLiveOrders(String restaurant_id,String curdate,String userId){
         DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
         userRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 parseUserData(snapshot,restaurant_id,curdate,userId);
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {
                 Toast.makeText(User_order_success.this, "Could not retrieve user data to update live orders", Toast.LENGTH_SHORT).show();
             }
         });
    }

    private void parseUserData(DataSnapshot dataSnapshot,String restaurant_id,String curdate,String userId) {
        String orderId,customerName,timeSlot,totalBill,customerContact;
        DataSnapshot userSnapshot = dataSnapshot.child(userId);
            customerName = userSnapshot.child("customer_name").getValue(String.class);
            customerContact = userSnapshot.child("contact_number").getValue(String.class);
            DataSnapshot orderHistorySnapshot = userSnapshot.child("order_history");
            DataSnapshot dateTimeSnapshot = orderHistorySnapshot.child(curdate);
            orderId = dateTimeSnapshot.child("Order Id").getValue(String.class);
            timeSlot = dateTimeSnapshot.child("Time Slot").getValue(String.class);
            totalBill = dateTimeSnapshot.child("Total Bill").getValue(String.class);

        List<LiveOrderDishDataClass> dishes = new ArrayList<>();
            DataSnapshot cartsnapshot = userSnapshot.child("cart");
            for (DataSnapshot dishSnapshot : cartsnapshot.getChildren()) {
                    String dishName = dishSnapshot.child("dish_name").getValue(String.class);
                    String dishQuantity = dishSnapshot.child("quantity").getValue(String.class);
                    String dishPrice = dishSnapshot.child("total_price").getValue(String.class);

                    LiveOrderDishDataClass dish = new LiveOrderDishDataClass(dishQuantity, dishName, dishPrice);
                    dishes.add(dish);
            }
        LiveOrderDataClass liveOrder = new LiveOrderDataClass(timeSlot,"Pending",customerName,customerContact,orderId,totalBill,dishes);
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurant_id).child("Live Orders").child(orderId);
        ordersRef.setValue(liveOrder).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(User_order_success.this,"Could not update live orders",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart");
                cartRef.removeValue();
            }
        });
    }
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

}