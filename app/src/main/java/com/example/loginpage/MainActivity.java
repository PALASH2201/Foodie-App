package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;



    TextView Mess_1 , Mess_2 , Mess_3 ;
    public static final String EXTRA_NAME = "com.example.Mess_1.extra.NAME";
    public static final String EXTRA_ID = "com.example.Mess_1.extra.ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Mess_1 = findViewById(R.id.Mess_1);
        Mess_2 = findViewById(R.id.Mess_2);
        Mess_3 = findViewById(R.id.Mess_3);

        if(user == null || !user.isEmailVerified()){
            Intent intent =new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }


        Mess_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    retrieveVendorUserKeyByName();
                Intent intent = new Intent(getApplicationContext(),Mess_1.class);
                intent.putExtra(EXTRA_NAME,Mess_1.getText().toString());
            //    intent.putExtra(EXTRA_ID, restaurant_id);
                startActivity(intent);
                finish();
            }
        });

        Mess_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   retrieveVendorUserKeyByName();
                Intent intent = new Intent(getApplicationContext(),Mess_1.class);
                intent.putExtra(EXTRA_NAME,Mess_2.getText().toString());
            //    intent.putExtra(EXTRA_ID, restaurant_id);
                startActivity(intent);
                finish();
            }
        });

        Mess_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   retrieveVendorUserKeyByName();
                Intent intent = new Intent(getApplicationContext(),Mess_1.class);
                intent.putExtra(EXTRA_NAME,Mess_3.getText().toString());
            //    intent.putExtra(EXTRA_ID, restaurant_id);
                startActivity(intent);
                finish();
            }
        });



    }



}