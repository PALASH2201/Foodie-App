package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Cart_myAdapter extends RecyclerView.Adapter<Cart_MyViewHolder> {
    private final Context context ;
    private final List<CartDataClass> dataList;

    public Cart_myAdapter(Context context, List<CartDataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public Cart_MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_recycler_item
                ,parent,false);
        return new Cart_MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Cart_MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(dataList.get(position).getDish_image_url()).into(holder.item_image);
        holder.dish_name.setText(dataList.get(position).getDish_name());
        holder.dish_price.setText(dataList.get(position).getDish_price());
        holder.dish_quantity.setText(dataList.get(position).getQuantity());



    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
class Cart_MyViewHolder extends RecyclerView.ViewHolder{

    ImageView item_image ;
    TextView dish_name;

    TextView dish_price;
    ImageView plus_btn ;
    ImageView minus_btn;
    TextView dish_quantity;
    LinearLayout cart_item;
    public Cart_MyViewHolder(@NonNull View itemView){
        super(itemView);
        item_image = itemView.findViewById(R.id.item_image);
        dish_name = itemView.findViewById(R.id.dish_name);
        cart_item = itemView.findViewById(R.id.cart_item);
        dish_price = itemView.findViewById(R.id.dish_price);
        plus_btn = itemView.findViewById(R.id.plus_button);
        minus_btn = itemView.findViewById(R.id.minus_button);
        dish_quantity = itemView.findViewById(R.id.quantity_label);
    }
}