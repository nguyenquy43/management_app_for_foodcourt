package com.example.demo;

import java.util.List;

public interface FoodReportCallBack {
    /** A callback method to receive data from Firebase Database
     * @param value the returning FoodReport list, which contains data defined in FoodReport class
     */
    void onCallBack3(List<FoodReport> value);
}
