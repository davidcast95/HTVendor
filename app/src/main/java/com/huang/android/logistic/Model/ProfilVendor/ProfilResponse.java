package com.huang.android.logistic.Model.ProfilVendor;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by davidwibisono on 8/24/17.
 */

public class ProfilResponse {
    @SerializedName("message")
    public List<Profil> data;
}
