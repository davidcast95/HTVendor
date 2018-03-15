package com.huang.android.logistic.Model.JobOrderRoute;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.huang.android.logistic.Model.Location.Location;


/**
 * Created by davidwibisono on 1/20/18.
 */

public class JobOrderRouteData {
    public Location loc;
    @Expose
    @SerializedName("location")
    public String location;
    @Expose
    @SerializedName("warehouse_name")
    public String warehouse_name;
    @Expose
    @SerializedName("distributor_code")
    public String distributor_code;
    @Expose
    @SerializedName("city")
    public String city;
    @Expose
    @SerializedName("address")
    public String address;
    @Expose
    @SerializedName("contact")
    public String contact;
    @Expose
    @SerializedName("nama")
    public String nama;
    @Expose
    @SerializedName("phone")
    public String phone;
    @Expose
    @SerializedName("type")
    public String type;
    @Expose
    @SerializedName("item_info")
    public String item_info;
    @Expose
    @SerializedName("remark")
    public String remark;
    @Expose
    @SerializedName("order_index")
    public int order_index;
}
