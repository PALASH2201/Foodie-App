package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Vendor_interface extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String restaurant_name;
    private String restaurant_id ;
    boolean isDataRetrieved=false;

    public static final String EXTRA_NAME = "com.example.VendorMenu_viewer.extra.NAME";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_interface);


        ImageButton registerNow = findViewById(R.id.registerNow);

        AlertDialog.Builder builder = new AlertDialog.Builder(Vendor_interface.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button logout = findViewById(R.id.logout_btn);
        ImageButton viewSlots = findViewById(R.id.viewSlots);
        ImageButton viewLiveOrders = findViewById(R.id.viewLiveOrders);
        mAuth = FirebaseAuth.getInstance();

        String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Log.d("User ID:" , userID);

        DatabaseReference vendorRef = FirebaseDatabase.getInstance().getReference("vendors").child(userID);
        vendorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    restaurant_name = snapshot.child("restaurant_name").getValue(String.class);
                    restaurant_id = snapshot.child("key").getValue(String.class);
                    if (restaurant_name == null) {
                         Toast.makeText(Vendor_interface.this , "No restaurant name found for given vendorID",Toast.LENGTH_SHORT).show();
                    }else{
                        isDataRetrieved = true;
                        dialog.dismiss();
                        startVendorMenuViewerActivity();
                    }
                }else{
                    Toast.makeText(Vendor_interface.this , "No vendor found for given vendorID",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Vendor_interface.this , "Error in retrieving vendor information",Toast.LENGTH_SHORT).show();
            }
        });
        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Vendor_interface.this,Vendor_registration.class);
                startActivity(intent);
            }
        });

            viewSlots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isDataRetrieved){
                        Intent intent = new Intent(Vendor_interface.this,Vendor_view_timeslots.class);
                        intent.putExtra("restaurant_name",restaurant_name);
                        Log.d("sending rest name",restaurant_name);
                        intent.putExtra("restaurant_id",restaurant_id);
                        Log.d("sending rest id",restaurant_id);
                        startActivity(intent);
                    }
                }
            });

            viewLiveOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isDataRetrieved){
                        Intent intent = new Intent(Vendor_interface.this,Vendor_live_order_viewer.class);
                        intent.putExtra("restaurant_id",restaurant_id);
                        startActivity(intent);
                    }
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
    public void startVendorMenuViewerActivity(){

        ImageButton viewCategories = findViewById(R.id.viewCategories);
        viewCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Vendor_interface.this,VendorMenu_viewer.class);
                intent.putExtra(EXTRA_NAME, restaurant_name);
                Log.d("Name",restaurant_name);
                startActivity(intent);
            }
        });
    }
}