package com.example.loginpage;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import soup.neumorphism.NeumorphButton;

public class User_menu_detail extends AppCompatActivity {

    private UserDishDetails_myAdapter adapter;
    private AlertDialog dialog;
    List<DishDataClass> dataList ;

    String extra_restaurant_name , extra_category_name , extra_restaurant_id , extra_category_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_menu_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        extra_restaurant_name = intent.getStringExtra(Mess_1.EXTRA_REST_NAME);
        extra_category_name = intent.getStringExtra(Mess_1.EXTRA_CAT_NAME);
        extra_restaurant_id = intent.getStringExtra(Mess_1.EXTRA_REST_ID);
        extra_category_id = intent.getStringExtra(Mess_1.EXTRA_CAT_ID);
        TextView categoryName = findViewById(R.id.categoryName);
        categoryName.setText(extra_category_name);


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        AlertDialog.Builder builder = new AlertDialog.Builder(User_menu_detail.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();
        adapter = new UserDishDetails_myAdapter(User_menu_detail.this , dataList);
        recyclerView.setAdapter(adapter);

        retrieveDishIdByName(extra_category_id);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        checkCart(userId);
    }

    private void retrieveDishIdByName(String extra_category_id) {

        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("categories").child(extra_category_id).child("dishes");
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> dishIds = new ArrayList<>();
                    for (DataSnapshot dishSnapshot : dataSnapshot.getChildren()) {
                        String dishId = dishSnapshot.getValue(String.class);
                        dishIds.add(dishId);
                        assert dishId != null;
                        Log.d("DishID(User-side)",dishId);
                    }
                    Log.d("List Length(User-side)" , dishIds.size()+"");
                    fetchDishDetails(dishIds);
                } else {
                    Toast.makeText(User_menu_detail.this , "No dish present for given category",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(User_menu_detail.this , "Error in retrieving ids of dishes",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDishDetails(List<String> dishIds) {
        DatabaseReference dishesRef = FirebaseDatabase.getInstance().getReference("dishes");
        for (String dishId : dishIds) {
            DatabaseReference dishIdRef = dishesRef.child(dishId);
            dishIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String dishName = dataSnapshot.child("dish_name").getValue(String.class);
                        String dishImageURL = dataSnapshot.child("dish_image_url").getValue(String.class);
                        String categoryId = dataSnapshot.child("category_id").getValue(String.class);
                        String dishPrice = dataSnapshot.child("dish_price").getValue(String.class);
                        String restaurantId = dataSnapshot.child("restaurant_id").getValue(String.class);
                        String dishDescription = dataSnapshot.child("dish_description").getValue(String.class);
                        DishDataClass dataClass = new DishDataClass(dishName,dishDescription,dishPrice,dishImageURL,restaurantId,categoryId,dishId);
                        dataList.add(dataClass);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(User_menu_detail.this , "No dish present for given id",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(User_menu_detail.this , "Error in retrieving dish details",Toast.LENGTH_SHORT).show();
                }
            });
        }
        dialog.dismiss();
    }
    public void checkCart(String userId){
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("cart");
        NeumorphButton viewCartOption = findViewById(R.id.view_cart_option);

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("inside check cart" , "cart exists for user"+userId);
                    if (dataSnapshot.getChildrenCount() > 0) {
                        Log.d("inside children count" , "cart exits for user"+userId);
                        viewCartOption.setVisibility(View.VISIBLE);
                        viewCartOption.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("Check Cart successful" ,"user Clicked" );
                                Intent intent = new Intent(User_menu_detail.this,User_cart.class);
                                intent.putExtra("User Id",userId);
                                intent.putExtra("restaurant_name",extra_restaurant_name);
                                intent.putExtra("restaurant_id",extra_restaurant_id);
                                startActivity(intent);
                            }
                        });
                    } else {
                        viewCartOption.setVisibility(View.GONE);
                    }
                } else {
                    Log.d("inside check cart" , "cart does not exists for user"+userId);
                    viewCartOption.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(User_menu_detail.this,"Error in retrieving cart information",Toast.LENGTH_SHORT).show();
            }
        });
    }
}