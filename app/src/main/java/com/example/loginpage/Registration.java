package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Registration extends AppCompatActivity {
    TextInputEditText editTextEmail , editTextPassword , editName , editContact;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser updatedUser = mAuth.getCurrentUser();
                        if (updatedUser != null && updatedUser.isEmailVerified()) {
                            SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                            editor.putBoolean("isEmailVerified", true);
                            editor.apply();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // User's email is not verified, prompt them to verify
                            Toast.makeText(Registration.this, "Please verify your email to login.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Error refreshing user data
                        Toast.makeText(Registration.this, "Failed to check email verification status.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        String userid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editName = findViewById(R.id.name_reg);
        editContact = findViewById(R.id.contact_reg);
        buttonReg = findViewById(R.id.btn_register);
        progressBar =findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userid);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email,password,name,contact;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                name = String.valueOf(editName.getText());
                contact = String.valueOf(editContact.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Registration.this,"Please enter your email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Registration.this,"Please enter a minimum 6 character password",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(Registration.this,"Please enter your name",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(contact)){
                    Toast.makeText(Registration.this,"Please enter your contact",Toast.LENGTH_SHORT).show();
                    return;
                }

                userRef.child("customer_name").setValue(name);
                userRef.child("email_id").setValue(email);
                userRef.child("contact_number").setValue(contact);

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(Registration.this, "Account created.Please verify your email id",
                                                        Toast.LENGTH_SHORT).show();
                                                SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                                                editor.putBoolean("isEmailVerified", false);
                                                editor.apply();
                                                mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (mAuth.getCurrentUser().isEmailVerified()) {
                                                            // Email is verified, allow user to log in
                                                            Intent intent = new Intent(getApplicationContext(),Login.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                });
                                            }
                                            else{
                                                Toast.makeText(Registration.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Registration.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }
}