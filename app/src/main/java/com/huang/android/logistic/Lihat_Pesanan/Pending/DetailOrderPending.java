package com.huang.android.logistic.Lihat_Pesanan.Pending;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Dashboard;
import com.huang.android.logistic.Lihat_Driver.ChooseDriver;
import com.huang.android.logistic.Lihat_Pesanan.Base.StopLocationViewerAdapter;
import com.huang.android.logistic.MainActivity;
import com.huang.android.logistic.Model.JobOrder.GetJobOrderResponse;
import com.huang.android.logistic.Model.JobOrder.JobOrderData;
import com.huang.android.logistic.Model.JobOrder.JobOrderStatus;
import com.huang.android.logistic.Model.JobOrderRoute.JobOrderRouteData;
import com.huang.android.logistic.Model.Location.Location;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.Model.PushNotificationAction.PushNotificationAction;
import com.huang.android.logistic.Model.Vendor.VendorContactPersonData;
import com.huang.android.logistic.Model.Vendor.VendorContactPersonResponse;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailOrderPending extends AppCompatActivity {

    ListView stopLocationList;
    LinearLayout layout;
    JobOrderData jobOrder;
    Spinner dropdownCP;
    EditText vendorName, vendorCP;

    List<VendorContactPersonData> cps = null;

    String[] principleCPs;
    Boolean insertedCP = false;
    String from;
    public int listHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order_pending);

        setTitle(R.string.title_pending_jo);
        principleCPs = new String[]{ getString(R.string.dp_dropdown_add)};

        layout=(LinearLayout)findViewById(R.id.layout);
        dropdownCP = (Spinner)findViewById(R.id.vendor_dropdown);
        vendorCP = (EditText)findViewById(R.id.vendor_cp);
        vendorName = (EditText)findViewById(R.id.vendor_name);
        stopLocationList = (ListView)findViewById(R.id.stop_location_list);
        fetchCPs();

        Intent intent = getIntent();
        String job_order = intent.getStringExtra("joid");
        String notif = intent.getStringExtra("notif");
        if (notif != null) {
            if (notif.equals(PushNotificationAction.NEW_JO)) {
                Toast.makeText(getApplicationContext(),getString(R.string.msg_new_jo),Toast.LENGTH_LONG).show();
                setTitle(R.string.title_new_jo);
            }
            if (notif.equals(PushNotificationAction.RTO_JO)) {
                String value = intent.getStringExtra("value");
                Toast.makeText(getApplicationContext(),getString(R.string.msg_vendor_rto) + " " + value + " " + getString(R.string.hour),Toast.LENGTH_LONG).show();
            }
        }
        if (job_order != null) {
            getJobOrderBy(job_order);
        } else {
            int index = intent.getIntExtra("index", 0);
            from = intent.getStringExtra("from");
            if (from.equals("View Order")) {
                if (OrderPending.jobOrders.get(index) != null) {
                    jobOrder = OrderPending.jobOrders.get(index);

                }
            } else {
                if (Dashboard.jobOrders.get(index) != null) {
                    jobOrder = Dashboard.jobOrders.get(index);
                }
            }
            if (jobOrder != null) {
                TextView principle = (TextView) findViewById(R.id.principle);
                TextView ref = (TextView) findViewById(R.id.ref_id);
                TextView joid = (TextView) findViewById(R.id.joid);
                TextView principle_name = (TextView) findViewById(R.id.principle_name);
                TextView principle_cp_name = (TextView) findViewById(R.id.principle_cp_name);
                TextView principle_cp_phone = (TextView) findViewById(R.id.principle_cp_phone);
                TextView cargoInfo = (TextView) findViewById(R.id.cargo_info);
                TextView epd = (TextView) findViewById(R.id.pick_date);
                TextView edd = (TextView) findViewById(R.id.delivered_date);
                TextView volume = (TextView) findViewById(R.id.volume);
                TextView truck_type = (TextView) findViewById(R.id.expected_truck_type);
                TextView cargoNote = (TextView) findViewById(R.id.cargo_notes);

                principle.setText(jobOrder.principle);
                if (jobOrder.ref == null) jobOrder.ref = "";
                ref.setText("Ref No : " + jobOrder.ref.replace("\n", ""));
                joid.setText(jobOrder.joid);


                StopLocationViewerAdapter stopLocationAdapter = new StopLocationViewerAdapter(getApplicationContext(), jobOrder.routes,this);
                stopLocationList.setAdapter(stopLocationAdapter);
                ViewTreeObserver observer = stopLocationList.getViewTreeObserver();

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

                principle_name.setText(jobOrder.principle);
                principle_cp_name.setText(jobOrder.principle_cp_name);
                principle_cp_phone.setText(jobOrder.principle_cp_phone);
                Utility.utility.setDialContactPhone(principle_cp_phone, jobOrder.principle_cp_phone, this);
                cargoInfo.setText(jobOrder.cargoInfo);
                epd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.etd));
                edd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.eta));

                volume.setText(jobOrder.estimate_volume);
                truck_type.setText(jobOrder.suggest_truck_type);
                cargoNote.setText(jobOrder.notes);
            }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checklist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_yes:
                dialog();
                break;
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    void updateStateUI() {
        String nameCP = vendorName.getText().toString(), phoneCP = vendorCP.getText().toString();
        if (nameCP.equals("") || phoneCP.equals("")) {
            Toast.makeText(getApplicationContext(), "Please fill Contact Person", Toast.LENGTH_SHORT).show();
        } else {
            if (dropdownCP.getSelectedItem().toString().equals(getString(R.string.dp_dropdown_add)) && insertedCP == false) {
                insertedCP = false;
                insertCP(nameCP,phoneCP);
            } else {
                Intent intent = new Intent(DetailOrderPending.this, ChooseDriver.class);
                intent.putExtra("joid",jobOrder.joid);
                intent.putExtra("from",from);
                intent.putExtra("strict",jobOrder.strict);
                intent.putExtra("expected_truck",jobOrder.suggest_truck_type);
                intent.putExtra("status",JobOrderStatus.ON_PROGRESS);
                intent.putExtra("nama_vendor_cp",nameCP);
                intent.putExtra("telp_vendor_cp",phoneCP);
                startActivityForResult(intent,100);
            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    public void dialog() {

        new AlertDialog.Builder(this)
                .setTitle(R.string.popup_announcement)
                .setMessage(R.string.popup_announcement2)
                .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateStateUI();
                    }
                })
                .setNegativeButton(R.string.popup_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateJobOrderStatus(false);
                    }
                })
                .show();
    }

    void updateJobOrderStatus(final Boolean accept) {
        final RelativeLayout loading = (RelativeLayout)findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        String status = (accept) ? JobOrderStatus.ON_PROGRESS : JobOrderStatus.REJECTED;
        HashMap<String,String> statusJSON = new HashMap<>();
        String nameCP = vendorName.getText().toString(), phoneCP = vendorCP.getText().toString();
        String name  = Utility.utility.getLoggedName(this);
        statusJSON.put("status",status);
        if (accept) {
            statusJSON.put("vendor_contact_person", nameCP + " (" + name + ")");
            statusJSON.put("nama_vendor_cp", nameCP);
            statusJSON.put("telp_vendor_cp", phoneCP);
            Date today = new Date();
            statusJSON.put("accept_date", Utility.utility.dateToFormatDatabase(today));
        }
        final String json = new Gson().toJson(statusJSON);
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<JSONObject> callUpdateJO = api.updateJobOrder(jobOrder.joid, statusJSON);
        callUpdateJO.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                loading.setVisibility(View.GONE);
                if (Utility.utility.catchResponse(getApplicationContext(), response, json)) {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Utility.utility.showConnectivityWithError(getApplicationContext(),t);
            }
        });
    }

    void fetchCPs() {
        //create client to get cookies from OkHttp
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        String name = Utility.utility.getLoggedName(this);
        okHttpClient.cookieJar(cookieJar);
        OkHttpClient client = okHttpClient.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        API api = retrofit.create(API.class);
        Call<VendorContactPersonResponse> callCP = api.getPrincipleCP("[[\"Vendor Contact Person\", \"vendor\", \"=\", \"" + name + "\"]]");

        final Activity thisActivity = this;

        callCP.enqueue(new Callback<VendorContactPersonResponse>() {
            @Override
            public void onResponse(Call<VendorContactPersonResponse> call, Response<VendorContactPersonResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response, "")) {
                    VendorContactPersonResponse cpResponse = response.body();
                    if (cpResponse.cps == null) {
                        return;
                    }
                    cps = cpResponse.cps;
                    principleCPs = new String[cps.size() + 1];
                    for (int i = 0; i < cps.size(); i++) {
                        principleCPs[i] = cps.get(i).name + " (" + cps.get(i).phone + ")";
                    }
                    principleCPs[cps.size()] = getString(R.string.dp_dropdown_add);
                    dropdownCP = (Spinner) findViewById(R.id.vendor_dropdown);
                    ArrayAdapter<String> principleDropdownAdapter = new ArrayAdapter<String>(thisActivity, R.layout.support_simple_spinner_dropdown_item, principleCPs);
                    dropdownCP.setAdapter(principleDropdownAdapter);
                    dropdownCP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (cps == null) {
                                return;
                            }
                            if (i >= cps.size()) {
                                vendorCP.setEnabled(true);
                                vendorName.setEnabled(true);
                                vendorName.setText("");
                                vendorCP.setText("");
                            } else {
                                vendorCP.setEnabled(false);
                                vendorName.setEnabled(false);
                                vendorName.setText(cps.get(i).name);
                                vendorCP.setText(cps.get(i).phone);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<VendorContactPersonResponse> call, Throwable t) {
                Utility.utility.showConnectivityWithError(getApplicationContext(), t);
            }
        });
    }

    void insertCP(String name, String phone) {
        VendorContactPersonData cpData = new VendorContactPersonData();
        cpData.name = name;
        cpData.phone = phone;
        cpData.principle = Utility.utility.getLoggedName(this);

        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<JSONObject> insertCPResponseCall = api.insertVendorCP(cpData);
        final Activity activity = this;
        final String json = new Gson().toJson(cpData);
        insertCPResponseCall.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response, json)) {
                    insertedCP = true;
                    updateStateUI();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {

            }
        });
    }

    void getJobOrderBy(String joid) {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        final Activity activity = this;
        Call<GetJobOrderResponse> callJO = api.getJobOrderBy(joid);
        callJO.enqueue(new Callback<GetJobOrderResponse>() {
            @Override
            public void onResponse(Call<GetJobOrderResponse> call, Response<GetJobOrderResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response, "")) {
                    GetJobOrderResponse jobOrderResponse = response.body();
                    if (jobOrderResponse.jobOrders != null) {
                        jobOrder = jobOrderResponse.jobOrders.get(0);
                        TextView principle = (TextView) findViewById(R.id.principle);
                        TextView ref = (TextView) findViewById(R.id.ref_id);
                        TextView joid = (TextView) findViewById(R.id.joid);
                        TextView principle_name = (TextView) findViewById(R.id.principle_name);
                        TextView principle_cp_name = (TextView) findViewById(R.id.principle_cp_name);
                        TextView principle_cp_phone = (TextView) findViewById(R.id.principle_cp_phone);
                        TextView cargoInfo = (TextView) findViewById(R.id.cargo_info);
                        TextView epd = (TextView) findViewById(R.id.pick_date);
                        TextView edd = (TextView) findViewById(R.id.delivered_date);
                        TextView volume = (TextView) findViewById(R.id.volume);
                        TextView truck_type = (TextView) findViewById(R.id.expected_truck_type);
                        TextView cargoNote = (TextView) findViewById(R.id.cargo_notes);

                        principle.setText(jobOrder.principle);
                        if (jobOrder.ref == null) jobOrder.ref = "";
                        ref.setText("Ref No : " + jobOrder.ref.replace("\n",""));
                        joid.setText(jobOrder.joid);

                        if (jobOrder.routes.size() > 2) {
                            StopLocationViewerAdapter stopLocationAdapter = new StopLocationViewerAdapter(getApplicationContext(),jobOrder.routes.subList(2,jobOrder.routes.size()),activity);
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
                                    dialogStopLocation(jobOrder.routes.subList(2,jobOrder.routes.size()).get(i));
                                }
                            });
                        }
                        principle_name.setText(jobOrder.principle);
                        principle_cp_name.setText(jobOrder.principle_cp_name);
                        principle_cp_phone.setText(jobOrder.principle_cp_phone);
                        Utility.utility.setDialContactPhone(principle_cp_phone, jobOrder.principle_cp_phone, activity);
                        cargoInfo.setText(jobOrder.cargoInfo);
                        epd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.etd));
                        edd.setText(Utility.formatDateFromstring(Utility.dateDBShortFormat, Utility.dateLongFormat, jobOrder.eta));

                        volume.setText(jobOrder.estimate_volume);
                        truck_type.setText(jobOrder.suggest_truck_type);
                        cargoNote.setText(jobOrder.notes);
                    }

                }

            }

            @Override
            public void onFailure(Call<GetJobOrderResponse> call, Throwable t) {
                Utility.utility.showConnectivityWithError(getApplicationContext(), t);
            }
        });
    }
}
