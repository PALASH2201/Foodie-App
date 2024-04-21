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
import soup.neumorphism.NeumorphCardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Mess_myAdapter extends RecyclerView.Adapter<Mess_MyViewHolder> {


    private final Context context ;
    private final List<VendorDataClass> dataList;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public Mess_myAdapter(Context context, List<VendorDataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public Mess_MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.mess_choice_recycler_item
                ,parent,false);
        return new Mess_MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Mess_MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(context).load(dataList.get(position).getProfile_pic_image_url()).into(holder.recImage);
        holder.recName.setText(dataList.get(position).getRestaurant_name());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class Mess_MyViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage ;
    TextView recName;
    public Mess_MyViewHolder(@NonNull View itemView){
        super(itemView);
        recImage = itemView.findViewById(R.id.recImage);
        recName = itemView.findViewById(R.id.recName);
    }
}
