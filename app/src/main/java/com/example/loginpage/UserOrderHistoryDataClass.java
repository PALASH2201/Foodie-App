package com.example.loginpage;

import java.util.List;
import java.util.Map;

public class UserOrderHistoryDataClass {
    private String day;
    private String orderId;

    private String orderTime;
    private String timeSlot;
    private String totalBill;
    private String rest_id ;
    private Map<String, List<UserOrderHistoryDishDataClass>> dishes;

    public UserOrderHistoryDataClass(String day, String orderId, String orderTime, String timeSlot, String totalBill, String rest_id,Map<String,List<UserOrderHistoryDishDataClass> > dishes) {
        this.day = day;
        this.orderId = orderId;
        this.orderTime = orderTime;
        this.timeSlot = timeSlot;
        this.totalBill = totalBill;
        this.rest_id = rest_id;
        this.dishes = dishes;
    }

    public UserOrderHistoryDataClass() {
    }

    public String getRest_id() {
        return rest_id;
    }

    public String getDay() {
        return day;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getTotalBill() {
        return totalBill;
    }

    public Map<String, List<UserOrderHistoryDishDataClass>> getDishes() {
        return dishes;
    }
}
