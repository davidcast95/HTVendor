package com.huang.android.logistic.Model.Driver;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kgumi on 24-Jul-17.
 */

public class DriverResponse {
    @SerializedName("message")
    public List<Driver> drivers = new ArrayList<>();
}
