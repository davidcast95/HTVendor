package com.huang.android.logistic.Lihat_Pesanan.Base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Chat.Chat;
import com.huang.android.logistic.Dashboard;
import com.huang.android.logistic.Lihat_Pesanan.Active.OrderActive;
import com.huang.android.logistic.Lihat_Pesanan.CheckPoint;
import com.huang.android.logistic.Lihat_Pesanan.Done.OrderDone;
import com.huang.android.logistic.Lihat_Pesanan.TrackHistory;
import com.huang.android.logistic.Maps.TrackOrderMaps;
import com.huang.android.logistic.Model.JobOrder.JobOrderData;
import com.huang.android.logistic.Model.JobOrderRoute.JobOrderRouteData;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateData;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateResponse;
import com.huang.android.logistic.Model.Location.Location;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by davidwibisono on 20/10/17.
 */

public class DetailOrder extends AppCompatActivity {

    ListView stopLocationList;
    LinearLayout layout, last_status;
    TextView statusTimeTV,statusTV,notesTV;

    public static JobOrderData jobOrder;
    public static List<JobOrderUpdateData> jobOrderUpdates = new ArrayList<>();
    String from;
    public int listHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);

        setTitle(getTitleString(""));

        layout=(LinearLayout)findViewById(R.id.layout);
        last_status=(LinearLayout)findViewById(R.id.last_status);
        last_status.setVisibility(View.GONE);
        statusTimeTV=(TextView)findViewById(R.id.status_time);
        statusTV=(TextView)findViewById(R.id.status);
        notesTV = (TextView)findViewById(R.id.notes);
        stopLocationList = (ListView)findViewById(R.id.stop_location_list);


        Intent intent = getIntent();
        int index = intent.getIntExtra("index", 0);
        from = intent.getStringExtra("from");
        if (from.equals("OrderActive")) {
            if (OrderActive.jobOrders.get(index) != null) {
                jobOrder = OrderActive.jobOrders.get(index);

            }
        } else if (from.equals("OrderDone")) {
            if (OrderDone.jobOrders.get(index) != null) {
                jobOrder = OrderDone.jobOrders.get(index);

            }
        } else {
            if (Dashboard.jobOrders.get(index) != null) {
                jobOrder = Dashboard.jobOrders.get(index);
            }
        }
        if (jobOrder != null) {
            TextView principle = (TextView)findViewById(R.id.principle);
            TextView ref = (TextView)findViewById(R.id.ref_id);
            TextView joid = (TextView) findViewById(R.id.joid);
            TextView vendor_name = (TextView) findViewById(R.id.vendor_name);
            TextView vendor_cp_name = (TextView) findViewById(R.id.vendor_cp_name);
            TextView vendor_cp_phone = (TextView) findViewById(R.id.vendor_cp_phone);
            TextView principle_name = (TextView) findViewById(R.id.principle_name);
            TextView principle_cp_name = (TextView) findViewById(R.id.principle_cp_name);
            TextView principle_cp_phone = (TextView) findViewById(R.id.principle_cp_phone);
            TextView cargoInfo = (TextView) findViewById(R.id.cargo_info);
            TextView epd = (TextView) findViewById(R.id.pick_date);
            TextView edd = (TextView) findViewById(R.id.delivered_date);
            TextView volume = (TextView) findViewById(R.id.volume);
            TextView truck = (TextView) findViewById(R.id.truck);
            TextView truck_type = (TextView)findViewById(R.id.truck_type);
            TextView truck_hull_no = (TextView)findViewById(R.id.truck_hull_no);
            TextView cargoNote = (TextView)findViewById(R.id.cargo_notes);
            TextView driver_name = (TextView)findViewById(R.id.driver_name);
            TextView driver_phone = (TextView)findViewById(R.id.driver_phone);

            principle.setText(jobOrder.principle);
            if (jobOrder.ref == null) jobOrder.ref = "";
            ref.setText("Ref No : " + jobOrder.ref.replace("\n",""));
            joid.setText(jobOrder.joid);

            if (jobOrder.ref == null) jobOrder.ref = "";
            ref.setText("Ref No : " + jobOrder.ref.replace("\n",""));
            joid.setText(jobOrder.joid);

            StopLocationViewerAdapter stopLocationAdapter = new StopLocationViewerAdapter(getApplicationContext(),jobOrder.routes, this);
            stopLocationList.setAdapter(stopLocationAdapter);
            ViewTreeObserver observer = stopLocationList .getViewTreeObserver();

            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    listHeight = Utility.utility.setAndGetListViewHeightBasedOnChildren(stopLocationList);
                }
            });

            stopLocationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    dialogStopLocation(jobOrder.routes.get(i));
                }
            });

            principle.setText(jobOrder.principle);

            final ImageView profileImage = (ImageView)findViewById(R.id.profile_image);

            String imageUrl = jobOrder.principle_image.get(0);
            if (imageUrl != null) {
                MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(getApplicationContext());
                API api = Utility.utility.getAPIWithCookie(cookieJar);
                Call<ResponseBody> callImage = api.getImage(imageUrl);
                callImage.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if (responseBody != null) {
                                Bitmap bm = BitmapFactory.decodeStream(response.body().byteStream());
                                profileImage.setImageBitmap(bm);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            } else {
                profileImage.setImageDrawable(getResources().getDrawable(R.drawable.order_box));
            }

            vendor_name.setText(jobOrder.vendor);
            vendor_cp_name.setText(jobOrder.vendor_cp_name);
            vendor_cp_phone.setText(jobOrder.vendor_cp_phone);
            Utility.utility.setDialContactPhone(vendor_cp_phone, jobOrder.vendor_cp_phone, this);
            principle_name.setText(jobOrder.principle);
            principle_cp_name.setText(jobOrder.principle_cp_name);
            principle_cp_phone.setText(jobOrder.principle_cp_phone);
            Utility.utility.setDialContactPhone(principle_cp_phone, jobOrder.principle_cp_phone, this);
            cargoInfo.setText(jobOrder.cargoInfo);
            epd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.etd));
            edd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.eta));

            volume.setText(jobOrder.estimate_volume);
            truck.setText(jobOrder.truck);
            truck_type.setText(jobOrder.truck_type);
            truck_hull_no.setText(jobOrder.truck_lambung);
            cargoNote.setText(jobOrder.notes);
            driver_name.setText(jobOrder.driver_name);
            driver_phone.setText(jobOrder.driver_phone);
            Utility.utility.setDialContactPhone(driver_phone, jobOrder.driver_phone, this);
        }

    }


    public void dialogStopLocation(JobOrderRouteData route) {
        new AlertDialog.Builder(this);
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_stop_location_detail, null);


        TextView typeTV = (TextView)promptsView.findViewById(R.id.type),
                stopLocationTV = (TextView)promptsView.findViewById(R.id.stop_location),
                nameTV = (TextView)promptsView.findViewById(R.id.name),
                cpTV = (TextView)promptsView.findViewById(R.id.cp),
                itemTV = (TextView)promptsView.findViewById(R.id.item),
                remarkTV = (TextView)promptsView.findViewById(R.id.remark);

        Utility.utility.setTextView(typeTV,route.type);
        Utility.utility.setTextView(stopLocationTV,Utility.utility.longformatLocation(new Location(route.distributor_code,route.location,route.city,route.address,route.warehouse_name,"","")));
        Utility.utility.setTextView(nameTV,route.nama);
        Utility.utility.setTextView(cpTV,route.phone);
        Utility.utility.setTextView(itemTV,route.item_info);
        Utility.utility.setTextView(remarkTV,route.remark);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLastUpdate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(getMenuType(), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (hasOptionMenu(true)) {
            switch (item.getItemId()) {
                case R.id.action_history:
                    Intent intent = new Intent(this, TrackHistory.class);
                    intent.putExtra("joid", jobOrder.joid);
                    intent.putExtra("origin", Utility.utility.longformatLocation(new Location(jobOrder.routes.get(0).distributor_code,jobOrder.routes.get(0).location,jobOrder.routes.get(0).city,jobOrder.routes.get(0).address,jobOrder.routes.get(0).warehouse_name,"","")));
                    intent.putExtra("origin_type",jobOrder.routes.get(0).type);
                    int lastIndex = jobOrder.routes.size()-1;
                    intent.putExtra("destination", Utility.utility.longformatLocation(new Location(jobOrder.routes.get(lastIndex).distributor_code,jobOrder.routes.get(lastIndex).location,jobOrder.routes.get(lastIndex).city,jobOrder.routes.get(lastIndex).address,jobOrder.routes.get(lastIndex).warehouse_name,"","")));
                    intent.putExtra("destination_type",jobOrder.routes.get(lastIndex).type);intent.putExtra("from",from);
                    intent.putExtra("driver", jobOrder.driver);
                    startActivity(intent);
                    break;
                case R.id.action_cekpoint:
                    Intent intent3 = new Intent(this, CheckPoint.class);
                    intent3.putExtra("joid", jobOrder.joid);
                    intent3.putExtra("principle", jobOrder.principle);
                    intent3.putExtra("vendor", jobOrder.vendor);
                    intent3.putExtra("driver", jobOrder.driver);
                    startActivityForResult(intent3, 200);
                    break;
                case R.id.action_message:
                    Intent intent1 = new Intent(this, Chat.class);
                    intent1.putExtra("joid",jobOrder.joid);
                    startActivity(intent1);
                    break;
                case android.R.id.home:
                    // app icon in action bar clicked; goto parent activity.
                    this.finish();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }

            return true;
        } else {
            return false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                getLastUpdate();
            }
        }
    }

    protected int getMenuType() {
        return R.menu.active_track_titlebar;
    }

    //API
    void getLastUpdate() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<JobOrderUpdateResponse> callGetJOUpdate = api.getJOUpdate(jobOrder.joid);
        callGetJOUpdate.enqueue(new Callback<JobOrderUpdateResponse>() {
            @Override
            public void onResponse(Call<JobOrderUpdateResponse> call, Response<JobOrderUpdateResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response, "")) {
                    JobOrderUpdateResponse jobOrderUpdateResponse = response.body();
                    if (jobOrderUpdateResponse != null) {
                        jobOrderUpdates = jobOrderUpdateResponse.jobOrderUpdates;
                        if (jobOrderUpdateResponse.jobOrderUpdates != null) {
                            if (jobOrderUpdates.size() > 0) {
                                final JobOrderUpdateData lastJOStatus = jobOrderUpdates.get(0);
                                last_status.setVisibility(View.VISIBLE);
                                statusTimeTV.setText(Utility.formatDateFromstring(Utility.dateDBLongFormat, Utility.LONG_DATE_TIME_FORMAT, lastJOStatus.time));
                                statusTV.setText(lastJOStatus.status);
                                notesTV.setText(lastJOStatus.note);
                                ImageView button = (ImageView) findViewById(R.id.detail_active_last_map);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Double latitude = Double.valueOf(lastJOStatus.latitude), longitude = Double.valueOf(lastJOStatus.longitude);
                                        if (latitude != null || longitude != null) {
                                            Intent maps = new Intent(DetailOrder.this, TrackOrderMaps.class);
                                            maps.putExtra("longitude", longitude);
                                            maps.putExtra("latitude", latitude);

                                            maps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(maps);
                                        } else {
                                            Context c = getApplicationContext();
                                            Toast.makeText(c, R.string.nla, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else {
                                last_status.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JobOrderUpdateResponse> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });
    }

    //delegate
    protected String getTitleString(String title) {
        if (title.equals(""))
            return getResources().getString(R.string.track_order_list);
        else
            return title;
    }
    protected Boolean hasOptionMenu(Boolean has) {
        return has;
    }
}
