package com.example.loginpage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VendorLiveOrderDish_myAdapter extends RecyclerView.Adapter<VendorLiveOrderDish_myViewHolder> {

    private final Context context ;
    private final List<LiveOrderDishDataClass> dataList;

    public VendorLiveOrderDish_myAdapter(Context context, List<LiveOrderDishDataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public VendorLiveOrderDish_myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_order_dish_recycler_item
                , parent, false);
        return new VendorLiveOrderDish_myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorLiveOrderDish_myViewHolder holder, int position) {
           String temp_var = dataList.get(position).getDishQ()+" X ";
        Log.d("dishQ:",temp_var);
            holder.dish_quantity.setText(temp_var);
        Log.d("dishName:",dataList.get(position).getDishName());
            holder.dish_name.setText(dataList.get(position).getDishName());
        Log.d("dishPrice:",dataList.get(position).getTotalPrice());
            holder.dish_price.setText(dataList.get(position).getTotalPrice());
    }
    @Override
    public int getItemCount () {
        return dataList.size();
    }
}
 class VendorLiveOrderDish_myViewHolder extends RecyclerView.ViewHolder{
    TextView dish_name , dish_quantity , dish_price;
     public VendorLiveOrderDish_myViewHolder(@NonNull View itemView) {
         super(itemView);
         dish_name = itemView.findViewById(R.id.dish_name);
         dish_quantity=itemView.findViewById(R.id.dish_quantity);
         dish_price = itemView.findViewById(R.id.dish_price);
     }
 }
