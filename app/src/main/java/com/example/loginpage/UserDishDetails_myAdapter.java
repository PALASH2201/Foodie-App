package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class UserDishDetails_myAdapter extends RecyclerView.Adapter<UserDishDetails_MyViewHolder> {
    private final Context context;
    private final List<DishDataClass> dataList;

    public UserDishDetails_myAdapter(Context context, List<DishDataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public UserDishDetails_MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_dish_recycler_item
                , parent, false);
        return new UserDishDetails_MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDishDetails_MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(dataList.get(position).getDish_image_url()).into(holder.recImage);
        String priceText = "Price:Rs " + dataList.get(position).getDish_price();
        holder.recDishPrice.setText(priceText);
        String descriptionText = "Description: " + dataList.get(position).getDish_description();
        holder.recDishDescription.setText(descriptionText);

        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    @Override
    public int getItemCount () {
        return dataList.size();
    }
}
class UserDishDetails_MyViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage ;
    CardView recCard ;
    TextView recDishPrice;
    TextView recDishDescription;
    Button addToCart ;
    public UserDishDetails_MyViewHolder(@NonNull View itemView){
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recDishPrice = itemView.findViewById(R.id.recDishPrice);
        recDishDescription = itemView.findViewById(R.id.recDishDescription);
        addToCart = itemView.findViewById(R.id.addToCartButton);
    }
}


