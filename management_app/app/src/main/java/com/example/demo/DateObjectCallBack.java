package com.example.demo;


import java.util.ArrayList;
import java.util.List;

public interface DateObjectCallBack {
    /** A callback method to receive data from Firebase Database
     * @param value the returning DateObject array, which contains data about total orders and revenue
     *              of each day in specific month.
     */
    void onCallBack1(ArrayList<DateObject> value);
}
