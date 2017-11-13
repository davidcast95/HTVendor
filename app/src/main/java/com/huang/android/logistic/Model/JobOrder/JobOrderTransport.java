package com.huang.android.logistic.Model.JobOrder;

import com.google.gson.annotations.SerializedName;

/**
 * Created by David Wibisono on 8/24/2017.
 */

public class JobOrderTransport {
    @SerializedName("driver")
    public String driver;

    @SerializedName("truck_type")
    public String truck_type;

    @SerializedName("truck_license_plate")
    public String nopol;

    @SerializedName("rejected_reason")
    public String alasan;
}
