package com.example.loginpage;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseBooleanArray;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class VendorDishDetails_myAdapter extends RecyclerView.Adapter<VendorDishDetails_MyViewHolder> {

    private final Context context ;
    private final List<DishDataClass> dataList;
    private final SparseBooleanArray expandedItems;
    private String restaurant_name , category_name;

    public VendorDishDetails_myAdapter(Context context, List<DishDataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.expandedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public VendorDishDetails_MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_dish_recycler_item
                ,parent,false);
        return new VendorDishDetails_MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorDishDetails_MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(dataList.get(position).getDish_image_url()).into(holder.recImage);
        holder.recName.setText(dataList.get(position).getDish_name());
        String priceText = "Price:Rs " + dataList.get(position).getDish_price();
        holder.recDishPrice.setText(priceText);
        String descriptionText = "Description: " + dataList.get(position).getDish_description();
        holder.recDishDescription.setText(descriptionText);

        boolean isExpanded = expandedItems.get(position, false);
        holder.additionalDetailsLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.arrowIcon.setRotation(isExpanded ? 180 : 0);

        findRestaurantName(dataList.get(position).getRestaurant_id());
        findCategoryName(dataList.get(position).getCategory_id());
        holder.arrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isExpanded = expandedItems.get(position, false);
                expandedItems.put(position, !isExpanded);
                holder.arrowIcon.setRotation(isExpanded ? 0 : 180);
                holder.additionalDetailsLayout.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            }
        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Vendor_update_dish.class);
                intent.putExtra("Dish Name",dataList.get(position).getDish_name());
                intent.putExtra("Dish Description",dataList.get(position).getDish_description());
                intent.putExtra("Dish Price",dataList.get(position).getDish_price());
                intent.putExtra("Dish Image",dataList.get(position).getDish_image_url());
                intent.putExtra("Dish Id",dataList.get(position).getKey());
                intent.putExtra("Category Name",category_name);
                intent.putExtra("Restaurant Name",restaurant_name);
                context.startActivity(intent);
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dishId = dataList.get(position).getKey();
                String imageUrl = dataList.get(position).getDish_image_url();
                String categoryId = dataList.get(position).getCategory_id();
                Log.d("to-be-deleted-id",dishId);
                deleteDishFromFirebase(dishId,imageUrl,categoryId);
                dataList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void deleteDishFromFirebase(String dishId, String imageUrl,String categoryId) {
        DatabaseReference dishesRef = FirebaseDatabase.getInstance().getReference("dishes").child(dishId);
        dishesRef.removeValue();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                    Log.d("DeleteImage", "Image deleted successfully");
                }
            }).addOnFailureListener(e -> {

                Toast.makeText(context, "Error deleting. Try Again", Toast.LENGTH_SHORT).show();
                Log.e("DeleteImage", "Failed to delete image: " + e.getMessage());
            });
        }
            DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("categories")
                    .child(categoryId)
                    .child("dishes");

            categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        List<String> dishIds = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String id = snapshot.getValue(String.class);
                            assert id != null;
                            if (!id.equals(dishId)) {
                                dishIds.add(id);
                            }
                        }
                        // Update the dishes array in the category node
                        categoryRef.setValue(dishIds).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Dish ID removed from category successfully
                                Log.d("DeleteDish", "Dish ID removed from category successfully");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to remove dish ID from category
                                Log.e("DeleteDish", "Failed to remove dish ID from category: " + e.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                     Toast.makeText(context,"Database error",Toast.LENGTH_SHORT).show();
                }
            });
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
class VendorDishDetails_MyViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage ;
    TextView recName;
    CardView recCard ;
    ImageView arrowIcon;
    ConstraintLayout additionalDetailsLayout;
    TextView recDishPrice;
    TextView recDishDescription;
    Button editButton;
    Button deleteButton;
    public VendorDishDetails_MyViewHolder(@NonNull View itemView){
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recName = itemView.findViewById(R.id.recName);
        recCard = itemView.findViewById(R.id.recCard);
        arrowIcon = itemView.findViewById(R.id.arrowIcon);

        additionalDetailsLayout = itemView.findViewById(R.id.additionalDetailsLayout);
        recDishPrice = itemView.findViewById(R.id.dishPrice);
        recDishDescription = itemView.findViewById(R.id.dishDescription);
        editButton = itemView.findViewById(R.id.editButton);
        deleteButton = itemView.findViewById(R.id.deleteButton);
    }
}

