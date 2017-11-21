package com.huang.android.logistic.Lihat_Pesanan.Base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.huang.android.logistic.Model.JobOrder.JobOrderData;
import com.huang.android.logistic.Model.Location.Location;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import java.util.List;

/**
 * Created by davidwibisono on 20/10/17.
 */

public class JobOrderAdapter extends ArrayAdapter<JobOrderData> {
    private Context context;
    private List<JobOrderData> list;

    public JobOrderAdapter(Context context, int layout, List<JobOrderData> list) {
        super(context, layout,list);
        this.context=context;
        this.list=list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.default_order_list,parent,false);
        }

        TextView principle = (TextView)view.findViewById(R.id.principle);
        TextView joid = (TextView)view.findViewById(R.id.joid);
        TextView origin = (TextView) view.findViewById(R.id.origin);
        TextView destination = (TextView)view.findViewById(R.id.destination);
        TextView ref = (TextView)view.findViewById(R.id.ref_id);

        principle.setText(list.get(position).principle);
        ref.setText("Ref No : " + list.get(position).ref);
        joid.setText(list.get(position).joid);
        JobOrderData jobOrder = list.get(position);
        origin.setText(Utility.utility.formatLocation(new Location(jobOrder.origin_code,jobOrder.origin,jobOrder.origin_city,jobOrder.origin_address,jobOrder.origin_warehouse,"","")));
        destination.setText(Utility.utility.formatLocation(new Location(jobOrder.destination_code,jobOrder.destination,jobOrder.destination_city,jobOrder.destination_address,jobOrder.destination_warehouse,"","")));


        return view;
    }
}
