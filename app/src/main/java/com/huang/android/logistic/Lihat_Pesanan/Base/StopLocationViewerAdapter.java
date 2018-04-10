package com.huang.android.logistic.Lihat_Pesanan.Base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huang.android.logistic.Model.JobOrderRoute.JobOrderRouteData;
import com.huang.android.logistic.Model.Location.Location;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import java.util.List;


/**
 * Created by davidwibisono on 08/11/17.
 */

public class StopLocationViewerAdapter extends ArrayAdapter<JobOrderRouteData> {
    private Context context;
    private List<JobOrderRouteData> list;
    Runnable didDelete;
    private Activity activity;

    public StopLocationViewerAdapter(Context context, List<JobOrderRouteData> list, Activity activity) {
        super(context, R.layout.list_stop_location_viewer,list);
        this.context=context;
        this.list=list;
        this.activity=activity;
    }


    @Nullable
    @Override
    public JobOrderRouteData getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.list_stop_location_viewer,parent,false);
        }

        TextView wareHouseTitle = (TextView) view.findViewById(R.id.warehouse_name);
        ImageView dropIV = (ImageView)view.findViewById(R.id.drop_icon);
        ImageView pickupIV = (ImageView)view.findViewById(R.id.pickup_icon);
//        TextView locationDetails = (TextView)view.findViewById(R.id.list_location_details);

        if (list.get(position) == null) return view;
        if (list.get(position).distributor_code == null)
            Utility.utility.setTextView(wareHouseTitle,list.get(position).city + " - " + list.get(position).warehouse_name);
        else
            Utility.utility.setTextView(wareHouseTitle,list.get(position).city + " - " + list.get(position).warehouse_name + " (" + list.get(position).distributor_code + ")");
//        if (list.get(position).phone == null)
//            Utility.utility.setTextView(locationDetails,list.get(position).address + ", " + list.get(position).city + "<br>" + list.get(position).phone);
//        if (list.get(position).phone == null)
//            Utility.utility.setTextView(locationDetails,list.get(position).address + ", " + list.get(position).city);
//        else
//            Utility.utility.setTextView(locationDetails,list.get(position).address + ", " + list.get(position).city + "<br>" + list.get(position).pic + " - " + list.get(position).phone);
        dropIV.setVisibility(View.GONE);
        pickupIV.setVisibility(View.GONE);
        if (list.get(position).type.equals("Drop")) {
            dropIV.setVisibility(View.VISIBLE);
        } else if (list.get(position).type.equals("Pick Up")) {
            pickupIV.setVisibility(View.VISIBLE);
        }

        wareHouseTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogStopLocation(list.get(position));
            }
        });

        return view;
    }


    public void dialogStopLocation(JobOrderRouteData route) {
        new AlertDialog.Builder(activity);
        LayoutInflater li = LayoutInflater.from(activity);
        View promptsView = li.inflate(R.layout.dialog_stop_location_detail, null);


        TextView typeTV = (TextView)promptsView.findViewById(R.id.type),
                stopLocationTV = (TextView)promptsView.findViewById(R.id.stop_location),
                nameTV = (TextView)promptsView.findViewById(R.id.name),
                cpTV = (TextView)promptsView.findViewById(R.id.cp),
                itemTV = (TextView)promptsView.findViewById(R.id.item),
                remarkTV = (TextView)promptsView.findViewById(R.id.remark);

        Utility.utility.setTextView(typeTV,route.type);
        Utility.utility.setTextView(stopLocationTV,Utility.utility.longformatLocation(new Location(route.distributor_code,route.location,route.city,route.address,route.warehouse_name,"","")));
        Utility.utility.setTextView(nameTV,route.nama);
        Utility.utility.setTextView(cpTV,route.phone);
        Utility.utility.setDialContactPhone(cpTV, route.phone, activity);
        Utility.utility.setTextView(itemTV,route.item_info);
        Utility.utility.setTextView(remarkTV,route.remark);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }



}
