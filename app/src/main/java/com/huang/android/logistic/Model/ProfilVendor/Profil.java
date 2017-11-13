package com.huang.android.logistic.Model.ProfilVendor;

import com.google.gson.annotations.SerializedName;

/**
 * Created by davidwibisono on 8/24/17.
 */

public class Profil {
    @SerializedName("nama")
    public String name;
    @SerializedName("telp")
    public String phone;
    @SerializedName("alamat")
    public String address;
    @SerializedName("email")
    public String email;
}
