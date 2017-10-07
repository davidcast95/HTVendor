package com.logisticsmarketplace.android.vendor.Model.JobOrder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Kristoforus Gumilang on 8/16/2017.
 */

public class JobOrderResponse {
    @SerializedName("data")
    public List<JobOrderData> jobOrders;
}
