package com.huang.android.logistic.Maps;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Model.Driver.Driver;
import com.huang.android.logistic.Model.Driver.DriverBackgroundUpdateData;
import com.huang.android.logistic.Model.Driver.DriverBackgroundUpdateResponse;
import com.huang.android.logistic.Model.Driver.DriverResponse;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveAllDriversMaps extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    boolean isLoading = true;
    RelativeLayout loading, nodata;
    int pager = 0, limit = 20;
    ListView driverList;
    DriverLocationAdapter driverLocationAdapter;

    List<Driver> allDrivers = new ArrayList<>();
    List<Driver> drivers = new ArrayList<>();
    boolean loadDriver = true;

    public static LiveAllDriversMaps instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LiveAllDriversMaps.instance = this;
        setContentView(R.layout.activity_track_order_maps_with_footer);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setTitle(R.string.title_live_maps);

        loading = (RelativeLayout) findViewById(R.id.loading);
        nodata = (RelativeLayout) findViewById(R.id.nodata);
        nodata.setVisibility(View.GONE);
        driverList = (ListView)findViewById(R.id.footer_list);
        driverLocationAdapter = new DriverLocationAdapter(getApplicationContext(),R.layout.fragment_view_driver_list,drivers);
        driverList.setAdapter(driverLocationAdapter);
        driverList.setOnItemClickListener(onListClick);
        if (isLoading) {
            loading.setVisibility(View.VISIBLE);
        }


    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent,
                                View view,
                                int position, long id){
            Driver focusedDriver = driverLocationAdapter.getItem(position);
            LatLng loc = new LatLng(focusedDriver.lat, focusedDriver.lng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,18));
        }
    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getDriver();
    }

    public void refetchBackgroundUpdate(final String driver) {
        if (mMap != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mappingDriver(driver);
                }
            }, 2000);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                LiveAllDriversMaps.instance = null;
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void updateMarker() {

        driverLocationAdapter.clear();
        mMap.clear();
        double minLat = -1, maxLat = -1, minLong = -1, maxLong = -1;

        for (int i=0;i<allDrivers.size();i++) {
            if (allDrivers.get(i).lng != 0 && allDrivers.get(i).lat != 0) {
                Geocoder geoCoder = new Geocoder(getApplicationContext());
                Double lat = allDrivers.get(i).lat, longi = allDrivers.get(i).lng;
                String lastUpdate = allDrivers.get(i).last_update;
                Driver driver = new Driver();
                try {
                    List<Address> matches = geoCoder.getFromLocation(lat, longi, 1);
                    Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
                    bestMatch.setLocality(Utility.utility.getLocality(this));
                    driver.currentLocation = bestMatch.getAddressLine(0);
                } catch (IOException error) {
                    driver.currentLocation = "-";
                }
                driver.lat = lat;
                driver.lng = longi;
                driver.nama = allDrivers.get(i).nama;
                driverLocationAdapter.add(driver);
                LatLng currentLocation = new LatLng(lat,longi);

                if (i == 0) {
                    minLat = lat;
                    maxLat = lat;
                    minLong = longi;
                    maxLong = longi;
                } else {
                    if (lat < minLat) minLat = lat;
                    else if (lat > maxLat) maxLat = lat;
                    if (longi < minLong) minLong = longi;
                    else if (longi > maxLong) maxLong = longi;
                }

                mMap.addMarker(new MarkerOptions().position(currentLocation).title(driver.nama).snippet(getString(R.string.last_update) + " " + Utility.formatDateFromstring(Utility.dateDBLongFormat,Utility.LONG_DATE_TIME_FORMAT,lastUpdate)));

//        LatLng avgLocation = new LatLng(avgLat, avgLong);
                LatLng minLoc = new LatLng(minLat,minLong), maxLoc = new LatLng(maxLat, maxLong);
                mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(minLoc, maxLoc));
                mMap.setMinZoomPreference(5);
            }
        }


    }




    void mappingDrivers() {
        loadDriver = false;
        if (allDrivers.size() == 0) {
            nodata.setVisibility(View.VISIBLE);
        } else {
            for (int i=0;i<allDrivers.size();i++) {
                getBackgroundUpdate(allDrivers.get(i),i);
            }


        }
    }

    void mappingDriver(String driver) {
        loadDriver = false;
        if (allDrivers.size() == 0) {
            nodata.setVisibility(View.VISIBLE);
        } else {
            int findIndex = -1;
            for (int i=0;i<allDrivers.size() && findIndex == -1;i++) {
                if (allDrivers.get(i).name.equals(driver)) {
                    findIndex = i;
                }
            }
            if (findIndex != -1) {
                getBackgroundUpdate(allDrivers.get(findIndex),findIndex);
            }
        }
    }
    //API
    public void getDriver() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String vendorName = Utility.utility.getLoggedName(this);
        Call<DriverResponse> callDriver = api.getDriver("[[\"Driver\",\"vendor\",\"=\",\""+vendorName+"\"]]","" + (pager++ * limit));
        callDriver.enqueue(new Callback<DriverResponse>() {
            @Override
            public void onResponse(Call<DriverResponse> call, Response<DriverResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response, "")) {
                    DriverResponse driverResponses = response.body();
                    allDrivers.addAll(driverResponses.drivers);

                    mappingDrivers();
                }
            }

            @Override
            public void onFailure(Call<DriverResponse> call, Throwable throwable) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
                loading.setVisibility(View.INVISIBLE);
            }
        });
    }

    void getBackgroundUpdate(final Driver driver, final int markerIndex) {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String filters = "[[\"Driver Background Update\",\"driver\",\"=\",\"" + driver.name + "\"]]";
        Call<DriverBackgroundUpdateResponse> callbg = api.getBackgroundUpdate(filters);
        callbg.enqueue(new Callback<DriverBackgroundUpdateResponse>() {
            @Override
            public void onResponse(Call<DriverBackgroundUpdateResponse> call, Response<DriverBackgroundUpdateResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response, "")) {
                    isLoading = false;
                    if (!isLoading) {
                        loading.setVisibility(View.GONE);
                    }
                    List<DriverBackgroundUpdateData> bgData = response.body().data;
                    if (bgData.size() > 0) {
                        Driver temp = allDrivers.get(markerIndex);
                        try {
                            temp.lng = Double.valueOf(bgData.get(0).lo);
                            temp.lat = Double.valueOf(bgData.get(0).lat);
                            temp.last_update = bgData.get(0).last_update;
                            allDrivers.set(markerIndex, temp);
                            updateMarker();
                        } catch (NumberFormatException err) {

                        }

                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<DriverBackgroundUpdateResponse> call, Throwable t) {

            }
        });
    }

}


