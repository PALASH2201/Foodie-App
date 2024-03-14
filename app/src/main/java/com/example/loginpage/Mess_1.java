package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Mess_1 extends AppCompatActivity {
    Button button ;

    TextView mess_name ;
    FirebaseAuth mAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess1);
        mess_name = findViewById(R.id.mess_name);
        Intent intent = getIntent();
        String name = intent.getStringExtra(MainActivity.EXTRA_NAME_1);
    }
}