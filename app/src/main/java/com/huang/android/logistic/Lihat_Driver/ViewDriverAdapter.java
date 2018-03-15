package com.huang.android.logistic.Lihat_Driver;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Model.Driver.Driver;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;
import com.makeramen.roundedimageview.RoundedImageView;

import org.w3c.dom.Text;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewDriverAdapter extends ArrayAdapter<Driver> {
    private Context context;
    private List<Driver> list;

    public ViewDriverAdapter(Context context, int layout, List<Driver> list) {
        super(context, layout,list);
        this.context=context;
        this.list=list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.fragment_view_driver_list,parent,false);
        }

//        TextView ID = (TextView)view.findViewById(R.id.id_supir);
        TextView Nama = (TextView)view.findViewById(R.id.nama);
        TextView status = (TextView)view.findViewById(R.id.driver_status);

        Driver driver = list.get(position);
//        ID.setText(list.get(position).id);
        Utility.utility.setTextView(Nama,driver.nama);
        Utility.utility.setTextView(status,driver.status);

        final RoundedImageView profileImage = (RoundedImageView) view.findViewById(R.id.profile_image);
        if (driver.profile_image.size() > 0) {
            String imageUrl = driver.profile_image.get(0);
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

        return view;
    }

    public boolean isExist(Driver driver) {
        for (int i=0;i<list.size();i++) {
            if (driver.name == list.get(i).name) return true;
        }
        return false;
    }
}
