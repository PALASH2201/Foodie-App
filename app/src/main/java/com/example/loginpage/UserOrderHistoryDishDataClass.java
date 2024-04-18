package com.example.loginpage;

public class UserOrderHistoryDishDataClass {
    private String dishName;
    private String dishQuantity;
    private String dishPrice;

    public UserOrderHistoryDishDataClass() {
    }

    public UserOrderHistoryDishDataClass(String dishName, String dishQuantity, String dishPrice) {
        this.dishName = dishName;
        this.dishQuantity = dishQuantity;
        this.dishPrice = dishPrice;
    }

    public String getDishName() {
        return dishName;
    }

    public String getDishQuantity() {
        return dishQuantity;
    }

    public String getDishPrice() {
        return dishPrice;
    }
}
