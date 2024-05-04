package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.Date;
import java.util.List;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

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
        temp_var = "Mess/Canteen Name: "+orderHistoryList.get(position).getRest_name();
        holder.chosen_restaurant.setText(temp_var);
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

        holder.updateStatusButton.setOnClickListener(v -> updateStatus(orderHistoryList.get(position).getRest_id(),orderHistoryList.get(position).getOrderId(),holder));
        holder.cancelOrderButton.setOnClickListener(v -> {
            if(canCancelOrder(orderHistoryList.get(position).getTimeSlot()) && holder.orderStatus.getText().toString().equals("Pending")){
                cancelOrder(orderHistoryList.get(position).getRest_id(),orderHistoryList.get(position).getOrderId(),position,holder);
            }else{
                StyleableToast.makeText(context,"Cannot cancel order at this time!",Toast.LENGTH_SHORT,R.style.warningToast).show();
            }
        });
    }

    public boolean canCancelOrder(String timeSlot) {
        boolean isPM = false;
        Date currentTime = new Date();
        String[] timeParts = timeSlot.split(" ");
        String startTimeString = timeParts[0];
        if(timeParts[3].equals("pm")){
            isPM = true;
        }
        String startHour[] = startTimeString.split(":");
        int hour = Integer.parseInt(startHour[0]);
        int mSec = hour * 60 * 60 * 1000;
        long cancellationTimeMillis =  mSec - (30 * 60 * 1000) + currentTime.getTime();
        if (isPM) {
            cancellationTimeMillis += 12 * 60 * 60 * 1000;
        }
        Log.d("Cancellation Time",cancellationTimeMillis+"");
        Log.d("Current Time",currentTime.getTime()+"");
        return currentTime.getTime() < cancellationTimeMillis;
    }

    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }

    public void getOrderStatus(String restaurant_id , String orderId,@NonNull UserOrderHistory_myViewHolder holder){
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurant_id).child("Live Orders").child(orderId);
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    order_status = snapshot.child("orderStatus").getValue(String.class);
                    holder.orderStatus.setText(order_status);
                    if(order_status.equals("Ready for Pickup")){
                        holder.cancelOrderButton.setVisibility(View.GONE);
                        holder.orderStatus.setBackgroundColor(ContextCompat.getColor(context,R.color.green));
                    }
                    else if(order_status.equals("Pending")){
                        holder.orderStatus.setBackgroundColor(ContextCompat.getColor(context,R.color.red));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(context,"Database error! Try Again",Toast.LENGTH_LONG,R.style.failureToast).show();
            }
        });
    }

    public void cancelOrder(String restaurant_id , String orderId,int position,@NonNull UserOrderHistory_myViewHolder holder){
        String new_status = "Cancelled";
        holder.orderStatus.setText(new_status);
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurant_id).child("Live Orders").child(orderId);
        orderRef.removeValue().addOnCompleteListener(task -> StyleableToast.makeText(context,"Your order is successfully cancelled. Please wait for your refund",Toast.LENGTH_SHORT,R.style.successToast).show()).addOnFailureListener(e -> StyleableToast.makeText(context,"Could not cancel you order!",Toast.LENGTH_SHORT,R.style.failureToast).show());
         DatabaseReference pendingOrder = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurant_id).child("Pending Orders");
         pendingOrder.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 String num = snapshot.getValue(String.class);
                 assert num != null;
                 int new_num = Integer.parseInt(num) - 1;
                 pendingOrder.setValue(String.valueOf(new_num));
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
         DatabaseReference billRef = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurant_id).child("Money Earned");
         billRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if(snapshot.exists()){
                     String old_revenue = snapshot.getValue(String.class);
                     assert old_revenue != null;
                     int new_revenue = Integer.parseInt(old_revenue) - Integer.parseInt(orderHistoryList.get(position).getTotalBill());
                     billRef.setValue(String.valueOf(new_revenue));
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
    }
    public void updateStatus(String restaurant_id , String orderId,@NonNull UserOrderHistory_myViewHolder holder){
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurant_id).child("Live Orders").child(orderId);
        if(holder.orderStatus.getText().toString().equals("Ready for Pickup")){
            order_status = "Takeaway successful";
            orderRef.child("orderStatus").setValue(order_status);
            holder.updateStatusButton.setVisibility(View.GONE);
            holder.orderStatus.setText(order_status);

            DatabaseReference restRef = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurant_id);
            DatabaseReference pendingOrderRef = restRef.child("Pending Orders");
            DatabaseReference completedOrderRef = restRef.child("Completed Orders");

            pendingOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String curValue = snapshot.getValue(String.class);
                    assert curValue != null;
                    String newValue = String.valueOf(Integer.parseInt(curValue) - 1);
                    pendingOrderRef.setValue(newValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            completedOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String curValue = snapshot.getValue(String.class);
                        assert curValue != null;
                        String newValue = String.valueOf(Integer.parseInt(curValue) + 1);
                        completedOrderRef.setValue(newValue);
                    }else{
                        completedOrderRef.setValue(String.valueOf(1));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if(holder.orderStatus.getText().toString().equals("Pending")){
            StyleableToast.makeText(context,"Order is still being prepared!",Toast.LENGTH_LONG,R.style.warningToast).show();
        }
    }
}

class UserOrderHistory_myViewHolder extends  RecyclerView.ViewHolder{
    TextView orderTime , orderDay ,chosen_time_slot,orderId,customerBill,orderStatus,chosen_restaurant;
    Button updateStatusButton,cancelOrderButton;
    LinearLayout orderDetails;
    RecyclerView dish_details_recycler_view;
    public UserOrderHistory_myViewHolder(@NonNull View itemView) {
        super(itemView);
        cancelOrderButton = itemView.findViewById(R.id.cancelOrderButton);
        chosen_restaurant = itemView.findViewById(R.id.chosen_restaurant);
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
