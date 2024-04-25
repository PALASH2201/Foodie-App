package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class Vendor_live_order_viewer extends AppCompatActivity {

    private VendorLiveOrder_myAdapter adapter;
    private AlertDialog dialog;
    private List<LiveOrderDataClass> liveOrderDataClassList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vendor_live_order_viewer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.orange));

        Intent intent = getIntent();
        String restaurant_id = intent.getStringExtra("restaurant_id");



        AlertDialog.Builder builder = new AlertDialog.Builder(Vendor_live_order_viewer.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();


        retrieveLiveOrders(restaurant_id);

    }
    public void retrieveLiveOrders(String restaurant_id){
        liveOrderDataClassList = new ArrayList<>();

        DatabaseReference liveOrderRef = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurant_id).child("Live Orders");
        liveOrderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, List<LiveOrderDishDataClass>> dishMap = new HashMap<>();

                liveOrderDataClassList.clear();
                for (DataSnapshot liveOrderSnapshot: snapshot.getChildren()) {
                      String chosen_time_slot = liveOrderSnapshot.child("chosen_time_slot").getValue(String.class);
                      String customerBill = liveOrderSnapshot.child("customerBill").getValue(String.class);
                      String customerName = liveOrderSnapshot.child("customerName").getValue(String.class);
                      String customerContact = liveOrderSnapshot.child("customerContact").getValue(String.class);
                      String customerToken = liveOrderSnapshot.child("customerToken").getValue(String.class);
                      String orderId = liveOrderSnapshot.child("orderId").getValue(String.class);
                      String orderStatus = liveOrderSnapshot.child("orderStatus").getValue(String.class);

                      if(orderStatus != null && orderStatus.equals("Takeaway successful")){
                          assert orderId != null;
                          DatabaseReference orderRef = liveOrderRef.child(orderId);
                          orderRef.removeValue();
                          continue;
                      }

                      DataSnapshot dishSnapshot = liveOrderSnapshot.child("dishList");
                    List<LiveOrderDishDataClass> dishList = new ArrayList<>();
                    for (DataSnapshot dishInfo: dishSnapshot.getChildren()) {
                        String dishName = dishInfo.child("dishName").getValue(String.class);
                        String dishQ = dishInfo.child("dishQ").getValue(String.class);
                        String totalPrice = dishInfo.child("totalPrice").getValue(String.class);

                        LiveOrderDishDataClass liveOrderDishDataClass = new LiveOrderDishDataClass(dishQ,dishName,totalPrice);
                        dishList.add(liveOrderDishDataClass);
                    }
                      dishMap.put(orderId, dishList);
                    assert customerToken != null;
                    Log.d("Token in order viewer",customerToken);
                      LiveOrderDataClass liveOrderDataClass = new LiveOrderDataClass(chosen_time_slot,orderStatus,customerName,customerContact,customerToken,orderId,customerBill,dishList);
                      liveOrderDataClassList.add(liveOrderDataClass);

                }

                RecyclerView recyclerView = findViewById(R.id.recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(Vendor_live_order_viewer.this, LinearLayoutManager.VERTICAL, false));
                adapter = new VendorLiveOrder_myAdapter(Vendor_live_order_viewer.this, liveOrderDataClassList, dishMap,restaurant_id);
                recyclerView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(Vendor_live_order_viewer.this,"Failed to retrieve live orders. Try Again!!!",Toast.LENGTH_SHORT,R.style.failureToast).show();
            }
        });
    }
}