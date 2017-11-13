package com.huang.android.logistic.Lihat_Driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Model.Driver.Driver;
import com.huang.android.logistic.Model.Driver.DriverResponse;
import com.huang.android.logistic.Model.Driver.DriverStatus;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Truck.ChooseTruck;
import com.huang.android.logistic.Utility;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseDriver extends AppCompatActivity {
    ProgressBar loading;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ViewDriverAdapter viewDriverAdapter;
    ListView lv;
    TextView noData;
    String joid,from, expected_truck, status, nama_vendor_cp, telp_vendor_cp;
    int strict;

    public static List<Driver> drivers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_sopir);
        setTitle("Assign Driver");

        joid = getIntent().getStringExtra("joid");
        from = getIntent().getStringExtra("from");
        expected_truck = getIntent().getStringExtra("expected_truck");
        status = getIntent().getStringExtra("status");
        nama_vendor_cp = getIntent().getStringExtra("nama_vendor_cp");
        telp_vendor_cp = getIntent().getStringExtra("telp_vendor_cp");
        strict = getIntent().getIntExtra("strict",0);


        lv=(ListView)findViewById(R.id.layout);
        noData = (TextView)findViewById(R.id.no_data);
        lv.setVisibility(View.INVISIBLE);
        noData.setVisibility(View.GONE);
        loading=(ProgressBar)findViewById(R.id.loading);
        mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDriver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 300 && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra("joid",joid);
            intent.putExtra("from",from);
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    //API
    public void getDriver() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String vendorName = Utility.utility.getLoggedName(this);
        final Activity thisActivity = this;
        Call<DriverResponse> callDriver = api.getDriver("[[\"Driver\",\"vendor\",\"=\",\""+vendorName+"\"],[\"Driver\",\"status\",\"=\",\""+ DriverStatus.AVAILABLE+"\"]]");
        callDriver.enqueue(new Callback<DriverResponse>() {
            @Override
            public void onResponse(Call<DriverResponse> call, Response<DriverResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    DriverResponse driverResponses = response.body();
                    drivers = driverResponses.drivers;
                    if (drivers.size() == 0) noData.setVisibility(View.VISIBLE);
                    else {
                        viewDriverAdapter = new ViewDriverAdapter(thisActivity, R.layout.fragment_view_driver_list, drivers);
                        lv.setAdapter(viewDriverAdapter);
                        lv.setOnItemClickListener(onListClick);

                        lv.setVisibility(View.VISIBLE);
                        noData.setVisibility(View.GONE);
                    }

                    loading.setVisibility(View.INVISIBLE);
                    onItemsLoadComplete();
                }
            }

            @Override
            public void onFailure(Call<DriverResponse> call, Throwable throwable) {
                String error = throwable.getLocalizedMessage();
            }
        });
    }
    Driver choosenDriver;
    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent,
                                View view,
                                int position, long id){

            choosenDriver = drivers.get(position);
            assignDriver();
        }
    };

    void refreshItems() {
        getDriver();
    }

    void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                Intent intent = new Intent();
                intent.putExtra("from",from);
                setResult(RESULT_OK,intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void assignDriver() {
        Intent intent = new Intent(getApplicationContext(),ChooseTruck.class);
        intent.putExtra("joid",joid);
        intent.putExtra("driver",choosenDriver.email);
        intent.putExtra("from",from);

        intent.putExtra("expected_truck",expected_truck);
        intent.putExtra("status",status);
        intent.putExtra("nama_vendor_cp",nama_vendor_cp);
        intent.putExtra("telp_vendor_cp",telp_vendor_cp);
        intent.putExtra("strict",strict);
        startActivityForResult(intent, 300);
    }
}