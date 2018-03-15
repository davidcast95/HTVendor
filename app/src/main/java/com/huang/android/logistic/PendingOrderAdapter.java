package com.huang.android.logistic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Model.JobOrder.JobOrderData;
import com.huang.android.logistic.Model.Location.Location;
import com.huang.android.logistic.Model.MyCookieJar;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by David Wibisono on 8/24/2017.
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
            view=inflater.inflate(R.layout.fragment_order_pending_list,parent,false);
        }

        TextView principle = (TextView)view.findViewById(R.id.principle);
        TextView stopLocationCounter = (TextView)view.findViewById(R.id.stop_location_count);
        TextView joid = (TextView)view.findViewById(R.id.joid);
        TextView origin = (TextView) view.findViewById(R.id.origin);
        TextView destination = (TextView)view.findViewById(R.id.destination);
        TextView ref = (TextView)view.findViewById(R.id.ref_id);

        JobOrderData jobOrder = list.get(position);
        Utility.utility.setTextView(principle,jobOrder.principle);

        final ImageView profileImage = (ImageView)view.findViewById(R.id.profile_image);

        if (jobOrder.principle_image.size() > 0) {
            String imageUrl = jobOrder.principle_image.get(0);
            MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(context);
            API api = Utility.utility.getAPIWithCookie(cookieJar);
            Call<ResponseBody> callImage = api.getImage(imageUrl);
            callImage.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            Bitmap bm = BitmapFactory.decodeStream(response.body().byteStream());
                            profileImage.setImageBitmap(bm);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }

        Utility.utility.setTextView(ref,"Ref No : " + jobOrder.ref.replace("\n",""));

        int lastIndex = jobOrder.routes.size()-1;
        Utility.utility.setTextView(origin,Utility.utility.simpleFormatLocation(new Location(jobOrder.routes.get(0).distributor_code,jobOrder.routes.get(0).location,jobOrder.routes.get(0).city,jobOrder.routes.get(0).address,jobOrder.routes.get(0).warehouse_name,"","")));
        Utility.utility.setTextView(destination,Utility.utility.simpleFormatLocation(new Location(jobOrder.routes.get(lastIndex).distributor_code,jobOrder.routes.get(lastIndex).location,jobOrder.routes.get(lastIndex).city,jobOrder.routes.get(lastIndex).address,jobOrder.routes.get(lastIndex).warehouse_name,"","")));

        ImageView pickUpOrigin = (ImageView)view.findViewById(R.id.pickup_origin_icon);
        ImageView dropOrigin = (ImageView)view.findViewById(R.id.drop_origin_icon);
        if (jobOrder.routes.get(0).type.equals("Pick Up")) {
            pickUpOrigin.setVisibility(View.VISIBLE);
        } else {
            dropOrigin.setVisibility(View.VISIBLE);
        }

        ImageView pickUpDestination = (ImageView)view.findViewById(R.id.pickup_destination_icon);
        ImageView dropDestination = (ImageView)view.findViewById(R.id.drop_destination_icon);
        if (jobOrder.routes.get(lastIndex).type.equals("Pick Up")) {
            pickUpDestination.setVisibility(View.VISIBLE);
        } else {
            dropDestination.setVisibility(View.VISIBLE);
        }

        if (jobOrder.routes.size() > 2)
            Utility.utility.setTextView(stopLocationCounter, (jobOrder.routes.size() - 2) + " " + getContext().getString(R.string.stop_location));
        else
            Utility.utility.setTextView(stopLocationCounter, "");
        Utility.utility.setTextView(joid,jobOrder.joid);

        return view;
    }
}
