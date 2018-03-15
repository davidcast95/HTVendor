package com.huang.android.logistic.Lihat_Pesanan.Gallery;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by davidwibisono on 2/24/18.
 */

public class GalleryPhotoDefaultFragment extends Fragment {

    String imageLink = "";
    View v;
    ProgressBar loading;
    TextView unableImage;

    public GalleryPhotoDefaultFragment() {

    }

    public void setPhotoUrl(String photoUrl) {
        imageLink = photoUrl;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_gallery_photo_default, container, false);

        loading = (ProgressBar)v.findViewById(R.id.loading);
        unableImage = (TextView)v.findViewById(R.id.unable_image);
        unableImage.setVisibility(View.GONE);

        if (!imageLink.equals("")) {
            loading.setVisibility(View.VISIBLE);
            MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(getActivity());
            API api = Utility.utility.getAPIWithCookie(cookieJar);
            Call<ResponseBody> callImage = api.getImage(imageLink);
            callImage.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    loading.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            Bitmap bm = BitmapFactory.decodeStream(response.body().byteStream());
                            ImageView imageView = (ImageView)v.findViewById(R.id.photo_default);
                            imageView.setImageBitmap(bm);
                        }
                        unableImage.setVisibility(View.GONE);
                    } else {
                        unableImage.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

        }

        return v;
    }



}
