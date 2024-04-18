package com.example.loginpage;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class UserOrderHistory_myAdapter extends RecyclerView.Adapter<UserOrderHistory_myViewHolder> {
    private final List<UserOrderHistoryDataClass> orderHistoryList;

    private final Context context;
    String temp_var;
    private final SparseBooleanArray expandedItems;
    private final Map<String, List<UserOrderHistoryDishDataClass>> dishMap;

    public UserOrderHistory_myAdapter(List<UserOrderHistoryDataClass> orderHistoryList, Context context,Map<String, List<UserOrderHistoryDishDataClass>> dishMap) {
        this.orderHistoryList = orderHistoryList;
        this.context = context;
        expandedItems = new SparseBooleanArray();
        this.dishMap = dishMap;
    }

    @NonNull
    @Override
    public UserOrderHistory_myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_history_recycler_item, parent, false);
        return new UserOrderHistory_myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserOrderHistory_myViewHolder holder, int position) {
        holder.orderDay.setText(orderHistoryList.get(position).getDay());
        holder.orderTime.setText(orderHistoryList.get(position).getOrderTime());
        temp_var = "Order Id: "+orderHistoryList.get(position).getOrderId();
        holder.orderId.setText(temp_var);
        String slot_timing = orderHistoryList.get(position).getTimeSlot();
        holder.chosen_time_slot.setText(slot_timing);
        temp_var = "Total Bill: "+orderHistoryList.get(position).getTotalBill();
        holder.customerBill.setText(temp_var);

        List<UserOrderHistoryDishDataClass> dishList = dishMap.get(orderHistoryList.get(position).getOrderId());

        holder.dish_details_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        UserOrderHistoryDishes_myAdapter adapter = new UserOrderHistoryDishes_myAdapter(dishList,context);
        holder.dish_details_recycler_view.setAdapter(adapter);

        boolean isExpanded = expandedItems.get(position, false);
        holder.orderDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.arrowIcon.setRotation(isExpanded ? 180 : 0);

        holder.arrowIcon.setOnClickListener(view -> {
            boolean isExpanded1 = expandedItems.get(position, false);
            expandedItems.put(position, !isExpanded1);
            holder.arrowIcon.setRotation(isExpanded1 ? 0 : 180);
            holder.orderDetails.setVisibility(isExpanded1 ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }
}

class UserOrderHistory_myViewHolder extends  RecyclerView.ViewHolder{
    TextView orderTime , orderDay ,chosen_time_slot,orderId,customerBill;
    LinearLayout orderDetails;
    RecyclerView dish_details_recycler_view;
    ImageView arrowIcon;
    public UserOrderHistory_myViewHolder(@NonNull View itemView) {
        super(itemView);
        arrowIcon = itemView.findViewById(R.id.arrowIcon);
        orderDay = itemView.findViewById(R.id.orderDay);
        orderTime = itemView.findViewById(R.id.orderTime);
        chosen_time_slot = itemView.findViewById(R.id.chosen_time_slot);
        orderId = itemView.findViewById(R.id.orderId);
        customerBill = itemView.findViewById(R.id.customerBill);
        orderDetails = itemView.findViewById(R.id.orderDetails);
        dish_details_recycler_view = itemView.findViewById(R.id.dish_details_recycler_view);
    }
}
