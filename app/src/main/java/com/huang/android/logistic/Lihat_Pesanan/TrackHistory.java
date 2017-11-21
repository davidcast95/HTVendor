package com.huang.android.logistic.Lihat_Pesanan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huang.android.logistic.Lihat_Pesanan.Active.DetailActiveOrder;
import com.huang.android.logistic.Lihat_Pesanan.Done.DetailOrderDone;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateData;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import java.util.ArrayList;
import java.util.List;

public class TrackHistory extends AppCompatActivity {

    String joid, date,to,from;
    ListView lv;
    TrackHistoryAdapter trackHistoryAdapter;
    ProgressBar loading;
    LinearLayout layout;
    TextView nodata;

    List<JobOrderUpdateData> jobOrderUpdateDataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setTitle(R.string.tracking_history);
        setContentView(R.layout.activity_track_history);

        nodata = (TextView) findViewById(R.id.no_data);
        nodata.setVisibility(View.GONE);
        loading=(ProgressBar)findViewById(R.id.loading);
        lv=(ListView) findViewById(R.id.historylist);
        layout=(LinearLayout)findViewById(R.id.layout);

        Intent intent = getIntent();
        joid = intent.getStringExtra("joid");
        from = intent.getStringExtra("origin");
        to = intent.getStringExtra("destination");

        TextView joid = (TextView)findViewById(R.id.joid);
        joid.setText(this.joid);
        TextView tanggal = (TextView)findViewById(R.id.tanggal1);
        tanggal.setText(date);
        TextView locationfrom = (TextView)findViewById(R.id.locationfrom);
        locationfrom.setText(from);
        TextView locationto = (TextView)findViewById(R.id.locationto);
        locationto.setText(to);

        loading=(ProgressBar)findViewById(R.id.loading);
        lv=(ListView) findViewById(R.id.historylist);
        layout=(LinearLayout)findViewById(R.id.layout);
        loading.setVisibility(View.GONE);
        from = getIntent().getStringExtra("from");

        if (from.equals("OrderActive")) {
            jobOrderUpdateDataList = DetailActiveOrder.jobOrderUpdates;
        } else if (from.equals("OrderDone")) {
            jobOrderUpdateDataList = DetailOrderDone.jobOrderUpdates;
        }

        if (jobOrderUpdateDataList.size() > 0) {
            trackHistoryAdapter = new TrackHistoryAdapter(getApplicationContext(), R.layout.activity_track_history_list, jobOrderUpdateDataList);
            lv.setAdapter(trackHistoryAdapter);
        } else {
            nodata.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
        }
        layout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
