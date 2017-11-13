package com.huang.android.logistic.Lihat_Pesanan.Done;

import android.content.Context;

import com.huang.android.logistic.Lihat_Pesanan.Base.JobOrderAdapter;
import com.huang.android.logistic.Model.JobOrder.JobOrderData;

import java.util.List;

/**
 * Created by Kristoforus Gumilang on 8/24/2017.
 */

public class OrderDoneAdapter extends JobOrderAdapter {

    public OrderDoneAdapter(Context context, int layout, List<JobOrderData> list) {
        super(context, layout, list);
    }
}
