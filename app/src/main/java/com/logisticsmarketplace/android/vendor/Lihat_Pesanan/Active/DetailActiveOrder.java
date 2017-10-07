package com.logisticsmarketplace.android.vendor.Lihat_Pesanan.Active;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.logisticsmarketplace.android.vendor.API.API;
import com.logisticsmarketplace.android.vendor.Dashboard;
import com.logisticsmarketplace.android.vendor.Lihat_Pesanan.CheckPoint;
import com.logisticsmarketplace.android.vendor.Lihat_Pesanan.TrackHistory;
import com.logisticsmarketplace.android.vendor.Maps.TrackOrderMaps;
import com.logisticsmarketplace.android.vendor.Model.JobOrder.JobOrderData;
import com.logisticsmarketplace.android.vendor.Model.JobOrderUpdate.JobOrderUpdateData;
import com.logisticsmarketplace.android.vendor.Model.JobOrderUpdate.JobOrderUpdateResponse;
import com.logisticsmarketplace.android.vendor.Model.MyCookieJar;
import com.logisticsmarketplace.android.vendor.R;
import com.logisticsmarketplace.android.vendor.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActiveOrder extends AppCompatActivity {
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout layout, last_status;
    TextView statusTimeTV,statusTV,notesTV;

    public static JobOrderData jobOrder;
    public static List<JobOrderUpdateData> jobOrderUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order_aktif);

        setTitle(R.string.track_order_list);

        layout=(LinearLayout)findViewById(R.id.layout);
        last_status=(LinearLayout)findViewById(R.id.last_status);
        statusTimeTV=(TextView)findViewById(R.id.status_time);
        statusTV=(TextView)findViewById(R.id.status);
        notesTV = (TextView)findViewById(R.id.notes);


        Intent intent = getIntent();
        int index = intent.getIntExtra("index", 0);
        String from = intent.getStringExtra("from");
        if (from.equals("View Order")) {
            if (OrderActive.jobOrders.get(index) != null) {
                jobOrder = OrderActive.jobOrders.get(index);
                TextView joid = (TextView) findViewById(R.id.joid);
                TextView origin = (TextView) findViewById(R.id.origin);
                TextView destination = (TextView) findViewById(R.id.destination);
                TextView vendor_name = (TextView) findViewById(R.id.vendor_name);
                TextView vendor_cp_name = (TextView) findViewById(R.id.vendor_cp_name);
                TextView vendor_cp_phone = (TextView) findViewById(R.id.vendor_cp_phone);
                TextView principle_cp_name = (TextView) findViewById(R.id.principle_cp_name);
                TextView principle_cp_phone = (TextView) findViewById(R.id.principle_cp_phone);
                TextView cargoInfo = (TextView) findViewById(R.id.cargo_info);
                TextView epd = (TextView) findViewById(R.id.pick_date);
                TextView edd = (TextView) findViewById(R.id.delivered_date);

                joid.setText(jobOrder.joid);
                origin.setText(jobOrder.origin);
                destination.setText(jobOrder.destination);
                vendor_name.setText(jobOrder.vendor);
                vendor_cp_name.setText(jobOrder.vendor_cp_name);
                vendor_cp_phone.setText(jobOrder.vendor_cp_phone);
                principle_cp_name.setText(jobOrder.principle_cp_name);
                principle_cp_phone.setText(jobOrder.principle_cp_phone);
                cargoInfo.setText(jobOrder.cargoInfo);
                epd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.etd));
                edd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.eta));
            }
        } else {
            if (Dashboard.jobOrders.get(index) != null) {
                jobOrder = Dashboard.jobOrders.get(index);
                TextView joid = (TextView) findViewById(R.id.joid);
                TextView origin = (TextView) findViewById(R.id.origin);
                TextView destination = (TextView) findViewById(R.id.destination);
                TextView vendor_name = (TextView) findViewById(R.id.vendor_name);
                TextView vendor_cp_name = (TextView) findViewById(R.id.vendor_cp_name);
                TextView vendor_cp_phone = (TextView) findViewById(R.id.vendor_cp_phone);
                TextView principle_cp_name = (TextView) findViewById(R.id.principle_cp_name);
                TextView principle_cp_phone = (TextView) findViewById(R.id.principle_cp_phone);
                TextView cargoInfo = (TextView) findViewById(R.id.cargo_info);
                TextView epd = (TextView) findViewById(R.id.pick_date);
                TextView edd = (TextView) findViewById(R.id.delivered_date);

                joid.setText(jobOrder.joid);
                origin.setText(jobOrder.origin);
                destination.setText(jobOrder.destination);
                vendor_name.setText(jobOrder.vendor);
                vendor_cp_name.setText(jobOrder.vendor_cp_name);
                vendor_cp_phone.setText(jobOrder.vendor_cp_phone);
                principle_cp_name.setText(jobOrder.principle_cp_name);
                principle_cp_phone.setText(jobOrder.principle_cp_phone);
                cargoInfo.setText(jobOrder.cargoInfo);
                epd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.etd));
                edd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.eta));
            }
        }

        getLastUpdate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.track_titlebar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_history:
                Intent intent = new Intent(this, TrackHistory.class);
                intent.putExtra("joid",jobOrder.joid);
                intent.putExtra("origin",jobOrder.origin);
                intent.putExtra("destination",jobOrder.destination);
                startActivity(intent);
                break;
            case R.id.action_cekpoint:
                Intent intent3 = new Intent(this, CheckPoint.class);
                intent3.putExtra("joid",jobOrder.joid);
                intent3.putExtra("principle",jobOrder.principle);
                startActivityForResult(intent3, 200);
                break;
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public void cekpoin(View v){
        Intent maps = new Intent(getApplicationContext(), CheckPoint.class);
        startActivity(maps);
    }
    public void problem(View v){
        Intent maps = new Intent(getApplicationContext(), Problem.class);
        startActivity(maps);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                getLastUpdate();
            }
        }
    }

    //API
    void getLastUpdate() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<JobOrderUpdateResponse> callGetJOUpdate = api.getJOUpdate("[[\"Job Order Update\",\"job_order\",\"=\",\""+jobOrder.joid+"\"]]");
        callGetJOUpdate.enqueue(new Callback<JobOrderUpdateResponse>() {
            @Override
            public void onResponse(Call<JobOrderUpdateResponse> call, Response<JobOrderUpdateResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    JobOrderUpdateResponse jobOrderUpdateResponse = response.body();
                    jobOrderUpdates = jobOrderUpdateResponse.jobOrderUpdates;
                    if (jobOrderUpdates.size() > 0) {
                        final JobOrderUpdateData lastJOStatus = jobOrderUpdates.get(0);
                        last_status.setVisibility(View.VISIBLE);
                        statusTimeTV.setText(lastJOStatus.time);
                        statusTV.setText(lastJOStatus.status);
                        notesTV.setText(lastJOStatus.note);
                        ImageView button = (ImageView)findViewById(R.id.detail_active_last_map);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Double latitude = Double.valueOf(lastJOStatus.latitude), longitude = Double.valueOf(lastJOStatus.longitude);
                                if (latitude!= null || longitude!=null){
                                    Intent maps = new Intent(getApplicationContext(), TrackOrderMaps.class);
                                    maps.putExtra("longitude", longitude );
                                    maps.putExtra("latitude", latitude );
                                    Log.e("LAT",latitude+"");
                                    Log.e("LONG",longitude+"");
                                    maps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(maps);
                                }
                                else {
                                    Context c = getApplicationContext();
                                    Toast.makeText(c,R.string.nla, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {
                        last_status.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<JobOrderUpdateResponse> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });
    }
}
