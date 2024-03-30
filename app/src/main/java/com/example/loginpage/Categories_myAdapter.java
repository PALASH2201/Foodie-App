package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Categories_myAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private final Context context ;
    private final List<CategoriesDataClass> dataList;
    private OnItemClickListener mListener;
    private final boolean isVendor ;

    public interface OnItemClickListener {
        void onItemClick(int position,boolean isVendor);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(position,isVendor);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage ;
    TextView recName;
    CardView recCard ;
    public MyViewHolder(@NonNull View itemView){
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recName = itemView.findViewById(R.id.recName);
        recCard = itemView.findViewById(R.id.recCard);
    }
}
