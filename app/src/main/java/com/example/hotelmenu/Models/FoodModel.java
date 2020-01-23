package com.example.hotelmenu.Models;

import android.content.Context;

public class FoodModel {
    Context context;
    int id;
    String foodName;
    double price;
    String category;
    String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Context getContext() {
        return context;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public FoodModel(Context context) {
        this.context = context;

    }


}
