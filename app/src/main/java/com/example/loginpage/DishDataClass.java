package com.example.loginpage;

public class DishDataClass {

    private String dish_name ;
    private String dish_description ;
    private String dish_price;
    private String dish_rating;
    private String dish_image_url ;
    private String restaurant_id;
    private String category_id;
    private String key;

    public DishDataClass(String dish_name, String dish_description, String dish_price,String dish_image_url, String restaurant_id, String category_id) {
        this.dish_name = dish_name;
        this.dish_description = dish_description;
        this.dish_price = dish_price;
        this.dish_image_url = dish_image_url;
        this.restaurant_id = restaurant_id;
        this.category_id = category_id;
    }

    public void setDish_rating(String dish_rating) {
        this.dish_rating = dish_rating;
    }

    public DishDataClass() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDish_name() {
        return dish_name;
    }

    public String getDish_description() {
        return dish_description;
    }

    public String getDish_price() {
        return dish_price;
    }

    public String getDish_rating() {
        return dish_rating;
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
}
