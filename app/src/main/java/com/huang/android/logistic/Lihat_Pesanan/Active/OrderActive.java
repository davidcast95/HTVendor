package com.huang.android.logistic.Lihat_Pesanan.Active;

import android.app.Activity;
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
import com.huang.android.logistic.Model.JobOrder.GetJobOrderResponse;
import com.huang.android.logistic.Model.JobOrder.JobOrderData;
import com.huang.android.logistic.Model.JobOrder.JobOrderResponse;
import com.huang.android.logistic.Model.JobOrder.JobOrderStatus;
import com.huang.android.logistic.Model.JobOrderRoute.JobOrderRouteResponse;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;
import com.paging.listview.PagingListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;


public class OrderActive extends Fragment implements PagingListView.Pagingable {
    View v;
    PagingListView lv;
    ProgressBar loading;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView noData;
    String lastQuery = "";

    public static List<JobOrderData> jobOrders = new ArrayList<>();
    int pager = 0, limit=20;
    OrderActiveAdapter orderActiveAdapter;

    public OrderActive() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_order_aktif, container, false);

        lv=(PagingListView) v.findViewById(R.id.layout);
        loading=(ProgressBar)v.findViewById(R.id.loading);
        noData = (TextView)v.findViewById(R.id.nodata);
        noData.setVisibility(View.GONE);

        mSwipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        orderActiveAdapter = new OrderActiveAdapter(v.getContext(), R.layout.fragment_order_aktif_list, jobOrders);
        lv.setOnItemClickListener(onListClick);
        lv.setAdapter(orderActiveAdapter);
        lv.setHasMoreItems(false);
        lv.setPagingableListener(this);
        getActiveOrder();

        return v;
    }


    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent,
                                View view,
                                int position, long id){
            Intent goDetail = new Intent(getActivity().getApplicationContext(),DetailActiveOrder.class);
            goDetail.putExtra("index",position);
            goDetail.putExtra("from","OrderActive");
            startActivity(goDetail);
        }
    };

    void refreshItems() {
        pager=0;
        orderActiveAdapter.clear();
        getActiveOrder();
        ViewJobOrder viewJobOrder = (ViewJobOrder)getParentFragment();
        viewJobOrder.getCount();
    }

    void onItemsLoadComplete() {

        mSwipeRefreshLayout.setRefreshing(false);
    }

    void getActiveOrder() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this.getActivity());
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String vendorName = Utility.utility.getLoggedName(getActivity());
        Call<GetJobOrderResponse> callJO = api.getJobOrder(JobOrderStatus.ON_PROGRESS, vendorName,lastQuery + "%", "" + (pager++ * limit));
        callJO.enqueue(new Callback<GetJobOrderResponse>() {
            @Override
            public void onResponse(Call<GetJobOrderResponse> call, Response<GetJobOrderResponse> response) {
                Log.e("asd",response.message());
                if (response.message().equals("OK")) {
                    GetJobOrderResponse jobOrderResponse = response.body();
                    if (jobOrderResponse.jobOrders != null) {
                        orderActiveAdapter.addAll(jobOrderResponse.jobOrders);
                        lv.onFinishLoading(true, jobOrderResponse.jobOrders);

                    } else {
                        lv.onFinishLoading(false,null);
                    }

                    if (jobOrders.size() == 0) {
                        noData.setVisibility(View.VISIBLE);
                    }
                    else {
                        noData.setVisibility(View.GONE);
                    }

                    loading.setVisibility(View.GONE);
                    lv.setVisibility(View.VISIBLE);
                    onItemsLoadComplete();
                } else {
                    Utility.utility.showConnectivityUnstable(getActivity().getApplicationContext());
                }

            }

            @Override
            public void onFailure(Call<GetJobOrderResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            refreshItems();
        }
    }

    @Override
    public void onLoadMoreItems() {
        getActiveOrder();
    }

    public void searchJobOrder(String query) {
        loading.setVisibility(View.VISIBLE);
        lastQuery = query;
        pager = 0;
        orderActiveAdapter.clear();
        getActiveOrder();
    }
}
