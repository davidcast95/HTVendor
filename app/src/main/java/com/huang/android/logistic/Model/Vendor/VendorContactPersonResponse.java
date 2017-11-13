package com.huang.android.logistic.Model.Vendor;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by davidwibisono on 8/24/17.
 */

public class VendorContactPersonResponse {
    @SerializedName("data")
    public List<VendorContactPersonData> cps;
}
