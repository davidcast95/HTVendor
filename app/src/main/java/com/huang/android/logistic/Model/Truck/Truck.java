package com.huang.android.logistic.Model.Truck;

import com.google.gson.annotations.SerializedName;

/**
 * Created by David Wibisono on 8/26/2017.
 */

public class Truck {
    @SerializedName("name")
    public String name;

    @SerializedName("status")
    public String status;

    @SerializedName("nopol")
    public String nopol;

    @SerializedName("type")
    public String type;

    @SerializedName("tahun")
    public String year;

    @SerializedName("volume")
    public String volume;

    @SerializedName("merek")
    public String brand;

    @SerializedName("note")
    public String note;

}
