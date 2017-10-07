package com.logisticsmarketplace.android.vendor.Model.JobOrderUpdate;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidwibisono on 9/10/17.
 */

public class JobOrderUpdateData {
    @SerializedName("waktu")
    public String time;
    @SerializedName("job_order")
    public String joid;
    @SerializedName("note")
    public String note;
    @SerializedName("lo")
    public String longitude;
    @SerializedName("lat")
    public String latitude;
    @SerializedName("vendor")
    public String vendor;
    @SerializedName("principle")
    public String principle;
    @SerializedName("docstatus")
    public int docstatus;
    @SerializedName("status")
    public String status;
}
