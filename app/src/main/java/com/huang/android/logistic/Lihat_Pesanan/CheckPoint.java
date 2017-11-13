package com.huang.android.logistic.Lihat_Pesanan;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Maps.DirectionFinderListener;
import com.huang.android.logistic.Maps.Route;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateCreation;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateData;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckPoint extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

        private Spinner spinner;
        private GoogleMap mMap;
        Double lat=0.0, longi=0.0;
        EditText notesEditText;
        List<String> categories = new ArrayList<String>();
        String joid, principle;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setTitle(R.string.checkpoint);
            setContentView(R.layout.activity_check_point);

            notesEditText = (EditText)findViewById(R.id.notes);
            Intent intent = getIntent();
            joid = intent.getStringExtra("joid");
            principle = intent.getStringExtra("principle");

            getNextStage();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }


            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }

    void updateStateUI() {
        String notes = notesEditText.getText().toString();
        if (notes.equals("")) {
            Toast.makeText(getApplicationContext(),"Notes cannot be empty",Toast.LENGTH_SHORT).show();
        } else {
            updateStatus();
        }
    }
        //API
    void updateStatus() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        JobOrderUpdateData jobOrderUpdateData = new JobOrderUpdateData();
        jobOrderUpdateData.joid = joid;
        Date today = new Date();
        jobOrderUpdateData.note = notesEditText.getText().toString();
        jobOrderUpdateData.time = Utility.utility.dateToFormatDatabase(today);
        jobOrderUpdateData.longitude = longi + "";
        jobOrderUpdateData.latitude = lat + "";
        jobOrderUpdateData.docstatus = 1;
        jobOrderUpdateData.vendor = Utility.utility.getLoggedName(this);
        jobOrderUpdateData.principle = principle;
        String status = spinner.getSelectedItem().toString();
        jobOrderUpdateData.status = status;
        Call<JobOrderUpdateCreation> callInsertUpdateJO = api.insertUpdateJO(jobOrderUpdateData);
        callInsertUpdateJO.enqueue(new Callback<JobOrderUpdateCreation>() {
            @Override
            public void onResponse(Call<JobOrderUpdateCreation> call, Response<JobOrderUpdateCreation> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {

                    Toast.makeText(getApplicationContext(), joid + " has been updated",Toast.LENGTH_SHORT);
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JobOrderUpdateCreation> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });
    }


    public void getNextStage() {
        categories.add("Update Lokasi");
        categories.add("Proses Muat");
        categories.add("Proses Bongkar");
        categories.add("Proses Timbang");
        categories.add("Menginap");
        categories.add("Tertahan");
        categories.add("Selesai");
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        LatLng lokasi = new LatLng(lat, longi);
//        LatLng lokasi = new LatLng(0,0);
//        mMap.addMarker(new MarkerOptions().position(lokasi).title(""));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 17));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }
    GoogleApiClient mGoogleApiClient;
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    LocationRequest mLocationRequest;
    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
        }

    }

    Location mLastLocation;
    Marker mCurrLocationMarker;
    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        lat=location.getLatitude();
        longi=location.getLongitude();
        Log.e("LATLONG",lat+","+longi);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }

    public void dialog() {

        new AlertDialog.Builder(this)
                .setTitle(R.string.popup_announcement)
                .setMessage(R.string.popup_announcement3)
                .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateStateUI();
                    }
                })
                .show();
    }


    @Override
    public void onDirectionFinderStart() {
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
