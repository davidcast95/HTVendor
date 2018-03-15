package com.huang.android.logistic.Lihat_Pesanan;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Lihat_Pesanan.Active.OrderActive;
import com.huang.android.logistic.Lihat_Pesanan.Done.OrderDone;
import com.huang.android.logistic.Lihat_Pesanan.Pending.OrderPending;
import com.huang.android.logistic.Model.JobOrder.JobOrderMetaDataResponse;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewJobOrder extends Fragment implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener{


    private MenuItem mSearchItem;
    private SearchView sv;
    ViewPager viewPager;
    TabHost tabHost;
    int onprogress = 0,done = 0, pending = 0;
    int p_onprogress = 0, p_done = 0, p_pending = 0, limit = 20;


    public OrderActive orderActive = new OrderActive();
    public OrderPending orderPending = new OrderPending();
    public OrderDone orderDone = new OrderDone();
    View v;
    public ViewJobOrder() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_view_order, container, false);

        initViewPager();
        initTabHost();
        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getCount();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        mSearchItem = menu.findItem(R.id.search);
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
                int selectedItem = tabHost.getCurrentTab();
                if (selectedItem == 0) {
                    orderPending.searchJobOrder(query);
                } else if (selectedItem == 1) {
                    orderActive.searchJobOrder(query);
                } else {
                    orderDone.searchJobOrder(query);
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);

    }

    private void initTabHost() {
        tabHost=(TabHost)v.findViewById(R.id.tabhost);
        tabHost.setup();

        SharedPreferences prefs = this.getActivity().getSharedPreferences("LanguageSwitch", Context.MODE_PRIVATE);
        String language = prefs.getString("language","Basaha Indonesia");
        String[] tabs = new String[3];
        if(language.contentEquals("English")){
            tabs[0]="Pending";
            tabs[1]="Active";
            tabs[2]="Done";
        }
        else {
            tabs[0]="Pending";
            tabs[1]="Aktif";
            tabs[2]="Selesai";
        }

        for(int i=0;i<3;i++){
            TabHost.TabSpec tabSpec;
            tabSpec = tabHost.newTabSpec(tabs[i]);
            tabSpec.setIndicator(tabs[i]);
            tabSpec.setContent(new FakeContent(getActivity().getApplicationContext()));
            tabHost.addTab(tabSpec);
        }

        tabHost.setOnTabChangedListener(this);
    }

    public class FakeContent implements TabHost.TabContentFactory{
        Context context;
        public FakeContent(Context mcontext){
            context=mcontext;
        }
        @Override
        public View createTabContent(String s) {
            View fakeView = new View(context);
            fakeView.setMinimumHeight(0);
            fakeView.setMinimumHeight(0);
            return fakeView;
        }
    }

    private void initViewPager() {
        viewPager = (ViewPager)v.findViewById(R.id.view_pager);
        List<Fragment> listFragments =  new ArrayList<Fragment>();
        listFragments.add(orderPending);
        listFragments.add(orderActive);
        listFragments.add(orderDone);

        ViewJobOrderPagerAdapter viewJobOrderAdapter = new ViewJobOrderPagerAdapter(getChildFragmentManager(), listFragments);
        viewPager.setAdapter(viewJobOrderAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int selectedItem) {
        tabHost.setCurrentTab(selectedItem);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabChanged(String s) {
        int selectedItem = tabHost.getCurrentTab();
        Log.e("asd",viewPager.toString());
        viewPager.setCurrentItem(selectedItem);
    }

    void getJOMetaData() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this.getActivity());
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String vendorName = Utility.utility.getLoggedName(getActivity());
        Call<JobOrderMetaDataResponse> callJO = api.getJobOrderCount(vendorName);
        callJO.enqueue(new Callback<JobOrderMetaDataResponse>() {
            @Override
            public void onResponse(Call<JobOrderMetaDataResponse> call, Response<JobOrderMetaDataResponse> response) {
                if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response, "")) {
                    JobOrderMetaDataResponse metaData = response.body();
                    if (metaData != null) {

                        pending = metaData.message.pending.count;
                        onprogress = metaData.message.onprogress.count;
                        done = metaData.message.done.count;

                        SharedPreferences prefs = getActivity().getSharedPreferences("LanguageSwitch", Context.MODE_PRIVATE);
                        String language = prefs.getString("language", "Basaha Indonesia");
                        TextView label = (TextView) tabHost.getTabWidget().getChildTabViewAt(0).findViewById(android.R.id.title);
                        if (language.contentEquals("English")) {
                            label.setText("Pending (" + pending + ")");
                        } else {
                            label.setText("Pending (" + pending + ")");
                        }
                        label = (TextView) tabHost.getTabWidget().getChildTabViewAt(1).findViewById(android.R.id.title);
                        if (language.contentEquals("English")) {
                            label.setText("Active (" + onprogress + ")");
                        } else {
                            label.setText("Pending (" + pending + ")");
                            label.setText("Aktif (" + onprogress + ")");
                        }
                        label = (TextView) tabHost.getTabWidget().getChildTabViewAt(2).findViewById(android.R.id.title);
                        if (language.contentEquals("English")) {
                            label.setText("Complete (" + done + ")");
                        } else {
                            label.setText("Selesai (" + done + ")");
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<JobOrderMetaDataResponse> call, Throwable t) {
            }
        });
    }

    public void getCount() {
        p_onprogress = 0;
        p_done = 0;
        p_pending = 0;
        onprogress = 0;
        done = 0;
        pending = 0;
        getJOMetaData();
    }

}
