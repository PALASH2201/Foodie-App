package com.example.loginpage;

public class CartDataClass {

    private String dish_name ;
    private String dish_price;
    private String dish_image_url ;
    private String restaurant_id;
    private String category_id;
    private String key;

    private int quantity;

    public CartDataClass(String dish_name, String dish_price, String dish_image_url, String restaurant_id, String category_id, String key, int quantity) {
        this.dish_name = dish_name;
        this.dish_price = dish_price;
        this.dish_image_url = dish_image_url;
        this.restaurant_id = restaurant_id;
        this.category_id = category_id;
        this.key = key;
        this.quantity = quantity;
    }

    public CartDataClass() {
    }

    public String getDish_name() {
        return dish_name;
    }

    public String getDish_price() {
        return dish_price;
    }

    public String getDish_image_url() {
        return dish_image_url;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getKey() {
        return key;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return Double.parseDouble(dish_price)*quantity;
    }
}
