package com.logisticsmarketplace.android.vendor.Model.History;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Kristoforus Gumilang on 8/19/2017.
 */

public class HistoryData {
    @SerializedName("history")
    public List<HistoryHistory> history;
}
