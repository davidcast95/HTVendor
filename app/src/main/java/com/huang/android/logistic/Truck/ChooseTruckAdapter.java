package com.huang.android.logistic.Truck;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huang.android.logistic.Model.Truck.Truck;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import java.util.List;

/**
 * Created by David Wibisono on 8/26/2017.
 */

public class ChooseTruckAdapter extends ArrayAdapter<Truck> {
    private Context context;
    private List<Truck> list;

    public ChooseTruckAdapter(Context context, int layout, List<Truck> list) {
        super(context, layout,list);
        this.context=context;
        this.list=list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.activity_pilih_truck_list,parent,false);
        }

//        TextView ID = (TextView)view.findViewById(R.name.id_supir);
        TextView plat = (TextView)view.findViewById(R.id.platnomer);
        TextView tipe = (TextView)view.findViewById(R.id.tipetruck);

        Truck productList = list.get(position);
//        ID.setText(list.get(position).name);
        Utility.utility.setTextView(plat,list.get(position).nopol + " (" +list.get(position).lambung+ ")");
        Utility.utility.setTextView(tipe,list.get(position).type);

        if(list.get(position).status.equals("Operate"))
        {
            Drawable d = ContextCompat.getDrawable(context, R.drawable.trukhijau);
            ImageView image = (ImageView)view.findViewById(R.id.truckcolor);
            image.setImageDrawable(d);
        }
        else if (list.get(position).status.equals("In Repair"))
        {
            Drawable d = ContextCompat.getDrawable(context, R.drawable.trukkuning);
            ImageView image = (ImageView)view.findViewById(R.id.truckcolor);
            image.setImageDrawable(d);
        }
        else
        {
            Drawable d = ContextCompat.getDrawable(context, R.drawable.trukmerah);
            ImageView image = (ImageView)view.findViewById(R.id.truckcolor);
            image.setImageDrawable(d);
        }


        return view;
    }
}
