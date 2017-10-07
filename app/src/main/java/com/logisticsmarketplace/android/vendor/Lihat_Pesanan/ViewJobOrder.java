package com.logisticsmarketplace.android.vendor.Lihat_Pesanan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.logisticsmarketplace.android.vendor.Lihat_Pesanan.Active.OrderActive;
import com.logisticsmarketplace.android.vendor.Lihat_Pesanan.Pending.OrderPending;
import com.logisticsmarketplace.android.vendor.R;

import java.util.ArrayList;
import java.util.List;

public class ViewJobOrder extends Fragment implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener{
    ViewPager viewPager;
    TabHost tabHost;
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

        return v;
    }

    private void initTabHost() {
        tabHost=(TabHost)v.findViewById(R.id.tabhost);
        tabHost.setup();

        SharedPreferences prefs = this.getActivity().getSharedPreferences("LanguageSwitch", Context.MODE_PRIVATE);
        String language = prefs.getString("language","English");
        String[] tabs = new String[2];
        if(language.contentEquals("English")){
            tabs[0]="On Progress";
            tabs[1]="Pending";
        }
        else {
            tabs[0]="Dalam Proses";
            tabs[1]="Pending";
        }

        for(int i=0;i<2;i++){
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
        listFragments.add(new OrderActive());
        listFragments.add(new OrderPending());

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

}
