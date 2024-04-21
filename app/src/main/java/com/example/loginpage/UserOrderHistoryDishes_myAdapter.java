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

public class UserOrderHistoryDishes_myAdapter extends RecyclerView.Adapter<UserOrderHistoryDishes_myViewHolder> {


    private final List<UserOrderHistoryDishDataClass> orderHistoryDishList;
    private final Context context;

    public UserOrderHistoryDishes_myAdapter(List<UserOrderHistoryDishDataClass> orderHistoryDishList, Context context) {
        this.orderHistoryDishList = orderHistoryDishList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserOrderHistoryDishes_myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_history_dish_recycler_item, parent, false);
        return new UserOrderHistoryDishes_myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserOrderHistoryDishes_myViewHolder holder, int position) {
        Log.d("N",orderHistoryDishList.get(position).getDishName());
        holder.dish_name.setText(orderHistoryDishList.get(position).getDishName());
        String temp_var = orderHistoryDishList.get(position).getDishQuantity()+" X ";
        Log.d("Q:",temp_var);
        holder.dish_quantity.setText(temp_var);
        Log.d("P",orderHistoryDishList.get(position).getDishPrice());
        holder.dish_price.setText(orderHistoryDishList.get(position).getDishPrice());
    }

    @Override
    public int getItemCount() {
       return orderHistoryDishList.size();
    }
}

class UserOrderHistoryDishes_myViewHolder extends  RecyclerView.ViewHolder{
     TextView dish_name , dish_quantity , dish_price ;
    public UserOrderHistoryDishes_myViewHolder(@NonNull View itemView) {
        super(itemView);
        dish_name = itemView.findViewById(R.id.dish_name);
        dish_price = itemView.findViewById(R.id.dish_price);
        dish_quantity = itemView.findViewById(R.id.dish_quantity);
    }
}
