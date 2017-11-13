package com.huang.android.logistic.Model.JobOrderUpdate;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by davidwibisono on 9/10/17.
 */

public class JobOrderUpdateResponse {
    @SerializedName("data")
    public List<JobOrderUpdateData> jobOrderUpdates;
}
