package com.huang.android.logistic.Lihat_Driver;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Model.Driver.Driver;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

//        TextView ID = (TextView)view.findViewById(R.name.id_supir);
        TextView Nama = (TextView)view.findViewById(R.id.nama);

        Driver driver = list.get(position);
//        ID.setText(list.get(position).name);
        Utility.utility.setTextView(Nama,driver.nama);

        final RoundedImageView profileImage = (RoundedImageView) view.findViewById(R.id.profile_image);

        String imageUrl = driver.profile_image.get(0);
        if (imageUrl != null) {
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
}
