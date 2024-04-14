package com.example.loginpage;

public class LiveOrderDishDataClass {
    private String dishQ ;
    private String dishName ;
    private String totalPrice;

    public LiveOrderDishDataClass() {
    }

    public String getDishQ() {
        return dishQ;
    }

    public String getDishName() {
        return dishName;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public LiveOrderDishDataClass(String dishQ, String dishName, String totalPrice) {
        this.dishQ = dishQ;
        this.dishName = dishName;
        this.totalPrice = totalPrice;
    }
}
