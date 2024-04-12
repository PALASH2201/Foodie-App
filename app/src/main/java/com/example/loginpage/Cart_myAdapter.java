package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    private double subTotal , total_bill ;
    private final  double platform_fees = 8;
    TextView subtotal , totalBill;

    public Cart_myAdapter(Context context, List<CartDataClass> dataList,TextView subtotal , TextView totalBill) {
        this.context = context;
        this.dataList = dataList;
        this.subtotal = subtotal;
        this.totalBill = totalBill;
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
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String dishId = dataList.get(position).getKey();
        final double single_price = Double.parseDouble(dataList.get(position).getSingleDish_price());
        final double[] total_price = {Double.parseDouble(dataList.get(position).getTotalDish_price())};
        Glide.with(context).load(dataList.get(position).getDish_image_url()).into(holder.item_image);
        holder.dish_name.setText(dataList.get(position).getDish_name());
        String price = "Price: "+ total_price[0];
        holder.dish_price.setText(price);
        holder.dish_quantity.setText(String.valueOf(dataList.get(position).getQuantity()));

        getSubTotal(dataList);
        holder.plus_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(holder.dish_quantity.getText().toString());
                int newQuantity = currentQuantity + 1;
                holder.dish_quantity.setText(String.valueOf(newQuantity));
                 total_price[0] = newQuantity * single_price;
                 String price = "Price: "+ total_price[0];
                 holder.dish_price.setText(price);
                CartDataClass new_dataList = new CartDataClass(
                        dataList.get(position).getDish_name()
                        ,String.valueOf(single_price)
                        ,String.valueOf(total_price[0])
                        ,dataList.get(position).getDish_image_url()
                        ,dataList.get(position).getRestaurant_id()
                        ,dataList.get(position).getCategory_id()
                        ,dishId
                        ,newQuantity
                        ,dataList.get(position).getRestaurant_name()
                        ,dataList.get(position).getCategory_name());
                dataList.set(position,new_dataList);
                 updatePrice(dishId,userId,String.valueOf(total_price[0]),String.valueOf(newQuantity));
                 subTotal += (newQuantity - currentQuantity) * single_price;
                 total_bill = subTotal + platform_fees;
                 subtotal.setText("Rs: "+subTotal);
                 totalBill.setText("Rs: "+total_bill);
            }
        });
        holder.minus_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(holder.dish_quantity.getText().toString());
                if (currentQuantity > 1) {
                    int newQuantity = currentQuantity - 1;
                    holder.dish_quantity.setText(String.valueOf(newQuantity));
                    total_price[0] = newQuantity * single_price;
                    String price = "Price: "+ total_price[0];
                    holder.dish_price.setText(price);
                    CartDataClass new_dataList = new CartDataClass(
                            dataList.get(position).getDish_name()
                            ,String.valueOf(single_price)
                            ,String.valueOf(total_price[0])
                            ,dataList.get(position).getDish_image_url()
                            ,dataList.get(position).getRestaurant_id()
                             ,dataList.get(position).getCategory_id()
                             ,dishId
                             ,newQuantity
                             ,dataList.get(position).getRestaurant_name()
                             ,dataList.get(position).getCategory_name());
                    dataList.set(position,new_dataList);
                    updatePrice(dishId,userId,String.valueOf(total_price[0]),String.valueOf(newQuantity));
                    subTotal -= (currentQuantity - newQuantity) * single_price;
                    total_bill = subTotal + platform_fees;
                    subtotal.setText("Rs: "+subTotal);
                    totalBill.setText("Rs: "+total_bill);
                } else {
                    Toast.makeText(context, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.remove_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String dishId = dataList.get(position).getKey();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String total_price_dish = dataList.get(position).getTotalDish_price();
                subTotal-= Double.parseDouble(total_price_dish) ;
                total_bill = subTotal + platform_fees;
                subtotal.setText("Rs: "+subTotal);
                totalBill.setText("Rs: "+total_bill);
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
    public void updatePrice(String dishId , String userId , String totalPrice , String new_quantity){
          DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart");
          DatabaseReference dishRef = cartRef.child(dishId);
          dishRef.child("quantity").setValue(new_quantity);
          dishRef.child("total_price").setValue(totalPrice);
          Toast.makeText(context,"Price updated",Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("SetTextI18n")
    public void getSubTotal(List<CartDataClass> dataList){
        subTotal = 0;
        for(int i =0 ;i < dataList.size();i++){
            subTotal += Double.parseDouble(dataList.get(i).getTotalDish_price());
        }
        total_bill = subTotal + platform_fees;
        subtotal.setText("Rs: "+subTotal);
        totalBill.setText("Rs: "+total_bill);
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