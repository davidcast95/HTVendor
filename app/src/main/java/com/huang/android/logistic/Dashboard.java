package com.huang.android.logistic;

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

import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Lihat_Pesanan.Pending.DetailOrderPending;
import com.huang.android.logistic.Model.JobOrder.GetJobOrderResponse;
import com.huang.android.logistic.Model.JobOrder.JobOrderData;
import com.huang.android.logistic.Model.JobOrder.JobOrderResponse;
import com.huang.android.logistic.Model.JobOrder.JobOrderStatus;
import com.huang.android.logistic.Model.JobOrderRoute.JobOrderRouteResponse;
import com.huang.android.logistic.Model.MyCookieJar;
import com.paging.listview.PagingListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class Dashboard extends Fragment implements PagingListView.Pagingable {
    View v;
    PagingListView lv;
    TextView noData;
    PendingOrderAdapter pendingOrderAdapter;
    ProgressBar loading;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public static List<JobOrderData> jobOrders = new ArrayList<>();
    int pager =0, limit=20;
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
        lv= (PagingListView) v.findViewById(R.id.pendingListView);
        lv.setVisibility(View.INVISIBLE);
        loading=(ProgressBar)v.findViewById(R.id.loading);
        mSwipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        pendingOrderAdapter = new PendingOrderAdapter(v.getContext(), R.layout.fragment_order_aktif_list, jobOrders);
        lv.setOnItemClickListener(onListClick);
        lv.setAdapter(pendingOrderAdapter);
        pendingOrderAdapter.clear();
        lv.setPagingableListener(this);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getPendingOrder();
    }

    public void getLanguage(){
        SharedPreferences prefs = this.getActivity().getSharedPreferences("LanguageSwitch", Context.MODE_PRIVATE);
        String language = prefs.getString("language","Basaha Indonesia");

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
            startActivityForResult(goDetail, 100);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getPendingOrder();
    }

    //API
    void getPendingOrder() {
        noData.setVisibility(View.GONE);
        lv.setVisibility(View.GONE);
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this.getActivity());
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String vendorName = Utility.utility.getLoggedName(getActivity());
        Call<GetJobOrderResponse> callJO = api.getJobOrder(JobOrderStatus.WAITING_FOR_APPROVAL,vendorName,"%", "" + (pager++ * limit));
        callJO.enqueue(new Callback<GetJobOrderResponse>() {
            @Override
            public void onResponse(Call<GetJobOrderResponse> call, Response<GetJobOrderResponse> response) {
                if (getActivity().getApplicationContext() != null) {
                    if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response, "")) {
                        GetJobOrderResponse jobOrderResponse = response.body();
                        if (jobOrderResponse.jobOrders != null) {
                            pendingOrderAdapter.addAll(jobOrderResponse.jobOrders);
                            lv.onFinishLoading(true, jobOrderResponse.jobOrders);
                        } else {
                            lv.onFinishLoading(false, null);
                        }
                        if (jobOrders.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                        }
                        else {
                            lv.setVisibility(View.VISIBLE);
                        }
                        loading.setVisibility(View.GONE);
                        onItemsLoadComplete();
                    }
                }

            }

            @Override
            public void onFailure(Call<GetJobOrderResponse> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getActivity().getApplicationContext());
                loading.setVisibility(View.GONE);
            }
        });
    }



    void refreshItems() {
        pager = 0;
        pendingOrderAdapter.clear();
        getPendingOrder();
    }

    void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoadMoreItems() {
        getPendingOrder();
    }
}
