package com.huang.android.logistic.Lihat_Pesanan.Pending;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Dashboard;
import com.huang.android.logistic.Lihat_Driver.ChooseDriver;
import com.huang.android.logistic.MainActivity;
import com.huang.android.logistic.Model.JobOrder.JobOrderData;
import com.huang.android.logistic.Model.JobOrder.JobOrderStatus;
import com.huang.android.logistic.Model.Location.Location;
import com.huang.android.logistic.Model.MyCookieJar;
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

    LinearLayout layout;
    JobOrderData jobOrder;
    Spinner dropdownCP;
    EditText vendorName, vendorCP;

    List<VendorContactPersonData> cps = null;

    String[] principleCPs = new String[]{ "Create a new" };
    Boolean insertedCP = false;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order_pending);

        setTitle(R.string.track_order_list);

        layout=(LinearLayout)findViewById(R.id.layout);
        dropdownCP = (Spinner)findViewById(R.id.vendor_dropdown);
        vendorCP = (EditText)findViewById(R.id.vendor_cp);
        vendorName = (EditText)findViewById(R.id.vendor_name);
        fetchCPs();

        Intent intent = getIntent();
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
            TextView ref = (TextView)findViewById(R.id.ref_id);
            TextView joid = (TextView) findViewById(R.id.joid);
            TextView origin = (TextView) findViewById(R.id.origin);
            TextView destination = (TextView) findViewById(R.id.destination);
            TextView principle_cp_name = (TextView) findViewById(R.id.principle_cp_name);
            TextView principle_cp_phone = (TextView) findViewById(R.id.principle_cp_phone);
            TextView cargoInfo = (TextView) findViewById(R.id.cargo_info);
            TextView epd = (TextView) findViewById(R.id.pick_date);
            TextView edd = (TextView) findViewById(R.id.delivered_date);
            TextView volume = (TextView) findViewById(R.id.volume);
            TextView truck_type = (TextView)findViewById(R.id.expected_truck_type);
            TextView cargoNote = (TextView)findViewById(R.id.cargo_notes);

            if (jobOrder.ref == null) jobOrder.ref = "";
            ref.setText("Ref No : " + jobOrder.ref);
            joid.setText(jobOrder.joid);
            origin.setText(Utility.utility.formatLocation(new Location(jobOrder.origin_code,jobOrder.origin,jobOrder.origin_city,jobOrder.origin_address,jobOrder.origin_warehouse,"","")));
            destination.setText(Utility.utility.formatLocation(new Location(jobOrder.destination_code,jobOrder.destination,jobOrder.destination_city,jobOrder.destination_address,jobOrder.destination_warehouse,"","")));

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
            if (dropdownCP.getSelectedItem().toString().equals("Create a new") && insertedCP == false) {
                insertedCP = false;
                insertCP(nameCP,phoneCP);
            } else {
                Intent intent = new Intent(DetailOrderPending.this, ChooseDriver.class);
                intent.putExtra("joid",jobOrder.joid);
                intent.putExtra("from",from);
                intent.putExtra("expected_truck",jobOrder.suggest_truck_type);
                intent.putExtra("status",JobOrderStatus.ON_PROGRESS);
                intent.putExtra("nama_vendor_cp",jobOrder.vendor_cp_name);
                intent.putExtra("telp_vendor_cp",jobOrder.vendor_cp);
                startActivityForResult(intent,100);
            }


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
        String status = (accept) ? JobOrderStatus.ON_PROGRESS : JobOrderStatus.REJECTED;
        HashMap<String,String> statusJSON = new HashMap<>();
        String nameCP = vendorName.getText().toString(), phoneCP = vendorCP.getText().toString();
        String name  = Utility.utility.getLoggedName(this);
        statusJSON.put("status",status);
        statusJSON.put("vendor_contact_person", nameCP + " ("+name+")");
        statusJSON.put("nama_vendor_cp", nameCP);
        statusJSON.put("telp_vendor_cp",phoneCP);
        Date today = new Date();
        statusJSON.put("accept_date",Utility.utility.dateToFormatDatabase(today));

        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<JSONObject> callUpdateJO = api.updateJobOrder(jobOrder.joid, statusJSON);
        callUpdateJO.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    if (accept) {
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), TolakPesanan.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
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
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    VendorContactPersonResponse cpResponse = response.body();
                    if (cpResponse.cps == null) {
                        return;
                    }
                    cps = cpResponse.cps;
                    principleCPs = new String[cps.size() + 1];
                    for (int i = 0; i < cps.size(); i++) {
                        principleCPs[i] = cps.get(i).name + " (" + cps.get(i).phone + ")";
                    }
                    principleCPs[cps.size()] = "Create a new";
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
        insertCPResponseCall.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    insertedCP = true;
                    updateStateUI();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {

            }
        });
    }
}
