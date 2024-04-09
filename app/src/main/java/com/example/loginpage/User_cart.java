package com.example.loginpage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class User_cart extends AppCompatActivity {

    private Cart_myAdapter cartMyAdapter;
    private AlertDialog dialog;
    List<CartDataClass> dataList ;

    String extra_restaurant_name , extra_category_name , extra_restaurant_id , extra_category_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cart);

        Intent intent = getIntent();
        extra_restaurant_name = intent.getStringExtra("Restaurant Name");
        extra_category_name = intent.getStringExtra("Category Name");
        extra_restaurant_id = intent.getStringExtra("Restaurant Id");
        extra_category_id = intent.getStringExtra("Category Id");

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


    }
}