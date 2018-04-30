package com.huang.android.logistic.Maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.huang.android.logistic.Model.Driver.Driver;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import java.util.List;

public class DriverLocationAdapter extends ArrayAdapter<Driver> {
    private Context context;
    private List<Driver> list;

    public DriverLocationAdapter(Context context, int layout, List<Driver> list) {
        super(context, layout,list);
        this.context=context;
        this.list=list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.fragment_view_driver_location_list,parent,false);
        }

//        TextView ID = (TextView)view.findViewById(R.name.id_supir);
        TextView Nama = (TextView)view.findViewById(R.id.nama);
        TextView address = (TextView)view.findViewById(R.id.address);

        Driver productList = list.get(position);
//        ID.setText(list.get(position).name);
        Utility.utility.setTextView(Nama,list.get(position).nama);
        Utility.utility.setTextView(address,list.get(position).currentLocation);


        return view;
    }

    public boolean isExist(Driver driver) {
        for (int i=0;i<list.size();i++) {
            if (driver.name == list.get(i).name) return true;
        }
        return false;
    }
}
