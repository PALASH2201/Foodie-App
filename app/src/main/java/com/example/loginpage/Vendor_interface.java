package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class Vendor_interface extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String restaurant_name;
    private String restaurant_id ;
    boolean isDataRetrieved=false;

    String countMoneyEarned;
    public static final String EXTRA_NAME = "com.example.VendorMenu_viewer.extra.NAME";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_interface);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.orange));

        ImageView registerNow = findViewById(R.id.registerNow);

        AlertDialog.Builder builder = new AlertDialog.Builder(Vendor_interface.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button logout = findViewById(R.id.logout_btn);
        ImageView viewSlots = findViewById(R.id.viewSlots);
        ImageView viewLiveOrders = findViewById(R.id.viewLiveOrders);
        mAuth = FirebaseAuth.getInstance();

        String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        DatabaseReference vendorRef = FirebaseDatabase.getInstance().getReference("vendors").child(userID);
        vendorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    restaurant_name = snapshot.child("restaurant_name").getValue(String.class);
                    restaurant_id = snapshot.child("key").getValue(String.class);
                    if (restaurant_name == null) {
                         StyleableToast.makeText(Vendor_interface.this , "No restaurant name found for given vendorID",Toast.LENGTH_SHORT,R.style.warningToast).show();
                    }else{
                        isDataRetrieved = true;
                        getAnalytics();
                        dialog.dismiss();
                        startVendorMenuViewerActivity();
                    }
                }else{
                    StyleableToast.makeText(Vendor_interface.this , "Oops! We could not find you. Please register first",Toast.LENGTH_SHORT,R.style.warningToast).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(Vendor_interface.this , "Error in retrieving vendor information",Toast.LENGTH_SHORT,R.style.failureToast).show();
            }
        });

        registerNow.setOnClickListener(v -> {
            Intent intent =new Intent(Vendor_interface.this,Vendor_registration.class);
            startActivity(intent);
        });

        viewSlots.setOnClickListener(v -> {
            if(isDataRetrieved){
                Intent intent = new Intent(Vendor_interface.this,Vendor_view_timeslots.class);
                intent.putExtra("restaurant_name",restaurant_name);
                intent.putExtra("restaurant_id",restaurant_id);
                startActivity(intent);
            }
        });

        viewLiveOrders.setOnClickListener(v -> {
            if(isDataRetrieved){
                Intent intent = new Intent(Vendor_interface.this,Vendor_live_order_viewer.class);
                intent.putExtra("restaurant_id",restaurant_id);
                startActivity(intent);
            }
        });


        logout.setOnClickListener(v -> mAuth.signOut());

        mAuth.addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                startActivity(new Intent(Vendor_interface.this, VendorLogin.class));
                finish();
            }
        });


    }

    public void getAnalytics(){
        TextView pendingOrdersNum,completedOrdersNum,moneyEarned;
        pendingOrdersNum = findViewById(R.id.pendingOrdersNum);
        completedOrdersNum = findViewById(R.id.completedOrdersNum);
        moneyEarned = findViewById(R.id.moneyEarned);

        DatabaseReference restRef = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurant_id);
        restRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               pendingOrdersNum.setText(snapshot.child("Pending Orders").getValue(String.class));
               completedOrdersNum.setText(snapshot.child("Completed Orders").getValue(String.class));
               countMoneyEarned = "Rs: "+snapshot.child("Money Earned").getValue(String.class);
               moneyEarned.setText(countMoneyEarned);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(Vendor_interface.this,"Oops! Could not get your analytics. Try Again",Toast.LENGTH_SHORT,R.style.failureToast).show();
            }
        });
    }
    public void startVendorMenuViewerActivity(){

        ImageView viewCategories = findViewById(R.id.viewCategories);
        viewCategories.setOnClickListener(v -> {
            Intent intent =new Intent(Vendor_interface.this,VendorMenu_viewer.class);
            intent.putExtra(EXTRA_NAME, restaurant_name);
            startActivity(intent);
        });
    }
}