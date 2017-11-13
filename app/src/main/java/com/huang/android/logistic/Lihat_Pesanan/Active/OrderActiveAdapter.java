package com.huang.android.logistic.Lihat_Pesanan.Active;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.huang.android.logistic.Lihat_Pesanan.Base.JobOrderAdapter;
import com.huang.android.logistic.Model.JobOrder.JobOrderData;
import com.huang.android.logistic.R;

import java.util.List;

/**
 * Created by Kristoforus Gumilang on 8/24/2017.
 */

public class OrderActiveAdapter extends JobOrderAdapter {
    public OrderActiveAdapter(Context context, int layout, List<JobOrderData> list) {
        super(context, layout, list);
    }

}
