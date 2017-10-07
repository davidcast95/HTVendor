package com.logisticsmarketplace.android.vendor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.logisticsmarketplace.android.vendor.Model.JobOrder.JobOrderData;

import java.util.List;

/**
 * Created by Kristoforus Gumilang on 8/24/2017.
 */

public class PendingOrderAdapter extends ArrayAdapter<JobOrderData> {
    private Context context;
    private List<JobOrderData> list;

    public PendingOrderAdapter(Context context, int layout, List<JobOrderData> list) {
        super(context, layout,list);
        this.context=context;
        this.list=list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.fragment_order_on_progress_list,parent,false);
        }

        TextView joid = (TextView)view.findViewById(R.id.joid);
        TextView origin = (TextView) view.findViewById(R.id.origin);
        TextView destination = (TextView)view.findViewById(R.id.destination);

        joid.setText(list.get(position).joid);
        origin.setText(list.get(position).origin);
        destination.setText(list.get(position).destination);


        return view;
    }
}
