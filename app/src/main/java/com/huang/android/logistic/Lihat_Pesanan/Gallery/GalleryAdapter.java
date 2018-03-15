package com.huang.android.logistic.Lihat_Pesanan.Gallery;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by davidwibisono on 2/24/18.
 */

public class GalleryAdapter extends FragmentPagerAdapter {
    List<Fragment> listFragments;


    public GalleryAdapter(FragmentManager fm, List<Fragment> listFragments) {
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
