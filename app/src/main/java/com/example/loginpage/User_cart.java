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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User_cart extends AppCompatActivity {

    private Cart_myAdapter cartMyAdapter;
    private AlertDialog dialog;
    List<CartDataClass> dataList ;
    String userId,restaurant_name , restaurant_id;
    double subTotal ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cart);

        TextView subtotal= findViewById(R.id.subTotal);
        TextView totalBill = findViewById(R.id.totalBill);

        Intent intent = getIntent();
        userId = intent.getStringExtra("User Id");
        restaurant_name = intent.getStringExtra("restaurant_name");
        restaurant_id = intent.getStringExtra("restaurant_id");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        AlertDialog.Builder builder = new AlertDialog.Builder(User_cart.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();
        cartMyAdapter = new Cart_myAdapter(User_cart.this , dataList,subtotal,totalBill);
        recyclerView.setAdapter(cartMyAdapter);

        getCartDetails(userId);

        TextView cartCheckoutBtn = findViewById(R.id.cartCheckoutBtn);
        cartCheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_cart.this,User_time_slot_viewer.class);
                intent.putExtra("restaurant_name",restaurant_name);
                intent.putExtra("restaurant_id",restaurant_id);
                intent.putExtra("total bill",totalBill.getText().toString());
                startActivity(intent);
            }
        });
    }
    public void getCartDetails(String userId){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("cart")) {
                        DataSnapshot cartSnapshot = dataSnapshot.child("cart");
                        for (DataSnapshot cartItem : cartSnapshot.getChildren()) {
                            String dishId = cartItem.getKey();
                            DatabaseReference dishRef = userRef.child("cart").child(dishId);
                            dishRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dishSnapshot) {
                                    if (dishSnapshot.exists()) {
                                        Log.d("Dish exists",dishId);
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
                                    Toast.makeText(User_cart.this, "Could not fetch the cart properly", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(User_cart.this, "Cart is empty!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(User_cart.this, "User or cart does not exist", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(User_cart.this, "Database error. Try again", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

}