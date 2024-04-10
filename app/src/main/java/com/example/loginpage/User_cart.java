package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User_cart extends AppCompatActivity {

    private Cart_myAdapter cartMyAdapter;
    private AlertDialog dialog;
    List<CartDataClass> dataList ;
    String userId  ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cart);

        Intent intent = getIntent();
        userId = intent.getStringExtra("User Id");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        AlertDialog.Builder builder = new AlertDialog.Builder(User_cart.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();
        cartMyAdapter = new Cart_myAdapter(User_cart.this , dataList);
        recyclerView.setAdapter(cartMyAdapter);

        getCartDetails(userId);
    }
    public void getCartDetails(String userId){
        DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart");
        userCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot cartItem : dataSnapshot.getChildren()) {
                        String dishId = cartItem.getKey();
                        assert dishId != null;
                        Log.d("DishId",dishId);
                        DatabaseReference dishRef = userCartRef.child(dishId);
                        dishRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dishSnapshot) {
                                if (dishSnapshot.exists()) {
                                    Log.d("Dish exits",dishId);
                                    String dishName = dishSnapshot.child("dish_name").getValue(String.class);
                                    String dishPrice = dishSnapshot.child("dish_price").getValue(String.class);
                                    String restaurantName = dishSnapshot.child("restaurant_name").getValue(String.class);
                                    String categoryName = dishSnapshot.child("category_name").getValue(String.class);
                                    String dishImageUrl = dishSnapshot.child("dish_image_url").getValue(String.class);
                                    String restaurant_id = dishSnapshot.child("restaurant_id").getValue(String.class);
                                    String category_id = dishSnapshot.child("category_id").getValue(String.class);
                                    int quantity = Integer.parseInt(Objects.requireNonNull(dishSnapshot.child("quantity").getValue(String.class)));
                                    assert dishPrice != null;
                                    String total_price_dish = String.valueOf( Double.parseDouble(dishPrice) * quantity);
                                    CartDataClass cartItem = new CartDataClass(dishName, dishPrice,total_price_dish ,dishImageUrl,restaurant_id,category_id,dishId,quantity,restaurantName,categoryName);
                                    dataList.add(cartItem);
                                    cartMyAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(User_cart.this,"Could not fetch the cart properly",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(User_cart.this,"Cart is empty!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(User_cart.this,"Database error. Try again",Toast.LENGTH_SHORT).show();
            }
        });

    }
}