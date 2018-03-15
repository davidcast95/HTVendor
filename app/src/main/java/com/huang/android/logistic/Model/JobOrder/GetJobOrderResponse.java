package com.huang.android.logistic.Model.JobOrder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by davidwibisono on 1/25/18.
 */

public class GetJobOrderResponse {
    @SerializedName("message")
    public List<JobOrderData> jobOrders;
}
