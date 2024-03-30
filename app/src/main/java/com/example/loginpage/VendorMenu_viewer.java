package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VendorMenu_viewer extends AppCompatActivity {

    private Categories_myAdapter adapter;

    List<CategoriesDataClass> dataList ;

    private TextView restaurant_name ;

    private AlertDialog dialog;
    RecyclerView recyclerView;

    ValueEventListener eventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vendor_menu_viewer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        restaurant_name = findViewById(R.id.mess_name);
        Intent intent = getIntent();
        String name = intent.getStringExtra(MainActivity.EXTRA_NAME);
        restaurant_name.setText(name);

        recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(VendorMenu_viewer.this,1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(VendorMenu_viewer.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();

        adapter = new Categories_myAdapter(VendorMenu_viewer.this , dataList,true);
        recyclerView.setAdapter(adapter);

        retrieveRestaurantIdByName();
    }

    private void retrieveRestaurantIdByName() {
        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference("restaurants");
        restaurantRef.orderByChild("restaurant_name").equalTo(restaurant_name.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot restaurantSnapshot : snapshot.getChildren()) {
                        String restaurantId = restaurantSnapshot.getKey();
                        Log.d("Restaurant Id", "Id: " + restaurantId);
                        HandleDatabase(restaurantId);
                    }
                } else {
                    Toast.makeText(VendorMenu_viewer.this, "No restaurant found with the specified name", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VendorMenu_viewer.this, "Failed to fetch restaurant ID: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void HandleDatabase(String restaurant_id){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("categories");
        eventListener = databaseReference.orderByChild("restaurant_id").equalTo(restaurant_id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for(DataSnapshot categorySnapshot : snapshot.getChildren()){
                    String name = categorySnapshot.child("name").getValue(String.class);
                    String imageUrl = categorySnapshot.child("image_url").getValue(String.class);
                    CategoriesDataClass dataClass = new CategoriesDataClass(name, imageUrl,restaurant_id);
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

    }
}