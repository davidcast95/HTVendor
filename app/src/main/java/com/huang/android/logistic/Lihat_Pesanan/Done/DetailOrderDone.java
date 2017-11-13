package com.huang.android.logistic.Lihat_Pesanan.Done;


import com.huang.android.logistic.Lihat_Pesanan.Base.DetailOrder;
import com.huang.android.logistic.R;

public class DetailOrderDone extends DetailOrder {

    @Override
    protected String getTitleString(String title) {
        return getString(R.string.dpd);
    }

    @Override
    protected Boolean hasOptionMenu(Boolean has) {
        return true;
    }

    @Override
    protected int getMenuType() {
        return R.menu.done_track_titlebar;
    }
}
