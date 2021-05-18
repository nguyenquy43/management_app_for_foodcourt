package com.example.demo;

import java.util.List;

class  FoodInfo {
    private String id;
    private String name;
    private String price;
    private String discount;
    private String vendor;
    private int quantity;

    public FoodInfo() {}

    public FoodInfo (FoodInfo another) {
        this.id = another.id;
        this.name = another.name;
        this.price = another.price;
        this.discount = another.discount;
        this.vendor = another.vendor;
        this.quantity = another.quantity;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}

public class FoodReport {
    private String date;
    private List<FoodInfo> foods;


    public FoodReport(String date, List<FoodInfo> foods) {
        this.date = date;
        this.foods = foods;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<FoodInfo> getFoods() {
        return foods;
    }

    public void setFoods(List<FoodInfo> foods) {
        this.foods = foods;
    }
}
