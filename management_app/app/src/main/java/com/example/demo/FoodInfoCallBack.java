package com.example.demo;

import java.util.List;

public interface FoodInfoCallBack {
    /** A callback method to receive data from Firebase Database
     * @param value the returning FoodInfo list, which contains data defined in FoodInfo class from
     *              database
     * @throws Exception
     */
    void onCallBack2(List<FoodInfo> value) throws Exception;
}
