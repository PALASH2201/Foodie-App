package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    private AlertDialog dialog;

    private Mess_myAdapter adapter;
    private List<VendorDataClass> vendorList;

    public static final String EXTRA_NAME = "com.example.Mess_1.extra.NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(user == null || !user.isEmailVerified()){
            Intent intent =new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        RecyclerView recyclerView = findViewById(R.id.mess_choice_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this,1);
        recyclerView.setLayoutManager(gridLayoutManager);

        vendorList = new ArrayList<>();
        adapter = new Mess_myAdapter(this,vendorList);
        recyclerView.setAdapter(adapter);


        // Initialize Firebase
        DatabaseReference vendorsRef = FirebaseDatabase.getInstance().getReference("vendors");

        // Read from the database
        vendorsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vendorList.clear();
                for (DataSnapshot vendorSnapshot : dataSnapshot.getChildren()) {
                    String restaurantName = vendorSnapshot.child("restaurant_name").getValue(String.class);
                    String profilePicUrl = vendorSnapshot.child("profile_pic_image_url").getValue(String.class);
                    VendorDataClass vendor = new VendorDataClass(restaurantName, profilePicUrl);
                    vendorList.add(vendor);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"No restaurant found!",Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnItemClickListener(new Mess_myAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                VendorDataClass clickedVendor = vendorList.get(position);
                String restaurantName = clickedVendor.getRestaurant_name();
                Intent intent = new Intent(MainActivity.this, Mess_1.class);
                intent.putExtra(EXTRA_NAME, restaurantName);
                startActivity(intent);
            }
        });

    }
}