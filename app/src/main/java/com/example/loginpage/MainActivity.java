package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button ;
    FirebaseUser user;

    TextView Mess_1 , Mess_2 , Mess_3 ;
    public static final String EXTRA_NAME_1 = "com.example.Mess_1.extra.NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
       // button = findViewById(R.id.logout);
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
                Intent intent = new Intent(getApplicationContext(),Mess_1.class);
                intent.putExtra(EXTRA_NAME_1,Mess_1.getText().toString());
                startActivity(intent);
                finish();
            }
        });
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent =new Intent(getApplicationContext(), Login.class);
//                startActivity(intent);
//                finish();
//            }
//        });
    }
}