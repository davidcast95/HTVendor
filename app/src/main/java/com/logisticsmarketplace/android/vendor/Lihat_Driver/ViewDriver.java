package com.logisticsmarketplace.android.vendor.Lihat_Driver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.logisticsmarketplace.android.vendor.API.API;
import com.logisticsmarketplace.android.vendor.Model.Driver.Driver;
import com.logisticsmarketplace.android.vendor.Model.Driver.DriverResponse;
import com.logisticsmarketplace.android.vendor.Model.MyCookieJar;
import com.logisticsmarketplace.android.vendor.R;
import com.logisticsmarketplace.android.vendor.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class ViewDriver extends Fragment {
    View v;
    ProgressBar loading;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ViewDriverAdapter viewDriverAdapter;
    TextView noData;


    public static List<Driver> drivers;

    public ViewDriver() {
        // Required empty public constructor
    }

    ListView lv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utility.utility.getLanguage(this.getActivity());
        v = inflater.inflate(R.layout.fragment_view_driver, container, false);
        setHasOptionsMenu(true);

        lv=(ListView)v.findViewById(R.id.layout);
        noData = (TextView)v.findViewById(R.id.no_data);
        noData.setVisibility(View.GONE);

        lv.setVisibility(View.INVISIBLE);
        loading=(ProgressBar)v.findViewById(R.id.loading);
        mSwipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);
        getSopir();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

//        LinearLayout button = (LinearLayout) v.findViewById(R.id.sopir1);
//        button.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view) {
//                Intent detailsopir= new Intent(getActivity().getApplicationContext(),DetailDriver.class);
//                startActivity(detailsopir);
//            }
//        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_menu_view_driver, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(this.getActivity(), RegisterDriver.class);
                startActivityForResult(intent, 200);
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200 && resultCode == RESULT_OK) {
            getSopir();
        }
    }

    //API
    public void getSopir() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(getActivity());
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String vendorName = Utility.utility.getLoggedName(getActivity());
        Call<DriverResponse> callDriver = api.getDriver("[[\"Driver\",\"vendor\",\"=\",\""+vendorName+"\"]]");
        callDriver.enqueue(new Callback<DriverResponse>() {
            @Override
            public void onResponse(Call<DriverResponse> call, Response<DriverResponse> response) {
                if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response)) {
                    DriverResponse driverResponses = response.body();
                    drivers = driverResponses.drivers;
                    if (drivers.size() == 0) noData.setVisibility(View.VISIBLE);
                    else {
                        viewDriverAdapter = new ViewDriverAdapter(getActivity(), R.layout.fragment_view_driver_list, drivers);
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
                Utility.utility.showConnectivityUnstable(getActivity().getApplicationContext());
                loading.setVisibility(View.INVISIBLE);
            }
        });
    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent,
                                View view,
                                int position, long id){
            Intent goDetail = new Intent(getActivity().getApplicationContext(),DetailDriver.class);
            goDetail.putExtra("index", position);
            startActivity(goDetail);
        }
    };

    void refreshItems() {
        // Load items
        //loading.setVisibility(View.VISIBLE);
        getSopir();

        // Load complete

    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }


}
