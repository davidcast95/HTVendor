package com.logisticsmarketplace.android.vendor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.logisticsmarketplace.android.vendor.API.API;
import com.logisticsmarketplace.android.vendor.Lihat_Pesanan.Pending.DetailOrderPending;
import com.logisticsmarketplace.android.vendor.Model.JobOrder.JobOrderData;
import com.logisticsmarketplace.android.vendor.Model.JobOrder.JobOrderResponse;
import com.logisticsmarketplace.android.vendor.Model.MyCookieJar;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Dashboard extends Fragment {
    View v;
    ListView lv;
    TextView noData;
    PendingOrderAdapter pendingOrderAdapter;
    DashboardAdapter dashboardAdapter;
    ProgressBar loading;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public static List<JobOrderData> jobOrders;

    public Dashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getLanguage();
        v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        noData = (TextView) v.findViewById(R.id.no_data);
        lv= (ListView) v.findViewById(R.id.pendingListView);
        lv.setVisibility(View.INVISIBLE);
        loading=(ProgressBar)v.findViewById(R.id.loading);
        mSwipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);
        getOnProgressOrder();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        return v;
    }

    public void getLanguage(){
        SharedPreferences prefs = this.getActivity().getSharedPreferences("LanguageSwitch", Context.MODE_PRIVATE);
        String language = prefs.getString("language","English");

        if(language.contentEquals("English")){
            setLocal("en");
        }
        else {
            setLocal("in");
        }
    }
    private void setLocal(String language) {
        Locale myLocale;
        myLocale = new Locale(language);
        Resources res = getResources();
        DisplayMetrics dm= res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf,dm);
    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent,
                                View view,
                                int position, long id){
            Intent goDetail = new Intent(getActivity().getApplicationContext(),DetailOrderPending.class);
            goDetail.putExtra("index",position);
            goDetail.putExtra("from","Dashboard");
            startActivity(goDetail);
        }
    };

    //API
    void getOnProgressOrder() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this.getActivity());
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String vendorName = Utility.utility.getLoggedName(getActivity());
        Call<JobOrderResponse> callJO = api.getJobOrder("[[\"Job Order\",\"status\",\"=\",\"Menunggu Persetujuan Vendor\"], [\"Job Order\",\"vendor\",\"=\",\"" + vendorName + "\"]]");
        callJO.enqueue(new Callback<JobOrderResponse>() {
            @Override
            public void onResponse(Call<JobOrderResponse> call, Response<JobOrderResponse> response) {
                if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response)) {
                    JobOrderResponse jobOrderResponse = response.body();
                    jobOrders = jobOrderResponse.jobOrders;
                    if (jobOrders.size() == 0) noData.setVisibility(View.VISIBLE);
                    else {
                        PendingOrderAdapter pendingOrderAdapter = new PendingOrderAdapter(v.getContext(), R.layout.fragment_order_on_progress_list, jobOrders);
                        lv.setOnItemClickListener(onListClick);
                        lv.setAdapter(pendingOrderAdapter);
                        lv.setVisibility(View.VISIBLE);
                    }
                    loading.setVisibility(View.GONE);
                    onItemsLoadComplete();
                }

            }

            @Override
            public void onFailure(Call<JobOrderResponse> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getActivity().getApplicationContext());
                loading.setVisibility(View.GONE);
            }
        });
    }



    void refreshItems() {
        getOnProgressOrder();
    }

    void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

}
