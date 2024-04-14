package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class VendorLiveOrder_myAdapter extends RecyclerView.Adapter<VendorLiveOrder_myViewHolder> {

    private final Context context;
    private final List<LiveOrderDataClass> dataList;
    private final SparseBooleanArray expandedItems;
    private final Map<String, List<LiveOrderDishDataClass>> dishMap;

    public VendorLiveOrder_myAdapter(Context context, List<LiveOrderDataClass> dataList,Map<String, List<LiveOrderDishDataClass>> dishMap) {
        this.context = context;
        this.dataList = dataList;
        this.dishMap = dishMap;
        this.expandedItems = new SparseBooleanArray();
    }


    @NonNull
    @Override
    public VendorLiveOrder_myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_order_recycler_item
                , parent, false);
        return new VendorLiveOrder_myViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull VendorLiveOrder_myViewHolder holder, @SuppressLint("RecyclerView") int position) {
         holder.chosen_time_slot.setText(dataList.get(position).getChosen_time_slot());
         holder.OrderStatus.setText(dataList.get(position).getOrderStatus());
         String temp_var = "Customer Name: "+dataList.get(position).getCustomerName();
         holder.customerName.setText(temp_var);
         temp_var = "Order Id: "+dataList.get(position).getOrderId();
         holder.orderId.setText(temp_var);
         temp_var = "Total Bill: "+dataList.get(position).getCustomerBill();
         holder.customerBill.setText(temp_var);

        List<LiveOrderDishDataClass> dishList = dishMap.get(dataList.get(position).getOrderId());

        holder.dish_details_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
         VendorLiveOrderDish_myAdapter adapter = new VendorLiveOrderDish_myAdapter(context,dishList);
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
    public int getItemCount () {
        return dataList.size();
    }
}
class VendorLiveOrder_myViewHolder extends RecyclerView.ViewHolder{
    TextView chosen_time_slot,OrderStatus;
    TextView customerName,orderId,customerBill;
    RecyclerView dish_details_recycler_view;
    LinearLayout orderDetails;
    ImageView arrowIcon;
    public VendorLiveOrder_myViewHolder(@NonNull View itemView){
        super(itemView);
        chosen_time_slot = itemView.findViewById(R.id.chosen_time_slot);
        OrderStatus = itemView.findViewById(R.id.orderStatus);
        arrowIcon = itemView.findViewById(R.id.arrowIcon);
        orderDetails = itemView.findViewById(R.id.orderDetails);
        customerName = itemView.findViewById(R.id.customerName);
        orderId = itemView.findViewById(R.id.orderId);
        customerBill = itemView.findViewById(R.id.customerBill);
        dish_details_recycler_view = itemView.findViewById(R.id.dish_details_recycler_view);
    }

}
