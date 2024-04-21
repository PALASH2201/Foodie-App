package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class UserOrderHistory_myAdapter extends RecyclerView.Adapter<UserOrderHistory_myViewHolder> {
    private final List<UserOrderHistoryDataClass> orderHistoryList;

    private final Context context;
    String temp_var,order_status;
    private final Map<String, List<UserOrderHistoryDishDataClass>> dishMap;

    public UserOrderHistory_myAdapter(List<UserOrderHistoryDataClass> orderHistoryList, Context context,Map<String, List<UserOrderHistoryDishDataClass>> dishMap) {
        this.orderHistoryList = orderHistoryList;
        this.context = context;
        this.dishMap = dishMap;
    }

    @NonNull
    @Override
    public UserOrderHistory_myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_history_recycler_item, parent, false);
        return new UserOrderHistory_myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserOrderHistory_myViewHolder holder, @SuppressLint("RecyclerView") int position) {
        getOrderStatus(orderHistoryList.get(position).getRest_id(),orderHistoryList.get(position).getOrderId(),holder);
        holder.orderDay.setText(orderHistoryList.get(position).getDay());
        holder.orderTime.setText(orderHistoryList.get(position).getOrderTime());
        temp_var = "Order Id: "+orderHistoryList.get(position).getOrderId();
        holder.orderId.setText(temp_var);
        String slot_timing = "Timeslot: "+orderHistoryList.get(position).getTimeSlot();
        holder.chosen_time_slot.setText(slot_timing);
        temp_var = "Total Bill: "+orderHistoryList.get(position).getTotalBill();
        holder.customerBill.setText(temp_var);

        List<UserOrderHistoryDishDataClass> dishList = dishMap.get(orderHistoryList.get(position).getOrderId());

        holder.dish_details_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        UserOrderHistoryDishes_myAdapter adapter = new UserOrderHistoryDishes_myAdapter(dishList,context);
        holder.dish_details_recycler_view.setAdapter(adapter);

        holder.updateStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatus(orderHistoryList.get(position).getRest_id(),orderHistoryList.get(position).getOrderId(),holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }

    public void getOrderStatus(String restaurant_id , String orderId,@NonNull UserOrderHistory_myViewHolder holder){
        Log.d("Rest-id-order-status",restaurant_id);
        Log.d("Order-id-order-status",orderId);
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurant_id).child("Live Orders").child(orderId);
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    order_status = snapshot.child("orderStatus").getValue(String.class);
                    holder.orderStatus.setText(order_status);
                    if(order_status.equals("Ready for Pickup")){
                        holder.orderStatus.setBackgroundColor(ContextCompat.getColor(context,R.color.green));
                    }
                    else if(order_status.equals("Pending")){
                        holder.orderStatus.setBackgroundColor(ContextCompat.getColor(context,R.color.red));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,"Database error! Try Again",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void updateStatus(String restaurant_id , String orderId,@NonNull UserOrderHistory_myViewHolder holder){
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurant_id).child("Live Orders").child(orderId);
        if(holder.orderStatus.getText().toString().equals("Ready for Pickup")){
            order_status = "Takeaway successful";
            orderRef.child("orderStatus").setValue(order_status);
            holder.orderStatus.setText(order_status);
        }else{
            Toast.makeText(context,"Order is still being prepared",Toast.LENGTH_SHORT).show();
        }
    }
}

class UserOrderHistory_myViewHolder extends  RecyclerView.ViewHolder{
    TextView orderTime , orderDay ,chosen_time_slot,orderId,customerBill,orderStatus;
    Button updateStatusButton;
    LinearLayout orderDetails;
    RecyclerView dish_details_recycler_view;
    public UserOrderHistory_myViewHolder(@NonNull View itemView) {
        super(itemView);
        updateStatusButton = itemView.findViewById(R.id.updateStatusButton);
        orderStatus = itemView.findViewById(R.id.orderStatus);
        orderDay = itemView.findViewById(R.id.orderDay);
        orderTime = itemView.findViewById(R.id.orderTime);
        chosen_time_slot = itemView.findViewById(R.id.chosen_time_slot);
        orderId = itemView.findViewById(R.id.orderId);
        customerBill = itemView.findViewById(R.id.customerBill);
        orderDetails = itemView.findViewById(R.id.orderDetails);
        dish_details_recycler_view = itemView.findViewById(R.id.dish_details_recycler_view);
    }
}
