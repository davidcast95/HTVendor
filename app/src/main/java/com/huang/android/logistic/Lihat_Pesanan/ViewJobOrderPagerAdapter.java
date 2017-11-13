package com.huang.android.logistic.Lihat_Pesanan;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Kristoforus Gumilang on 8/24/2017.
 */

public class ViewJobOrderPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> listFragments;

    public ViewJobOrderPagerAdapter(FragmentManager fm, List<Fragment> listFragments) {
        super(fm);
        this.listFragments=listFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return listFragments.get(position);
    }

    @Override
    public int getCount() {
        return listFragments.size();
    }
}
