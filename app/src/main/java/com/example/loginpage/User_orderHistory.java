package com.example.loginpage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class User_orderHistory extends Fragment {

    private AlertDialog dialog;
    private List<UserOrderHistoryDataClass> userOrderHistoryDataClassList;
    private UserOrderHistory_myAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view =inflater.inflate(R.layout.fragment_user_order_history, container, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        retrieveLiveOrders(user_id,view);

        return view;
    }
    public void retrieveLiveOrders(String user_id,View view){
        userOrderHistoryDataClassList = new ArrayList<>();

        DatabaseReference orderHistoryRef = FirebaseDatabase.getInstance().getReference("users").child(user_id).child("order_history");
        orderHistoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, List<UserOrderHistoryDishDataClass>> dishMap = new HashMap<>();

                userOrderHistoryDataClassList.clear();
                for (DataSnapshot orderHistorySnapshot: snapshot.getChildren()) {
                    String order_time = orderHistorySnapshot.getKey();
                    String chosen_time_slot = orderHistorySnapshot.child("Time Slot").getValue(String.class);
                    String customerBill = orderHistorySnapshot.child("Total Bill").getValue(String.class);
                    String Day = orderHistorySnapshot.child("Day").getValue(String.class);
                    String orderId = orderHistorySnapshot.child("Order Id").getValue(String.class);
                    String rest_id = orderHistorySnapshot.child("Restaurant_id").getValue(String.class);

                    List<UserOrderHistoryDishDataClass> dishList = new ArrayList<>();
                    for (DataSnapshot dishInfo: orderHistorySnapshot.getChildren()) {
                        if(!dishInfo.getKey().equals("Time Slot") && !dishInfo.getKey().equals("Total Bill") && !dishInfo.getKey().equals("Order Id") && !dishInfo.getKey().equals("Day")){
                            String dishName = dishInfo.child("dish_name").getValue(String.class);
                            String dishQ = dishInfo.child("quantity").getValue(String.class);
                            String totalPrice = dishInfo.child("total_price").getValue(String.class);

                            if (dishName != null && dishQ != null && totalPrice != null) {
                                UserOrderHistoryDishDataClass userOrderHistoryDishDataClass = new UserOrderHistoryDishDataClass(dishName, dishQ, totalPrice);
                                dishList.add(userOrderHistoryDishDataClass);
                            }
                        }
                    }
                    dishMap.put(orderId, dishList);

                    UserOrderHistoryDataClass userOrderHistoryDataClass = new UserOrderHistoryDataClass(Day,orderId,order_time,chosen_time_slot,customerBill,rest_id,dishMap);
                    userOrderHistoryDataClassList.add(userOrderHistoryDataClass);

                }

                RecyclerView recyclerView = view.findViewById(R.id.recycler_view_order_history);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                adapter = new UserOrderHistory_myAdapter(userOrderHistoryDataClassList, getContext(), dishMap);
                recyclerView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Failed to retrieve live orders. Try Again!!!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}