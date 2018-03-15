package com.huang.android.logistic.Model.JobOrder;

import com.google.gson.annotations.SerializedName;
import com.huang.android.logistic.Model.JobOrderRoute.JobOrderRouteData;

import java.util.ArrayList;
import java.util.List;


public class JobOrderData {

    @SerializedName("principle_image")
    public List<String> principle_image = new ArrayList<>();
    @SerializedName("vendor_image")
    public List<String> vendor_image = new ArrayList<>();

    @SerializedName("modified")
    public String modified;
    @SerializedName("reference")
    public String ref;
    @SerializedName("docstatus")
    public int docstatus;
    @SerializedName("status")
    public String status;
    @SerializedName("name")
    public String joid;
    @SerializedName("pick_date")
    public String etd = "";
    @SerializedName("expected_delivery")
    public String eta = "";

    @SerializedName("pick_location")
    public String origin;
    @SerializedName("kota_pengambilan")
    public String origin_city;
    @SerializedName("alamat_pengambilan")
    public String origin_address;
    @SerializedName("nama_gudang_pengambilan")
    public String origin_warehouse;
    @SerializedName("kode_distributor_pengambilan")
    public String origin_code;

    @SerializedName("delivery_location")
    public String destination;
    @SerializedName("kota_pengiriman")
    public String destination_city;
    @SerializedName("alamat_pengiriman")
    public String destination_address;
    @SerializedName("nama_gudang_pengiriman")
    public String destination_warehouse;
    @SerializedName("kode_distributor_pengiriman")
    public String destination_code;

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
    @SerializedName("accept_date")
    public String acceptDate;
    @SerializedName("estimate_volume")
    public String estimate_volume;
    @SerializedName("suggest_truck_type")
    public String suggest_truck_type;
    @SerializedName("strict")
    public int strict;
    @SerializedName("truck_type")
    public String truck_type = "-";
    @SerializedName("truck")
    public String truck = "-";
    @SerializedName("truck_lambung")
    public String truck_lambung = "-";
    @SerializedName("driver")
    public String driver = "-";
    @SerializedName("driver_nama")
    public String driver_name = "-";
    @SerializedName("driver_phone")
    public String driver_phone = "-";

    @SerializedName("routes")
    public List<JobOrderRouteData> routes = new ArrayList<>();
}
