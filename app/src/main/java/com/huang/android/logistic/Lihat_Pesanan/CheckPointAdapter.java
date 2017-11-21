package com.huang.android.logistic.Lihat_Pesanan;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;


import com.huang.android.logistic.R;

import java.util.List;

/**
 * Created by Kristoforus Gumilang on 8/27/2017.
 */

public class CheckPointAdapter extends ArrayAdapter<Bitmap> {
    private Context context;
    private List<Bitmap> list;

    public CheckPointAdapter(Context context, int layout, List<Bitmap> list) {
        super(context, layout,list);
        this.context=context;
        this.list=list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.activity_check_point_photo_list,parent,false);

            ImageView foto=(ImageView)view.findViewById(R.id.hasilfoto);
            String asd= list.get(position).toString();
            Log.e("LOG FOTO",asd);
            foto.setImageBitmap(list.get(position));

        }


        return view;
    }
}
