package com.logisticsmarketplace.android.vendor.Lihat_Pesanan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.logisticsmarketplace.android.vendor.Maps.TrackOrderMaps;
import com.logisticsmarketplace.android.vendor.Model.JobOrder.JobOrderResponse;
import com.logisticsmarketplace.android.vendor.R;
import com.logisticsmarketplace.android.vendor.API.API;
import com.logisticsmarketplace.android.vendor.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrackOrderList extends AppCompatActivity {

    ProgressBar loading;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order_list);

        setTitle(R.string.track_order_list);

        loading=(ProgressBar)findViewById(R.id.loading);
        layout=(LinearLayout)findViewById(R.id.layout);
        layout.setVisibility(View.INVISIBLE);
        getDetailJO();

        ImageButton button = (ImageButton)findViewById(R.id.maps);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latitude!= null || longitude!=null){
                    Intent maps = new Intent(getApplicationContext(), TrackOrderMaps.class);
                    maps.putExtra("longitude", longitude );
                    maps.putExtra("latitude", latitude );
                    startActivity(maps);
                }
                else {
                    Context c = getApplicationContext();
                    Toast.makeText(c,R.string.nla, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trackorderlist_titlebar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_track_order:
                Intent intent = new Intent(this, TrackHistory.class);
                intent.putExtra("joid",JOID);
                intent.putExtra("date",tanggal4);
                intent.putExtra("to",to);
                intent.putExtra("from",from);
                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    String latitude;
    String longitude;
    String JOID,tanggal4,to,from;

    String type ="dr";
    String typeid ="Tonon";
    //API
    public void getDetailJO() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        Call<JobOrderResponse> callJO = api.getJobOrderList(type,typeid,"");
        callJO.enqueue(new Callback<JobOrderResponse>() {
            @Override
            public void onResponse(Call<JobOrderResponse> call, Response<JobOrderResponse> response) {
                JobOrderResponse jobOrderResponse = response.body();
            }

            @Override
            public void onFailure(Call<JobOrderResponse> call, Throwable throwable) {
                String error = throwable.getLocalizedMessage();
            }
        });
    }
}
