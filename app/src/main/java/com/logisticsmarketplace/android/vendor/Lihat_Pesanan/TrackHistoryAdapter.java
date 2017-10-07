package com.logisticsmarketplace.android.vendor.Lihat_Pesanan;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.logisticsmarketplace.android.vendor.Maps.TrackOrderMaps;
import com.logisticsmarketplace.android.vendor.Model.JobOrderUpdate.JobOrderUpdateData;
import com.logisticsmarketplace.android.vendor.R;

import java.util.List;


public class TrackHistoryAdapter extends ArrayAdapter<JobOrderUpdateData> {

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private Context context;
    private List<JobOrderUpdateData> list;
    Double longitude,latitude;
    public TrackHistoryAdapter(Context context, int layout, List<JobOrderUpdateData> list) {
        super(context, layout,list);
        this.context=context;
        this.list=list;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.activity_track_history_list,parent,false);
        }

        TextView Tanggal2 = (TextView)view.findViewById(R.id.tanggal2);
        TextView state = (TextView)view.findViewById(R.id.statehistory);
        TextView notes = (TextView) view.findViewById(R.id.notes);

        JobOrderUpdateData productList = list.get(position);
        Tanggal2.setText(list.get(position).time);
        state.setText(list.get(position).status);
        notes.setText(list.get(position).note);
        latitude= Double.parseDouble (list.get(position).latitude);
        longitude=Double.parseDouble(list.get(position).longitude);

        ImageView button = (ImageView)view.findViewById(R.id.historymaps);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latitude!= null || longitude!=null){
                    Intent maps = new Intent(getContext(), TrackOrderMaps.class);
                    maps.putExtra("longitude", longitude );
                    maps.putExtra("latitude", latitude );
                    Log.e("LAT",latitude+"");
                    Log.e("LONG",longitude+"");
                    maps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(maps);
                }
                else {
                    Context c = getContext();
                    Toast.makeText(c,R.string.nla, Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }
}
