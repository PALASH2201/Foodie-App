package com.example.loginpage;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VendorLiveOrder_myAdapter extends RecyclerView.Adapter<VendorLiveOrder_myViewHolder> {

    private final Context context;
    private final List<LiveOrderDataClass> dataList;
    private final SparseBooleanArray expandedItems;
    private final Map<String, List<LiveOrderDishDataClass>> dishMap;
    private long startTimeMillis;

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
         String slot_timing = dataList.get(position).getChosen_time_slot();
         holder.chosen_time_slot.setText(slot_timing);
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

        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        try {
            Date startTime = sdf.parse(slot_timing.split(" - ")[0]);
            assert startTime != null;
            startTimeMillis = startTime.getTime();
        } catch (ParseException e) {
            Log.d("Exception caught","Parse Exception");
        }

        long currentTimeMillis = System.currentTimeMillis();
        long timeDiffMillis = startTimeMillis - currentTimeMillis;

        CountDownTimer countDownTimer = new CountDownTimer(timeDiffMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsUntilFinished = millisUntilFinished / 1000;
                long minutes = secondsUntilFinished / 60;
                long seconds = secondsUntilFinished % 60;
                holder.timer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                holder.timer.setText("00:00");
            }
        };

        countDownTimer.start();


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
    TextView chosen_time_slot,OrderStatus,timer;
    TextView customerName,orderId,customerBill;
    RecyclerView dish_details_recycler_view;
    LinearLayout orderDetails;
    ImageView arrowIcon;
    public VendorLiveOrder_myViewHolder(@NonNull View itemView){
        super(itemView);
        timer = itemView.findViewById(R.id.timer);
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
