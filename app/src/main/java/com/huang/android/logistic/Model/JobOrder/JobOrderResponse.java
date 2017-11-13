package com.huang.android.logistic.Model.JobOrder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by David Wibisono on 8/16/2017.
 */

public class JobOrderResponse {
    @SerializedName("data")
    public List<JobOrderData> jobOrders;
}
