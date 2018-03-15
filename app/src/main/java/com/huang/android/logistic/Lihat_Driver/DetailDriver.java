package com.huang.android.logistic.Lihat_Driver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huang.android.logistic.Maps.LiveMaps;
import com.huang.android.logistic.Model.Driver.Driver;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

public class DetailDriver extends AppCompatActivity {

    ProgressBar loading;
    LinearLayout layout;
    Driver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setTitle("Detail Driver");
        setContentView(R.layout.activity_detail_driver);

        loading=(ProgressBar)findViewById(R.id.loading);
        layout=(LinearLayout)findViewById(R.id.layout);
        layout.setVisibility(View.INVISIBLE);

        TextView nama = (TextView)findViewById(R.id.nama_supir);
        TextView phone = (TextView)findViewById(R.id.phone);
        TextView alamat = (TextView)findViewById(R.id.alamat);

        Intent intent = getIntent();
        int index = intent.getIntExtra("index",0);
        driver = ViewDriver.drivers.get(index);
        if (driver != null) {
            Utility.utility.setTextView(nama,driver.nama+"");
            Utility.utility.setTextView(phone,driver.phone+"");
            Utility.utility.setTextView(alamat,driver.address+"");
        }

        loading.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_track_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_maps:
                Intent intent = new Intent(this, LiveMaps.class);
                intent.putExtra("driver",driver.name);
                intent.putExtra("driver_name",driver.nama);
                startActivity(intent);
                return true;
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
