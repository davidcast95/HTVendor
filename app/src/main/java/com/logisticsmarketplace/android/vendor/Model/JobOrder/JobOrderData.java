package com.logisticsmarketplace.android.vendor.Model.JobOrder;

import com.google.gson.annotations.SerializedName;


public class JobOrderData {
    @SerializedName("status")
    public String status;
    @SerializedName("name")
    public String joid;
    @SerializedName("pick_date")
    public String etd = "";
    @SerializedName("expected_delivery")
    public String eta = "";
    @SerializedName("pick_location")
    public String origin = "";
    @SerializedName("delivery_location")
    public String destination = "";
    @SerializedName("vendor")
    public String vendor = "";
    @SerializedName("principle")
    public String principle = "";
    @SerializedName("principle_contact_person")
    public String principle_cp = "";
    @SerializedName("notes")
    public String notes = "";
    @SerializedName("nama_principle_cp")
    public String principle_cp_name = "";
    @SerializedName("telp_principle_cp")
    public String principle_cp_phone = "";
    @SerializedName("vendor_contact_person")
    public String vendor_cp = "";
    @SerializedName("nama_vendor_cp")
    public String vendor_cp_name = "";
    @SerializedName("telp_vendor_cp")
    public String vendor_cp_phone = "";
    @SerializedName("goods_information")
    public String cargoInfo = "";


}
