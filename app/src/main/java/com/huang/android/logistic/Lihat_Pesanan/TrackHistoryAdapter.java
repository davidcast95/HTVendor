package com.huang.android.logistic.Lihat_Pesanan;

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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.huang.android.logistic.Lihat_Pesanan.Base.DetailOrder;
import com.huang.android.logistic.Lihat_Pesanan.Gallery.GalleryActivity;
import com.huang.android.logistic.Maps.TrackOrderMaps;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateData;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

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
    Double longitude, latitude;
    GoogleMap mMap;
    List<Marker> markers;

    public TrackHistoryAdapter(Context context, int layout, List<JobOrderUpdateData> list, GoogleMap googleMap, List<Marker> markers) {
        super(context, layout, list);
        this.context = context;
        this.list = list;
        this.markers = markers;
        mMap = googleMap;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_track_history_list, parent, false);
        }

        TextView Tanggal2 = (TextView) view.findViewById(R.id.tanggal2);
        TextView state = (TextView) view.findViewById(R.id.statehistory);
        TextView loc = (TextView) view.findViewById(R.id.loc_history);
        TextView notes = (TextView) view.findViewById(R.id.notes);

        Tanggal2.setText(Utility.formatDateFromstring(Utility.dateDBLongFormat, Utility.LONG_DATE_TIME_FORMAT, list.get(position).time));
        state.setText(list.get(position).status);
        if (list.get(position).location != null)
            loc.setText(list.get(position).city + " - " + list.get(position).warehouse_name);
        else
            loc.setVisibility(View.GONE);
        notes.setText(list.get(position).note);
        if (list.get(position).latitude != null && list.get(position).longitude != null) {
            latitude = Double.parseDouble(list.get(position).latitude);
            longitude = Double.parseDouble(list.get(position).longitude);
        }

        ImageView galleryButton = (ImageView) view.findViewById(R.id.historygallery);
        if (list.get(position).images.size() == 0) {
            galleryButton.setVisibility(View.GONE);
        } else {
            galleryButton.setVisibility(View.VISIBLE);
        }
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(getContext(), GalleryActivity.class);
                gallery.putExtra("jod_name", list.get(position).id);
                gallery.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(gallery);
            }
        });

        ImageView button = (ImageView) view.findViewById(R.id.historymaps);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (latitude != null && longitude != null && mMap != null) {
//                    Intent maps = new Intent(getContext(), TrackOrderMaps.class);
//                    maps.putExtra("longitude", Double.valueOf(list.get(position).longitude));
//                    maps.putExtra("latitude", Double.valueOf(list.get(position).latitude));
//                    maps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    getContext().startActivity(maps);

                    Double lat = Double.valueOf(DetailOrder.jobOrderUpdates.get(position).latitude), longi = Double.valueOf(DetailOrder.jobOrderUpdates.get(position).longitude);
                    LatLng loc = new LatLng(lat, longi);
                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                            loc, mMap.getCameraPosition().zoom);
                    mMap.animateCamera(location);
                    markers.get(position).showInfoWindow();
                } else {
                    Context c = getContext();
                    Toast.makeText(c, R.string.nla, Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }
}
