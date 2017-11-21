package com.huang.android.logistic.Truck;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Model.Driver.DriverStatus;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.Model.Truck.Truck;
import com.huang.android.logistic.Model.Truck.TruckResponse;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseTruck extends AppCompatActivity {
    ProgressBar loading;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ChooseTruckAdapter chooseTruckAdapter;
    GridView gridView;
    String driver, driver_name, driver_phone, joid, from, expected_truck, status, nama_vendor_cp, telp_vendor_cp;
    int strict;
    List<Truck> trucks;
    Truck chosenTruck;
    TextView expectedTruck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_truck);
        setTitle("Choose Truck");

        gridView = (GridView) findViewById(R.id.gridView);
        expectedTruck = (TextView)findViewById(R.id.expected_truck);
        gridView.setVisibility(View.INVISIBLE);
        loading=(ProgressBar)findViewById(R.id.loading);
        mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });
        Intent intent = getIntent();
        joid = intent.getStringExtra("joid");
        driver = intent.getStringExtra("driver");
        driver_name = intent.getStringExtra("driver_name");
        driver_phone = intent.getStringExtra("driver_phone");
        from = getIntent().getStringExtra("from");

        expected_truck = getIntent().getStringExtra("expected_truck");
        strict = getIntent().getIntExtra("strict",0);
        expectedTruck.setText("Expected Truck Type : " + expected_truck + " ("+ (strict == 1 ? "strict" : "optional") +")");
        status = getIntent().getStringExtra("status");
        nama_vendor_cp = getIntent().getStringExtra("nama_vendor_cp");
        telp_vendor_cp = getIntent().getStringExtra("telp_vendor_cp");

        TextView nama=(TextView)findViewById(R.id.namasopir);
        nama.setText(driver_name);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getTruck();
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

    //API
    public void getTruck() {
        loading.setVisibility(View.VISIBLE);

        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String vendorName = Utility.utility.getLoggedName(this);
        String strictFilter = (strict == 1) ? ",[\"Truck\",\"type\",\"=\",\""+expected_truck+"\"]" : "";
        Call<TruckResponse> truckResponseCall = api.getTruck("[[\"Truck\",\"status\",\"=\",\"Operate\"],[\"Truck\",\"vendor\",\"=\",\""+vendorName+"\"]"+strictFilter+"]");
        truckResponseCall.enqueue(new Callback<TruckResponse>() {
            @Override
            public void onResponse(Call<TruckResponse> call, Response<TruckResponse> response) {
                loading.setVisibility(View.GONE);
                onItemsLoadComplete();
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    trucks = response.body().trucks;
                    chooseTruckAdapter = new ChooseTruckAdapter(getApplicationContext(),R.layout.activity_pilih_truck_list,trucks);
                    gridView.setAdapter(chooseTruckAdapter);
                    gridView.setVisibility(View.VISIBLE);
                    gridView.setOnItemClickListener(onListClick);
                }
            }

            @Override
            public void onFailure(Call<TruckResponse> call, Throwable t) {
                onItemsLoadComplete();
                loading.setVisibility(View.GONE);
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });
    }

    public void dialog(int idi) {
        TextView dummy =(TextView)findViewById(R.id.dummy);
        dummy.setText("Driver : ");
        String m1= dummy.getText().toString();
        String m2= driver_name;
        dummy.setText("Truck : ");
        String m3= dummy.getText().toString();
        String m4 = trucks.get(idi).nopol;
        String message= m1+" "+m2+" & "+m3+" "+m4;
        Log.e("MESSAGE",message);
        new AlertDialog.Builder(this)
                .setTitle("Are you sure ?")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       assignTruck();
                        loading.setVisibility(View.VISIBLE);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent,
                                View view,
                                int position, long id){
            String ids=String.valueOf(id);
            chosenTruck = trucks.get(position);
            int idi=Integer.parseInt(ids);
            dialog(idi);

        }
    };

    void refreshItems() {
        // Load items
        //loading.setVisibility(View.VISIBLE);
        getTruck();

        // Load complete

    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }


    void assignTruck() {
        loading.setVisibility(View.VISIBLE);
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        HashMap<String,String> statusJSON = new HashMap<>();
        String name = Utility.utility.getLoggedName(this);
        statusJSON.put("status",status);
        statusJSON.put("vendor_contact_person", nama_vendor_cp + " ("+name+")");
        statusJSON.put("nama_vendor_cp", nama_vendor_cp);
        statusJSON.put("telp_vendor_cp",telp_vendor_cp);
        Date today = new Date();
        statusJSON.put("accept_date",Utility.utility.dateToFormatDatabase(today));
        statusJSON.put("driver",driver);
        statusJSON.put("driver_nama",driver_name);
        statusJSON.put("driver_phone",driver_phone);
        statusJSON.put("truck",chosenTruck.name);
        statusJSON.put("truck_type",chosenTruck.type);
        statusJSON.put("truck_volume",chosenTruck.volume);
        statusJSON.put("truck_lambung",chosenTruck.lambung);

        String a = new Gson().toJson(statusJSON);
        Call<JSONObject> callUpdateJO = api.updateJobOrder(joid, statusJSON);
        callUpdateJO.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                loading.setVisibility(View.GONE);
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    Toast.makeText(getApplicationContext(), "Job order : " +joid + " has been assigned to " + driver + " with Truck : " + chosenTruck.nopol, Toast.LENGTH_LONG).show();
                    assignDriver();
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
                loading.setVisibility(View.GONE);
            }
        });
    }

    void assignDriver() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        HashMap<String,String> statusJSON = new HashMap<>();
        statusJSON.put("status", DriverStatus.UNAVAILABLE);
        String a = new Gson().toJson(statusJSON);
        Call<JSONObject> callUpdateJO = api.updateDriver(driver, statusJSON);
        callUpdateJO.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {

                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });
    }

}
