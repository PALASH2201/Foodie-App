package com.example.loginpage;

import java.util.List;

public class LiveOrderDataClass {
    private String chosen_time_slot;
    private String OrderStatus;
    private String customerName ;
    private String customerContact;
    private String orderId ;
    private String customerBill;
    private List<LiveOrderDishDataClass> dishList ;

    public LiveOrderDataClass(String chosen_time_slot,String OrderStatus,String customerName,String customerContact,String orderId, String customerBill,List<LiveOrderDishDataClass> dishList ) {
        this.chosen_time_slot = chosen_time_slot;
        this.OrderStatus = OrderStatus;
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.orderId = orderId;
        this.customerBill = customerBill;
        this.dishList = dishList;
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
