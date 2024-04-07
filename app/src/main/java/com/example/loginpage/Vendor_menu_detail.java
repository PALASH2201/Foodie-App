package com.example.loginpage;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

public class Vendor_menu_detail extends AppCompatActivity {

    private AlertDialog dialog;
    private VendorDishDetails_myAdapter adapter;
    private List<DishDataClass> dataList ;

    RecyclerView recyclerView;
    String extra_restaurant_name , extra_category_name , extra_restaurant_id , extra_category_id;

    public static final String EXTRA_CAT_NAME = "com.example.Vendor_add_Menu.extra.CAT_NAME";
    public static final String EXTRA_REST_NAME = "com.example.Vendor_add_Menu.extra.REST_NAME";
    public static final String EXTRA_CAT_ID = "com.example.Vendor_add_Menu.extra.CAT_ID";
    public static final String EXTRA_REST_ID = "com.example.Vendor_add_Menu.extra.REST_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vendor_menu_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        extra_restaurant_name = intent.getStringExtra(VendorMenu_viewer.EXTRA_REST_NAME);
        extra_category_name = intent.getStringExtra(VendorMenu_viewer.EXTRA_CAT_NAME);
        extra_restaurant_id = intent.getStringExtra(VendorMenu_viewer.EXTRA_REST_ID);
        extra_category_id = intent.getStringExtra(VendorMenu_viewer.EXTRA_CAT_ID);

     //   Log.d("Extra Category ID:" , extra_category_id);

        TextView restaurant_name = findViewById(R.id.mess_name);
        restaurant_name.setText(extra_restaurant_name);

        recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(Vendor_menu_detail.this,1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(Vendor_menu_detail.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();

        adapter = new VendorDishDetails_myAdapter(Vendor_menu_detail.this , dataList);
        recyclerView.setAdapter(adapter);

        retrieveDishIdByName(extra_category_id);

        TextView addDishes = findViewById(R.id.addDishes);
        addDishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Vendor_menu_detail.this , Vendor_Add_Menu.class);
                intent.putExtra(EXTRA_CAT_NAME,extra_category_name);
                intent.putExtra(EXTRA_REST_NAME,extra_restaurant_name);
                intent.putExtra(EXTRA_CAT_ID,extra_category_id);
                intent.putExtra(EXTRA_REST_ID,extra_restaurant_id);
                startActivity(intent);
            }
        });

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
                        Log.d("DishID",dishId);
                    }
                    Log.d("List Length" , dishIds.size()+"");
                    fetchDishDetails(dishIds);
                } else {
                    Toast.makeText(Vendor_menu_detail.this , "No dish present for given category",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Vendor_menu_detail.this , "Error in retrieving ids of dishes",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Vendor_menu_detail.this , "No dish present for given id",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Vendor_menu_detail.this , "Error in retrieving dish details",Toast.LENGTH_SHORT).show();
                }
            });
        }
        dialog.dismiss();
    }
}