package com.huang.android.logistic.Lihat_Driver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Maps.LiveMaps;
import com.huang.android.logistic.Model.Driver.Driver;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;
import com.makeramen.roundedimageview.RoundedImageView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailDriver extends AppCompatActivity {

    ProgressBar loading;
    LinearLayout layout;
    Driver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setTitle("Detail Driver");
        setContentView(R.layout.activity_detail_driver);

        loading=(ProgressBar)findViewById(R.id.loading);
        layout=(LinearLayout)findViewById(R.id.layout);
        layout.setVisibility(View.INVISIBLE);

        TextView nama = (TextView)findViewById(R.id.nama_supir);
        TextView phone = (TextView)findViewById(R.id.phone);
        TextView alamat = (TextView)findViewById(R.id.alamat);

        Intent intent = getIntent();
        int index = intent.getIntExtra("index",0);
        driver = ViewDriver.drivers.get(index);
        if (driver != null) {
            Utility.utility.setTextView(nama,driver.nama+"");
            Utility.utility.setTextView(phone,driver.phone+"");
            Utility.utility.setTextView(alamat,driver.address+"");

            final RoundedImageView profileImage = (RoundedImageView) findViewById(R.id.profile_image);
            if (driver.profile_image.size() > 0) {
                String imageUrl = driver.profile_image.get(0);
                MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(getApplicationContext());
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

        }



        loading.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_track_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_maps:
                Intent intent = new Intent(this, LiveMaps.class);
                intent.putExtra("driver",driver.name);
                intent.putExtra("driver_name",driver.nama);
                startActivity(intent);
                return true;
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
