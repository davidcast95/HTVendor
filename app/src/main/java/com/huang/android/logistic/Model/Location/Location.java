package com.huang.android.logistic.Model.Location;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidwibisono on 20/10/17.
 */

public class Location {
    public Location() {}
    public Location(String _code,String _name, String _city,String _address, String _warehouse, String _phone,String _pic) {
        code = _code;
        phone = _phone;
        name = _name;
        city = _city;
        address = _address;
        warehouse = _warehouse;
        pic = _pic;
    }
    @SerializedName("kode_distributor")
    public String code = "";
    @SerializedName("phone")
    public String phone = "";
    @SerializedName("name")
    public String name = "";
    @SerializedName("kota")
    public String city = "";
    @SerializedName("alamat")
    public String address = "";
    @SerializedName("nama_gudang")
    public String warehouse = "";
    @SerializedName("nama_pic")
    public String pic = "";
}
