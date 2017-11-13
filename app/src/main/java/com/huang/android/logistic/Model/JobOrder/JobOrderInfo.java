package com.huang.android.logistic.Model.JobOrder;

import com.google.gson.annotations.SerializedName;

/**
 * Created by David Wibisono on 8/24/2017.
 */

public class JobOrderInfo {
    @SerializedName("unload_place")
    public String unload_place;

    @SerializedName("load_place")
    public String load_place;

    @SerializedName("jaid")
    public String joid;

    @SerializedName("volume")
    public int volume;

    @SerializedName("net_weight")
    public int net_weight;

    @SerializedName("quantitiy")
    public int quantitiy;

    @SerializedName("vessel_name")
    public String vessel_name;

    @SerializedName("precarriage")
    public String precarriage;

    @SerializedName("maincarriage")
    public String maincarriage;

    @SerializedName("eta")
    public String eta;

    @SerializedName("etd")
    public String etd;

    @SerializedName("route_cost")
    public int total_biaya;
}
