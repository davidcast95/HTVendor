package com.huang.android.logistic.Lihat_Pesanan.Done;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Lihat_Pesanan.ViewJobOrder;
import com.huang.android.logistic.Model.JobOrder.JobOrderData;
import com.huang.android.logistic.Model.JobOrder.JobOrderResponse;
import com.huang.android.logistic.Model.JobOrder.JobOrderStatus;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDone extends Fragment {
    View v;
    OrderDoneAdapter onOrderDoneAdapter;
    ListView lv;
    ProgressBar loading;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView noData;

    public static List<JobOrderData> jobOrders;

    public OrderDone() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_order_pending, container, false);

        lv=(ListView)v.findViewById(R.id.layout);
        loading=(ProgressBar)v.findViewById(R.id.loading);
        noData=(TextView) v.findViewById(R.id.nodata);
        noData.setVisibility(View.GONE);

        mSwipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDoneOrder();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            refreshItems();
        }
    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent,
                                View view,
                                int position, long id){
            Intent goDetail = new Intent(getActivity().getApplicationContext(),DetailOrderDone.class);
            goDetail.putExtra("index", position);
            goDetail.putExtra("from","OrderDone");
            startActivityForResult(goDetail,100);
        }
    };

    void refreshItems() {
        getDoneOrder();
        ViewJobOrder viewJobOrder = (ViewJobOrder)getParentFragment();
        viewJobOrder.getCount();
    }

    void onItemsLoadComplete() {

        mSwipeRefreshLayout.setRefreshing(false);
    }

    void getDoneOrder() {
        loading.setVisibility(View.VISIBLE);
        lv.setVisibility(View.GONE);
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this.getActivity());
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String vendorName = Utility.utility.getLoggedName(getActivity());
        String filters = "[[\"Job Order\",\"status\",\"=\",\""+ JobOrderStatus.DONE+"\"], [\"Job Order\",\"vendor\",\"=\",\"" + vendorName + "\"],[\"Job Order\",\"reference\",\"like\",\"%\"]]";
        Call<JobOrderResponse> callJO = api.getJobOrder(filters);
        callJO.enqueue(new Callback<JobOrderResponse>() {
            @Override
            public void onResponse(Call<JobOrderResponse> call, Response<JobOrderResponse> response) {
                Log.e("asd", response.message());
                loading.setVisibility(View.GONE);
                if (Utility.utility.catchResponse(getActivity().getApplicationContext(),response)) {
                    JobOrderResponse jobOrderResponse = response.body();
                    jobOrders = jobOrderResponse.jobOrders;
                    if (jobOrders.size() == 0) {
                        noData.setVisibility(View.VISIBLE);
                    } else {
                        noData.setVisibility(View.GONE);
                        OrderDoneAdapter onProgressOrderAdapter = new OrderDoneAdapter(v.getContext(), R.layout.fragment_order_pending_list, jobOrders);
                        lv.setOnItemClickListener(onListClick);
                        lv.setAdapter(onProgressOrderAdapter);
                        lv.setVisibility(View.VISIBLE);
                    }
                    onItemsLoadComplete();
                } else {
                    Utility.utility.showConnectivityUnstable(getActivity().getApplicationContext());
                }

            }

            @Override
            public void onFailure(Call<JobOrderResponse> call, Throwable t) {
                loading.setVisibility(View.GONE);
            }
        });
    }
}
