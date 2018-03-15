package com.huang.android.logistic.Model.Driver;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by davidwibisono on 12/29/17.
 */

public class DriverBackgroundUpdateResponse {
    @SerializedName("data")
    public List<DriverBackgroundUpdateData> data;
}
