package com.example.demo;

public class DateObject {
    private String date;
    private int numberOfOrder;
    private int revenue;

    public DateObject(String date, int numberOfOrder, int revenue) {
        this.date = date;
        this.numberOfOrder = numberOfOrder;
        this.revenue = revenue;
    }

    public int getNumberOfOrder() {
        return numberOfOrder;
    }

    public int getRevenue() {
        return revenue;
    }

    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public void setNumberOfOrder(int numberOfOrder) {
        this.numberOfOrder = numberOfOrder;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }
}
