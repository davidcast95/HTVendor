package com.huang.android.logistic.Lihat_Driver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.huang.android.logistic.Model.Driver.Driver;
import com.huang.android.logistic.R;

import java.util.List;

/**
 * Created by David Wibisono on 8/24/2017.
 */

public class ChooseDriverAdapter extends ArrayAdapter<Driver> {
    private Context context;
    private List<Driver> list;

    public ChooseDriverAdapter(Context context, int layout, List<Driver> list) {
        super(context, layout,list);
        this.context=context;
        this.list=list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.activity_pilih_sopir_list,parent,false);
        }

//        TextView ID = (TextView)view.findViewById(R.id.id_supir);
        TextView Nama = (TextView)view.findViewById(R.id.nama);

        Driver productList = list.get(position);
//        ID.setText(list.get(position).id);
        Nama.setText(list.get(position).name);


        return view;
    }
}
