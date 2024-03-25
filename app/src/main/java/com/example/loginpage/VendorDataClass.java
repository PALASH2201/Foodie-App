package com.example.loginpage;

public class VendorDataClass {
   private  String restaurant_name ;
    private  String owner_name ;
    private  String location ;
    private  String contact ;
    private  String profile_pic_image_url ;
    private String key;

    public VendorDataClass(String restaurant_name, String owner_name, String location, String contact, String profile_pic_image_url) {
        this.restaurant_name = restaurant_name;
        this.owner_name = owner_name;
        this.location = location;
        this.contact = contact;
        this.profile_pic_image_url = profile_pic_image_url;
    }

    public VendorDataClass(String restaurant_name, String profile_pic_image_url) {
        this.restaurant_name = restaurant_name;
        this.profile_pic_image_url = profile_pic_image_url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public String getLocation() {
        return location;
    }

    public String getContact() {
        return contact;
    }

    public String getProfile_pic_image_url() {
        return profile_pic_image_url;
    }

    public VendorDataClass() {
    }
}
