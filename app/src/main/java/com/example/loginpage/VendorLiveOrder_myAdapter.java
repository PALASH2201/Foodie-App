package com.example.loginpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VendorLiveOrder_myAdapter extends RecyclerView.Adapter<VendorLiveOrder_myViewHolder> {

    private final Context context;
    private final List<LiveOrderDataClass> dataList;
    private final List<LiveOrderDishDataClass> DishdataList;

    public VendorLiveOrder_myAdapter(Context context, List<LiveOrderDataClass> dataList,List<LiveOrderDishDataClass> DishdataList) {
        this.context = context;
        this.dataList = dataList;
        this.DishdataList = DishdataList;
    }


    @NonNull
    @Override
    public VendorLiveOrder_myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_order_recycler_item
                , parent, false);
        return new VendorLiveOrder_myViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull VendorLiveOrder_myViewHolder holder, int position) {
         holder.chosen_time_slot.setText(dataList.get(position).getChosen_time_slot());
         holder.OrderStatus.setText(dataList.get(position).getOrderStatus());
         holder.customerName.setText(dataList.get(position).getCustomerName());
         holder.orderId.setText(dataList.get(position).getOrderId());
         holder.customerBill.setText(dataList.get(position).getCustomerBill());

         VendorLiveOrderDish_myAdapter adapter = new VendorLiveOrderDish_myAdapter(context,DishdataList);
         holder.dish_details_recycler_view.setAdapter(adapter);
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
    ImageView arrowIcon;
    public VendorLiveOrder_myViewHolder(@NonNull View itemView){
        super(itemView);
        chosen_time_slot = itemView.findViewById(R.id.chosen_time_slot);
        OrderStatus = itemView.findViewById(R.id.orderStatus);
        arrowIcon = itemView.findViewById(R.id.arrowIcon);
        customerName = itemView.findViewById(R.id.customerName);
        orderId = itemView.findViewById(R.id.orderId);
        customerBill = itemView.findViewById(R.id.customerBill);
        dish_details_recycler_view = itemView.findViewById(R.id.dish_details_recycler_view);
    }

}
