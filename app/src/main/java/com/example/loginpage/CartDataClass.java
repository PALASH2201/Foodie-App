package com.example.loginpage;

public class CartDataClass {

    private String dish_name ;
    private String single_dish_price;
    private String total_dish_price;
    private String dish_image_url ;
    private String restaurant_id;
    private String category_id;
    private String key;

    private String restaurant_name ;
    private String category_name;

    private int quantity;

    public CartDataClass(String dish_name, String single_dish_price, String total_dish_price,String dish_image_url, String restaurant_id, String category_id, String key, int quantity,String restaurant_name,String category_name) {
        this.dish_name = dish_name;
        this.single_dish_price = single_dish_price;
        this.total_dish_price= total_dish_price;
        this.dish_image_url = dish_image_url;
        this.restaurant_id = restaurant_id;
        this.category_id = category_id;
        this.key = key;
        this.quantity = quantity;
        this.restaurant_name = restaurant_name;
        this.category_name = category_name;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public CartDataClass() {
    }

    public String getDish_name() {
        return dish_name;
    }

    public String getSingleDish_price() {
        return single_dish_price;
    }
    public String getTotalDish_price() {
        return total_dish_price;
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

}
