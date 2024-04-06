package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class VendorDishDetails_myAdapter extends RecyclerView.Adapter<VendorDishDetails_MyViewHolder> {

    private final Context context ;
    private final List<DishDataClass> dataList;
    private final SparseBooleanArray expandedItems;

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
        holder.arrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isExpanded = expandedItems.get(position, false);
                expandedItems.put(position, !isExpanded);
                holder.arrowIcon.setRotation(isExpanded ? 0 : 180);
                holder.additionalDetailsLayout.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            }
        });
//        holder.editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });
//        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatabaseReference dishRef = FirebaseDatabase.getInstance().getReference("dishes");
//                FirebaseStorage storage =FirebaseStorage.getInstance();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
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

