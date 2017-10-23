package com.huang.android.logistic.Model.Truck;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by David Wibisono on 8/26/2017.
 */

public class TruckResponse {
    @SerializedName("data")
    public List<Truck> trucks;
}
