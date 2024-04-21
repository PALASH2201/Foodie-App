package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

import soup.neumorphism.NeumorphCardView;

public class Categories_myAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private final Context context ;
    private final List<CategoriesDataClass> dataList;
    private OnItemClickListener mListener;
    private final boolean isVendor ;
    private String restaurant_name;

    public interface OnItemClickListener {
        void onItemClick(int position,boolean isVendor);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public Categories_myAdapter(Context context, List<CategoriesDataClass> dataList , boolean isVendor) {
        this.context = context;
        this.dataList = dataList;
        this.isVendor = isVendor;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_recycler_item
                ,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(dataList.get(position).getImage_url()).into(holder.recImage);
        holder.recName.setText(dataList.get(position).getName());

        findRestaurantName(dataList.get(position).getRestaurant_id());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(position,isVendor);
                }
            }
        });

        if (isVendor) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);

            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Vendor_update_category.class);
                    intent.putExtra("Category Name",dataList.get(position).getName());
                    intent.putExtra("Category Image",dataList.get(position).getImage_url());
                    intent.putExtra("Category Id",dataList.get(position).getKey());
                    intent.putExtra("Restaurant Name",restaurant_name);
                    context.startActivity(intent);
                }
            });

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String categoryId = dataList.get(position).getKey();
                    String imageUrl = dataList.get(position).getImage_url();
                    String restaurantId = dataList.get(position).getRestaurant_id();

                    Log.d("to-be-deleted-id",categoryId);
                    deleteDishFromFirebase(categoryId,imageUrl,restaurantId);
                    dataList.remove(position);
                    notifyDataSetChanged();
                }
            });
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void deleteDishFromFirebase(String categoryId, String imageUrl,String restaurantId) {
        DatabaseReference catRef = FirebaseDatabase.getInstance().getReference("categories").child(categoryId);
        catRef.removeValue();
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
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("restaurants")
                .child(restaurantId)
                .child("categories");

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> categoryIds = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String id = snapshot.getValue(String.class);
                        assert id != null;
                        if (!id.equals(categoryId)) {
                            categoryIds.add(id);
                        }
                    }
                    // Update the dishes array in the category node
                    categoryRef.setValue(categoryIds).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Dish ID removed from category successfully
                            Log.d("DeleteRest", "Category ID removed from restaurants successfully");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to remove dish ID from category
                            Log.e("DeleteRest", "Failed to remove category ID from restaurants: " + e.getMessage());
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


}

class MyViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage ;
    Button editButton , deleteButton;
    TextView recName;
    NeumorphCardView recCard ;

    public MyViewHolder(@NonNull View itemView){
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recName = itemView.findViewById(R.id.recName);
        recCard = itemView.findViewById(R.id.recCard);
        editButton = itemView.findViewById(R.id.editButton);
        deleteButton = itemView.findViewById(R.id.deleteButton);
    }
}
