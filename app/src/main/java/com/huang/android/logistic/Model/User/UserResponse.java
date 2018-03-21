package com.huang.android.logistic.Model.User;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidwibisono on 3/21/18.
 */

public class UserResponse {
    @SerializedName("message")
    public List<UserData> users = new ArrayList<>();
}
