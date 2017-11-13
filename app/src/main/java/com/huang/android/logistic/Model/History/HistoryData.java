package com.huang.android.logistic.Model.History;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by David Wibisono on 8/19/2017.
 */

public class HistoryData {
    @SerializedName("history")
    public List<HistoryHistory> history;
}
