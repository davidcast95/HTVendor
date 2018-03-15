package com.huang.android.logistic.Lihat_Driver;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
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


import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Maps.LiveAllDriversMaps;
import com.huang.android.logistic.Model.Driver.Driver;
import com.huang.android.logistic.Model.Driver.DriverResponse;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;
import com.paging.listview.PagingListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class ViewDriver extends Fragment implements PagingListView.Pagingable {


    private MenuItem mSearchItem;
    private SearchView sv;
    View v;
    ProgressBar loading;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ViewDriverAdapter viewDriverAdapter;
    TextView noData;
    String lastQuery = "";


    public static List<Driver> drivers = new ArrayList<>();
    int pager = 0, limit =20;

    public ViewDriver() {
        // Required empty public constructor
    }

    PagingListView lv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utility.utility.getLanguage(this.getActivity());
        v = inflater.inflate(R.layout.fragment_view_driver, container, false);
        setHasOptionsMenu(true);

        lv=(PagingListView) v.findViewById(R.id.layout);
        noData = (TextView)v.findViewById(R.id.no_data);
        noData.setVisibility(View.GONE);

        lv.setVisibility(View.INVISIBLE);
        loading=(ProgressBar)v.findViewById(R.id.loading);
        mSwipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);
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
        viewDriverAdapter = new ViewDriverAdapter(getActivity(), R.layout.fragment_view_driver_list, drivers);
        lv.setAdapter(viewDriverAdapter);
        lv.setOnItemClickListener(onListClick);
        lv.setHasMoreItems(false);
        lv.setPagingableListener(this);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDriver();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_menu_view_driver, menu);
        mSearchItem = menu.findItem(R.id.action_search);
        sv = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        sv.setIconified(true);

        SearchManager searchManager = (SearchManager)  getActivity().getSystemService(Context.SEARCH_SERVICE);
        sv.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sv.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.e("search",query);
                searchQuery(query);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_maps:
                Intent intent1 = new Intent(this.getActivity(), LiveAllDriversMaps.class);
                startActivity(intent1);
                return true;
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
            getDriver();
        }
    }

    //API
    public void getDriver() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(getActivity());
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String vendorName = Utility.utility.getLoggedName(getActivity());
        Call<DriverResponse> callDriver = api.getDriver("[[\"Driver\",\"nama\",\"like\",\""+lastQuery+"%\"],[\"Driver\",\"vendor\",\"=\",\""+vendorName+"\"]]","" + (pager++ * limit));
        callDriver.enqueue(new Callback<DriverResponse>() {
            @Override
            public void onResponse(Call<DriverResponse> call, Response<DriverResponse> response) {
                if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response, "")) {
                    DriverResponse driverResponses = response.body();
                    viewDriverAdapter.addAll(driverResponses.drivers);
                    if (drivers.size() == 0) noData.setVisibility(View.VISIBLE);
                    else {
                        lv.setVisibility(View.VISIBLE);
                        noData.setVisibility(View.GONE);
                    }

                    if (driverResponses.drivers.size() == 0) lv.onFinishLoading(false,null);
                    else lv.onFinishLoading(true,driverResponses.drivers);
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
        pager=0;
        viewDriverAdapter.clear();
        getDriver();

        // Load complete

    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onLoadMoreItems() {
        getDriver();
    }

    void searchQuery(String query) {
        lastQuery = query;
        viewDriverAdapter.clear();
        pager = 0;
        getDriver();
    }
}
