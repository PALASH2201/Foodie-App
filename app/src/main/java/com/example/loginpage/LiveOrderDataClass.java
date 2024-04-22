package com.example.loginpage;

import android.util.Log;

import java.util.List;

public class LiveOrderDataClass {
    private String chosen_time_slot;
    private String OrderStatus;
    private String customerName ;
    private String customerContact;
    private String customerToken;
    private String orderId ;
    private String customerBill;
    private List<LiveOrderDishDataClass> dishList ;

    public LiveOrderDataClass(String chosen_time_slot,String OrderStatus,String customerName,String customerContact,String customerToken,String orderId, String customerBill,List<LiveOrderDishDataClass> dishList ) {
        this.chosen_time_slot = chosen_time_slot;
        this.OrderStatus = OrderStatus;
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.customerToken = customerToken;
        this.orderId = orderId;
        this.customerBill = customerBill;
        this.dishList = dishList;
       // Log.d("Token in constructor",customerToken);
    }

    public List<LiveOrderDishDataClass> getDishList() {
        return dishList;
    }

    public String getChosen_time_slot() {
        return chosen_time_slot;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public String getCustomerToken() {
       // Log.d("Token in data class",customerToken);
        return customerToken;
    }

    public LiveOrderDataClass() {
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerBill() {
        return customerBill;
    }
}
