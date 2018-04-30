package com.huang.android.logistic.Model.FirebaseID;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidwibisono on 4/25/18.
 */

public class FirebaseIDMessage {
    @SerializedName("for_value")
    public List<FirebaseIDData> for_value = new ArrayList<>();
    @SerializedName("role")
    public String role = "";
}
