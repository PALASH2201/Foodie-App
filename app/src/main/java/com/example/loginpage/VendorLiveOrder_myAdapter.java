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

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VendorLiveOrder_myAdapter extends RecyclerView.Adapter<VendorLiveOrder_myViewHolder> {

    private final Context context;
    private final List<LiveOrderDataClass> dataList;
    private final SparseBooleanArray expandedItems;
    private final Map<String, List<LiveOrderDishDataClass>> dishMap;
    private final String restaurant_id;

    public VendorLiveOrder_myAdapter(Context context, List<LiveOrderDataClass> dataList,Map<String, List<LiveOrderDishDataClass>> dishMap,String restaurant_id) {
        this.context = context;
        this.dataList = dataList;
        this.dishMap = dishMap;
        this.expandedItems = new SparseBooleanArray();
        this.restaurant_id = restaurant_id;
    }


    @NonNull
    @Override
    public VendorLiveOrder_myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_order_recycler_item
                , parent, false);
        return new VendorLiveOrder_myViewHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VendorLiveOrder_myViewHolder holder, @SuppressLint("RecyclerView") int position) {
        boolean flag = false;
        String slot_timing = dataList.get(position).getChosen_time_slot();
        holder.chosen_time_slot.setText(slot_timing);
        holder.OrderStatus.setText(dataList.get(position).getOrderStatus());
        String temp_var = "Customer Name: "+dataList.get(position).getCustomerName();
        holder.customerName.setText(temp_var);
        temp_var = "Contact Number: "+dataList.get(position).getCustomerContact();
        holder.customerContact.setText(temp_var);
        temp_var = "Order Id: "+dataList.get(position).getOrderId();
        holder.orderId.setText(temp_var);
        temp_var = "Total Bill: "+dataList.get(position).getCustomerBill();
        holder.customerBill.setText(temp_var);

        List<LiveOrderDishDataClass> dishList = dishMap.get(dataList.get(position).getOrderId());

        holder.dish_details_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        VendorLiveOrderDish_myAdapter adapter = new VendorLiveOrderDish_myAdapter(context,dishList);
        holder.dish_details_recycler_view.setAdapter(adapter);

        long currentTime = System.currentTimeMillis();
        long startTimeMillis = getTimeInMillis(slot_timing);
        if(startTimeMillis == -1){
            Toast.makeText(context,"Wrong timer. Refresh Again!" , Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("Start time",startTimeMillis+"");
        Log.d("Current time",currentTime+"");

        long remainingTime = startTimeMillis - currentTime;
        Log.d("Remaining time:",remainingTime+"");

        if (remainingTime < 0) {
            holder.timer.setText("Time slot has started");
        }
        else {
            new CountDownTimer(remainingTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String temp_var = "Remaining Time: "+getRemainingTimeFormatted(millisUntilFinished);
                    holder.timer.setText(temp_var);
                }
                @Override
                public void onFinish() {
                    holder.timer.setText("Time slot starts now");
                }
            }.start();
        }


        boolean isExpanded = expandedItems.get(position, false);
        holder.orderDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.arrowIcon.setRotation(isExpanded ? 180 : 0);

        holder.arrowIcon.setOnClickListener(view -> {
            boolean isExpanded1 = expandedItems.get(position, false);
            expandedItems.put(position, !isExpanded1);
            holder.arrowIcon.setRotation(isExpanded1 ? 0 : 180);
            holder.orderDetails.setVisibility(isExpanded1 ? View.GONE : View.VISIBLE);
        });

        holder.updateStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderId = dataList.get(position).getOrderId();
                DatabaseReference order = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurant_id).child("Live Orders").child(orderId).child("orderStatus");
                order.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            order.setValue("Ready for Pickup");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                       Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        if(holder.OrderStatus.getText().toString().equals("Ready for Pickup")){
            holder.OrderStatus.setBackgroundColor(ContextCompat.getColor(context,R.color.green));
        }else{
            holder.OrderStatus.setBackgroundColor(ContextCompat.getColor(context,R.color.red));
        }


    }

    @Override
    public int getItemCount () {
        return dataList.size();
    }


    private long getTimeInMillis(String timeSlotString) {
        long startTimeMillis = -1;
        try {
            String[] timeParts = timeSlotString.split(" ");
            String startTime = timeParts[0];

            String[] startParts = startTime.split(":");
            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);
            String startMeridian = timeParts[3].toLowerCase();

            if (startMeridian.equals("pm") && startHour != 12) {
                startHour += 12;
            }

            Log.d("Start Hour",startHour+"");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, startHour);
            calendar.set(Calendar.MINUTE, startMinute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            startTimeMillis =  calendar.getTimeInMillis();
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            Log.d("Exception caught", Objects.requireNonNull(e.getMessage()));
        }
        return startTimeMillis;
    }
    @SuppressLint("DefaultLocale")
    private String getRemainingTimeFormatted(long millisUntilFinished) {
        long seconds = (millisUntilFinished / 1000) % 60;
        long minutes = (millisUntilFinished / (1000 * 60)) % 60;
        long hours = (millisUntilFinished / (1000 * 60 * 60)) % 24;
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }

}
class VendorLiveOrder_myViewHolder extends RecyclerView.ViewHolder{
    TextView chosen_time_slot,OrderStatus,timer;
    TextView customerName,orderId,customerBill,customerContact;
    RecyclerView dish_details_recycler_view;
    LinearLayout orderDetails;
    ImageView arrowIcon;
    Button updateStatusButton;
    public VendorLiveOrder_myViewHolder(@NonNull View itemView){
        super(itemView);
        customerContact = itemView.findViewById(R.id.contact_number);
        updateStatusButton = itemView.findViewById(R.id.updateStatusButton);
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
