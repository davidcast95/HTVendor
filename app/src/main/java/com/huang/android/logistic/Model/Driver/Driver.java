package com.huang.android.logistic.Model.Driver;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kgumi on 24-Jul-17.
 */

public class Driver {
    @SerializedName("name")
    public String name;
    @SerializedName("nama")
    public String nama;
    @SerializedName("phone")
    public String phone;
    @SerializedName("address")
    public String address;
    @SerializedName("email")
    public String email;
    @SerializedName("vendor")
    public String vendor;
    @SerializedName("password")
    public String password;
    @SerializedName("status")
    public String status;
    @SerializedName("profile_image")
    public List<String> profile_image = new ArrayList<>();

    public double lat = 0,lng = 0;
    public String currentLocation;
    public String last_update = "";
}
