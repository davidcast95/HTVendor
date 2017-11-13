package com.huang.android.logistic.Model.JobOrder;

import com.huang.android.logistic.Model.Default.DataMessage;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by David Wibisono on 8/24/2017.
 */

public class JobOrderMessage extends DataMessage {
    @SerializedName("data")
    public List<JobOrderData> data;
}