package com.huang.android.logistic.Model.JobOrderUpdateImage;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidwibisono on 2/24/18.
 */

public class JobOrderUpdateImageResponse {
    @SerializedName("message")
    public List<JobOrderUpdateImageData> data = new ArrayList<>();
}
