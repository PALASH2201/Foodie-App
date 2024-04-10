package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        final double single_price = Double.parseDouble(dataList.get(position).getSingleDish_price());
        final double[] total_price = {Double.parseDouble(dataList.get(position).getTotalDish_price())};
        Glide.with(context).load(dataList.get(position).getDish_image_url()).into(holder.item_image);
        holder.dish_name.setText(dataList.get(position).getDish_name());
        String price = "Price: "+ total_price[0];
        holder.dish_price.setText(price);
        holder.dish_quantity.setText(String.valueOf(dataList.get(position).getQuantity()));

        holder.plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(holder.dish_quantity.getText().toString());
                int newQuantity = currentQuantity + 1;
                holder.dish_quantity.setText(String.valueOf(newQuantity));
                 total_price[0] = newQuantity * single_price;
                 String price = "Price: "+ total_price[0];
                 holder.dish_price.setText(price);
            }
        });
        holder.minus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(holder.dish_quantity.getText().toString());
                if (currentQuantity > 1) {
                    int newQuantity = currentQuantity - 1;
                    holder.dish_quantity.setText(String.valueOf(newQuantity));
                    total_price[0] = newQuantity * single_price;
                    String price = "Price: "+ total_price[0];
                    holder.dish_price.setText(price);
                } else {
                    Toast.makeText(context, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dishId = dataList.get(position).getKey();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                deleteFromCart(dishId,userId);
                dataList.remove(position);
                notifyDataSetChanged();
            }
        });
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void deleteFromCart(String dishId,String userId){
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart");
        DatabaseReference dishRef = cartRef.child(dishId);
        dishRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context,"Item successfully removed from cart",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Error in removing item from the cart",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
class Cart_MyViewHolder extends RecyclerView.ViewHolder{

    ImageView item_image ;
    TextView dish_name;

    TextView dish_price;
    ImageView plus_btn ;
    ImageView minus_btn;
    ImageView remove_btn;
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
        remove_btn = itemView.findViewById(R.id.removeDish);
        dish_quantity = itemView.findViewById(R.id.quantity_text);
    }
}