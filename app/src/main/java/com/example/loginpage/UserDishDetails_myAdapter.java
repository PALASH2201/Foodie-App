package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class UserDishDetails_myAdapter extends RecyclerView.Adapter<UserDishDetails_MyViewHolder> {
    private final Context context;
    private final List<DishDataClass> dataList;
    private String restaurant_name , category_name;

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
        holder.recDishName.setText(dataList.get(position).getDish_name());
        String priceText = "Price:Rs " + dataList.get(position).getDish_price();
        holder.recDishPrice.setText(priceText);
        String descriptionText = "Description: " + dataList.get(position).getDish_description();
        holder.recDishDescription.setText(descriptionText);

        findRestaurantName(dataList.get(position).getRestaurant_id());
        findCategoryName(dataList.get(position).getCategory_id());




        holder.plus_btn.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.recQuantity.getText().toString());
            int newQuantity = currentQuantity + 1;
            holder.recQuantity.setText(String.valueOf(newQuantity));
        });
        holder.minus_btn.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.recQuantity.getText().toString());
            if (currentQuantity > 1) {
                int newQuantity = currentQuantity - 1;
                holder.recQuantity.setText(String.valueOf(newQuantity));
            } else {
                Toast.makeText(context, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
            }
        });


        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                  String dishId = dataList.get(position).getKey();
                DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart").child(dishId);
                userCartRef.child("dish_name").setValue(dataList.get(position).getDish_name());
                userCartRef.child("dish_price").setValue(dataList.get(position).getDish_price());
                userCartRef.child("dish_image_url").setValue(dataList.get(position).getDish_image_url());
                userCartRef.child("quantity").setValue(holder.recQuantity.getText().toString());
                userCartRef.child("category_name").setValue(category_name);
                userCartRef.child("restaurant_name").setValue(restaurant_name);
                userCartRef.child("category_id").setValue(dataList.get(position).getCategory_id());
                userCartRef.child("restaurant_id").setValue(dataList.get(position).getRestaurant_id());
                double total_price = Double.parseDouble(dataList.get(position).getDish_price()) * Double.parseDouble(holder.recQuantity.getText().toString());
                userCartRef.child("total_price").setValue(String.valueOf(total_price));

               Toast.makeText(context,"Dish Successfully added in cart",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getItemCount () {
        return dataList.size();
    }

    private void findRestaurantName(String restaurantId){
        DatabaseReference restRef = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurantId);
        restRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    restaurant_name = snapshot.child("restaurant_name").getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,"Database error in retrieving restaurant name",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void findCategoryName(String categoryId){
        DatabaseReference restRef = FirebaseDatabase.getInstance().getReference("categories").child(categoryId);
        restRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    category_name = snapshot.child("name").getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,"Database error in retrieving category name",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
class UserDishDetails_MyViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage ;
    CardView recCard ;
    TextView recDishName;
    TextView recDishPrice;
    TextView recDishDescription;
    TextView recQuantity;
    ImageView plus_btn , minus_btn;
    Button addToCart ;
    public UserDishDetails_MyViewHolder(@NonNull View itemView){
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recDishName = itemView.findViewById(R.id.recDishName);
        recDishPrice = itemView.findViewById(R.id.recDishPrice);
        recDishDescription = itemView.findViewById(R.id.recDishDescription);
        recQuantity = itemView.findViewById(R.id.recQuantity);
        plus_btn = itemView.findViewById(R.id.imageButtonPlus);
        minus_btn = itemView.findViewById(R.id.imageButtonMinus);
        addToCart = itemView.findViewById(R.id.addToCartButton);
    }
}


