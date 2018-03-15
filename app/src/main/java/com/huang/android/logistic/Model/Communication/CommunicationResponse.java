package com.huang.android.logistic.Model.Communication;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by davidwibisono on 2/5/18.
 */

public class CommunicationResponse {
    @SerializedName("data")
    public ArrayList<CommunicationData> communicationData;
}
