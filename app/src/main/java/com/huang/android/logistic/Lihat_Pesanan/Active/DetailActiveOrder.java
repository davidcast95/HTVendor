package com.huang.android.logistic.Lihat_Pesanan.Active;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Dashboard;
import com.huang.android.logistic.Lihat_Pesanan.Base.DetailOrder;
import com.huang.android.logistic.Lihat_Pesanan.CheckPoint;
import com.huang.android.logistic.Lihat_Pesanan.TrackHistory;
import com.huang.android.logistic.Maps.TrackOrderMaps;
import com.huang.android.logistic.Model.JobOrder.JobOrderData;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateData;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateResponse;
import com.huang.android.logistic.Model.Location.Location;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActiveOrder extends DetailOrder {
    @Override
    protected String getTitleString(String title) {
        return getString(R.string.dpa);
    }

    @Override
    protected Boolean hasOptionMenu(Boolean has) {
        return true;
    }

    @Override
    protected int getMenuType() {
        return R.menu.active_track_titlebar;
    }
}
