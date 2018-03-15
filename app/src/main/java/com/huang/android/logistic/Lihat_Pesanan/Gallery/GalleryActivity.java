package com.huang.android.logistic.Lihat_Pesanan.Gallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Model.JobOrderUpdateImage.JobOrderUpdateImageData;
import com.huang.android.logistic.Model.JobOrderUpdateImage.JobOrderUpdateImageResponse;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by davidwibisono on 2/24/18.
 */

public class GalleryActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    List<String> imageLinks = new ArrayList<>();
    ViewPager viewPager;
    ProgressBar loading;
    TextView noImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        setTitle(R.string.jo_update_image);
        loading = (ProgressBar)findViewById(R.id.loading);
        noImage = (TextView)findViewById(R.id.no_image);
        String jodName = getIntent().getStringExtra("jod_name");
        getJOUpdateImage(jodName);
    }

    void initViewPager() {
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        List<Fragment> listFragments =  new ArrayList<Fragment>();

        for (int i=0;i<imageLinks.size();i++) {
            GalleryPhotoDefaultFragment galleryPhotoDefaultFragment = new GalleryPhotoDefaultFragment();
            galleryPhotoDefaultFragment.setPhotoUrl(imageLinks.get(i));
            listFragments.add(galleryPhotoDefaultFragment);
        }

        GalleryAdapter viewJobOrderAdapter = new GalleryAdapter(getSupportFragmentManager(), listFragments);
        viewPager.setAdapter(viewJobOrderAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //API
    void getJOUpdateImage(String jod_name) {
        loading.setVisibility(View.VISIBLE);
        noImage.setVisibility(View.GONE);
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<JobOrderUpdateImageResponse> call = api.getJOUpdateImage(jod_name);
        call.enqueue(new Callback<JobOrderUpdateImageResponse>() {
            @Override
            public void onResponse(Call<JobOrderUpdateImageResponse> call, Response<JobOrderUpdateImageResponse> response) {
                loading.setVisibility(View.GONE);
                if (response.code() == 200) {
                    JobOrderUpdateImageResponse jobOrderUpdateImageResponse = response.body();
                    if (jobOrderUpdateImageResponse != null) {
                        if (jobOrderUpdateImageResponse.data.size() > 0) {
                            noImage.setVisibility(View.GONE);
                            for (JobOrderUpdateImageData joUpdateImage : jobOrderUpdateImageResponse.data) {
                                imageLinks.add(API.BASE_URL + joUpdateImage.file_url);
                            }

                            initViewPager();
                        } else {
                            noImage.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),getString(R.string.unable_to_load_image),Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JobOrderUpdateImageResponse> call, Throwable t) {

            }
        });
    }
}
