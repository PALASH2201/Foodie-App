package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Vendor_interface extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_interface);

        TextView addCategories = findViewById(R.id.addCategories);
        TextView registerNow = findViewById(R.id.registerNow);
        TextView addDishes = findViewById(R.id.addDishes);
        Button logout = findViewById(R.id.logout_btn);
        mAuth = FirebaseAuth.getInstance();

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

        addDishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Vendor_interface.this,Vendor_Add_Menu.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(Vendor_interface.this, VendorLogin.class));
                    finish();
                }
            }
        });
    }
}