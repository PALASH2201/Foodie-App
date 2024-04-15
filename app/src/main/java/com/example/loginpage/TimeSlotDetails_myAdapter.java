package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeSlotDetails_myAdapter extends RecyclerView.Adapter<TimeSlot_MyViewHolder> {
    private final Context context;
    private final List<TimeSlotDataClass> dataList;
    private TimeSlotDetails_myAdapter.OnItemClickListener mListener;
    int lastCheckedPosition;
    private final TextView timeSlotCheckoutBtn;
    String selected_timeSlot, day , available_slots,restaurant_id , restaurant_name,total_bill;

    private final boolean isVendor;
    public interface OnItemClickListener {
        void onItemClick(int position,boolean isVendor);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(TimeSlotDetails_myAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public TimeSlotDetails_myAdapter(Context context, List<TimeSlotDataClass> dataList,boolean isVendor,TextView timeSlotCheckoutBtn,String total_bill) {
        this.context = context;
        this.dataList = dataList;
        this.isVendor = isVendor;
        this.timeSlotCheckoutBtn=timeSlotCheckoutBtn;
        this.total_bill = total_bill;
    }
    @NonNull
    @Override
    public TimeSlot_MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_slot_recycler_item
                ,parent,false);
        return new TimeSlot_MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlot_MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.time_slot_card.setVisibility(View.VISIBLE);
        if (!isVendor) {
                holder.selectedTimeSlot_checkBox.setVisibility(View.VISIBLE);
                holder.editButton.setVisibility(View.GONE);
                holder.deleteButton.setVisibility(View.GONE);
                String numSlotsAvailable = "Available slots: "+dataList.get(position).getAvailable_slots();
                holder.available_slot_info.setText(numSlotsAvailable);
                holder.selectedTimeSlot_checkBox.setTag(position);

//            if (position == lastCheckedPosition) {
//                holder.selectedTimeSlot_checkBox.setChecked(true);
//            } else {
//                holder.selectedTimeSlot_checkBox.setChecked(false);
//            }

            holder.selectedTimeSlot_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (lastCheckedPosition != -1 && lastCheckedPosition != position) {
//                        notifyItemChanged(lastCheckedPosition);
//                    }
//                    lastCheckedPosition = position;

                    day = null;selected_timeSlot=null;available_slots=null;
                    int checkboxPosition = (int) buttonView.getTag();
                    TimeSlotDataClass selectedItem = dataList.get(checkboxPosition);
                    day = selectedItem.getDay();
                    selected_timeSlot = selectedItem.getTime_slot();
                    available_slots = selectedItem.getAvailable_slots();
                    restaurant_name=selectedItem.getRestaurant_name();
                    restaurant_id=selectedItem.getRestaurant_id();
                }
            });

            timeSlotCheckoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, User_review_order.class);
                    intent.putExtra("day", day);
                    intent.putExtra("Selected time slot", selected_timeSlot);
                    intent.putExtra("Available slots",available_slots);
                    intent.putExtra("restaurant_name",restaurant_name);
                    intent.putExtra("restaurant_id",restaurant_id);
                    intent.putExtra("total bill",total_bill);
                    context.startActivity(intent);
                }
            });
        }
        else {
            String numSlotsAvailable = "Default slots: "+dataList.get(position).getDefault_available_slots();
            holder.available_slot_info.setText(numSlotsAvailable);
            holder.selectedTimeSlot_checkBox.setVisibility(View.GONE);
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Vendor_update_timeslot.class);
                    intent.putExtra("Selected Day",dataList.get(position).getDay());
                    intent.putExtra("Selected Time Slot",dataList.get(position).getTime_slot());
                    intent.putExtra("Default Available Slots",dataList.get(position).getDefault_available_slots());
                    intent.putExtra("Available slots",dataList.get(position).getAvailable_slots());
                    intent.putExtra("restaurant_name",dataList.get(position).getRestaurant_name());
                    intent.putExtra("restaurant_id",dataList.get(position).getRestaurant_id());
                    context.startActivity(intent);
                }
            });

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String day = dataList.get(position).getDay();
                    String timeSlot = dataList.get(position).getTime_slot();
                    String restaurant_name = dataList.get(position).getRestaurant_name();

                    Log.d("to-be-deleted-timeslot",timeSlot);
                    deleteDishFromFirebase(day,restaurant_name,timeSlot);
                    dataList.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
        holder.day.setText(dataList.get(position).getDay());
        holder.time_slot_info.setText(dataList.get(position).getTime_slot());
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void deleteDishFromFirebase(String day , String restaurant_name , String timeSlot) {
        DatabaseReference timeSlotRef = FirebaseDatabase.getInstance().getReference("time_slots").child(day).child(restaurant_name).child(timeSlot);
        timeSlotRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context,timeSlot+" on "+ day+" successfully deleted!",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Failed in deleting "+timeSlot+", Try Again!",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
class TimeSlot_MyViewHolder extends RecyclerView.ViewHolder{

    CheckBox selectedTimeSlot_checkBox;
    Button editButton , deleteButton;
     TextView time_slot_info , available_slot_info ,day;
     CardView time_slot_card ;
    public TimeSlot_MyViewHolder(@NonNull View itemView){
        super(itemView);
        day = itemView.findViewById(R.id.day);
        time_slot_card=itemView.findViewById(R.id.time_slot_card);
        selectedTimeSlot_checkBox = itemView.findViewById(R.id.selectedTimeSlot_checkBox);
        available_slot_info = itemView.findViewById(R.id.available_slot_info);
        time_slot_info = itemView.findViewById(R.id.time_slot_info);
        editButton = itemView.findViewById(R.id.editButton);
        deleteButton = itemView.findViewById(R.id.deleteButton);
    }
}
