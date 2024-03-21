package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Vendor_interface extends AppCompatActivity {
    private TextView addCategories , registerNow;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_interface);

        addCategories = findViewById(R.id.addCategories);
        registerNow = findViewById(R.id.registerNow);

        addCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Vendor_interface.this,Vendor_category_upload.class);
                startActivity(intent);
            }
        });

        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Vendor_interface.this,Vendor_registration.class);
                startActivity(intent);
            }
        });
    }
}