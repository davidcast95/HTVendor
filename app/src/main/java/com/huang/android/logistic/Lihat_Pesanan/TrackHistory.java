package com.huang.android.logistic.Lihat_Pesanan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Lihat_Pesanan.Active.DetailActiveOrder;
import com.huang.android.logistic.Lihat_Pesanan.Base.DetailOrder;
import com.huang.android.logistic.Lihat_Pesanan.Done.DetailOrderDone;
import com.huang.android.logistic.Maps.DirectionFinder;
import com.huang.android.logistic.Maps.DirectionFinderListener;
import com.huang.android.logistic.Maps.LiveMaps;
import com.huang.android.logistic.Maps.Route;
import com.huang.android.logistic.Model.Driver.DriverBackgroundUpdateData;
import com.huang.android.logistic.Model.Driver.DriverBackgroundUpdateResponse;
import com.huang.android.logistic.Model.Driver.RouteData;
import com.huang.android.logistic.Model.Driver.RouteResponse;
import com.huang.android.logistic.Model.JobOrder.JobOrderStatus;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateData;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackHistory extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    private List<Marker> markers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    String joid, date,to,from, driver, from_type, to_type;
    ListView lv;
    TrackHistoryAdapter trackHistoryAdapter;
    ProgressBar loading;
    LinearLayout layout;
    TextView nodata;

    List<JobOrderUpdateData> jobOrderUpdateDataList = new ArrayList<>();

    List<RouteData> lastUpdateDriverRoute = null;

    ImageView currentLocation, pickUpOrigin, dropOrigin, pickUpDestination, dropDestination;
    TextView noUpdateLocation;
    public static TrackHistory instance = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        TrackHistory.instance = this;
        setTitle(R.string.tracking_history);
        setContentView(R.layout.activity_track_history);
        getDriverPosition();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        int locationProvide = 0;

        if (DetailOrder.jobOrderUpdates != null) {
            for (int i = 0; i < DetailOrder.jobOrderUpdates.size(); i++) {
                if (DetailOrder.jobOrderUpdates.get(i).longitude != null && DetailOrder.jobOrderUpdates.get(i).latitude != null)
                    if (!DetailOrder.jobOrderUpdates.get(i).longitude.equals("0.0") && !DetailOrder.jobOrderUpdates.get(i).equals("0.0"))
                        locationProvide++;

            }
        } else {
            DetailOrder.jobOrderUpdates = new ArrayList<>();
        }



        currentLocation = (ImageView)findViewById(R.id.current_location);

        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusOnDriver();
            }
        });

        noUpdateLocation = (TextView)findViewById(R.id.no_update_location);

        if (locationProvide > 0) {
            mapFragment.getView().setVisibility(View.VISIBLE);
            currentLocation.setVisibility(View.VISIBLE);
            noUpdateLocation.setVisibility(View.GONE);
        }
        else {
            mapFragment.getView().setVisibility(View.GONE);
            currentLocation.setVisibility(View.GONE);
            noUpdateLocation.setVisibility(View.VISIBLE);
        }
//        RelativeLayout mapHolder = (RelativeLayout)findViewById(R.name.mapholder);
//        mapHolder.setVisibility(View.GONE);

//        nodata = (TextView) findViewById(R.name.nodata);
//        nodata.setVisibility(View.GONE);
        loading=(ProgressBar)findViewById(R.id.loading);
        lv=(ListView) findViewById(R.id.historylist);
        layout=(LinearLayout)findViewById(R.id.layout);

        Intent intent = getIntent();
        joid = intent.getStringExtra("joid");
        from = intent.getStringExtra("origin");
        from_type = intent.getStringExtra("origin_type");
        to = intent.getStringExtra("destination");
        to_type = intent.getStringExtra("destination_type");
        driver = intent.getStringExtra("driver");

        TextView locationfrom = (TextView)findViewById(R.id.origin);
        locationfrom.setText(Html.fromHtml(from));
        TextView locationto = (TextView)findViewById(R.id.destination);
        locationto.setText(Html.fromHtml(to));

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

        pickUpOrigin = (ImageView)findViewById(R.id.pickup_origin_icon);
        dropOrigin = (ImageView)findViewById(R.id.drop_origin_icon);
        pickUpDestination = (ImageView)findViewById(R.id.pickup_destination_icon);
        dropDestination = (ImageView)findViewById(R.id.drop_destination_icon);
        if (from_type.equals("Pick Up")) {
            pickUpOrigin.setVisibility(View.VISIBLE);
        } else {
            dropOrigin.setVisibility(View.VISIBLE);
        }
        if (to_type.equals("Pick Up")) {
            pickUpDestination.setVisibility(View.VISIBLE);
        } else {
            dropDestination.setVisibility(View.VISIBLE);
        }

        layout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (from.equals("OrderActive")) {
//            // Inflate the menu; this adds items to the action bar if it is present.
//            getMenuInflater().inflate(R.menu.activity_track_history, menu);
//            return true;
//        } else {
//            return false;
//        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_maps:
                Intent intent = new Intent(this, LiveMaps.class);
                intent.putExtra("driver",driver);
                intent.putExtra("driver_name","");
                startActivity(intent);
                return true;
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                TrackHistory.instance = null;
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        drawJOUpdateMarker();

        trackHistoryAdapter = new TrackHistoryAdapter(getApplicationContext(),R.layout.activity_track_history_list, DetailOrder.jobOrderUpdates, mMap, markers);
        lv.setAdapter(trackHistoryAdapter);
        layout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);
    }

    void drawJOUpdateMarker() {
        boolean isPinned = false;
        double minLat = -1, maxLat = -1, minLong = -1, maxLong = -1;
        LatLng lastLocation = null;
        if (DetailOrder.jobOrderUpdates != null) {
            for (int i = 0; i < DetailOrder.jobOrderUpdates.size(); i++) {
                if (DetailOrder.jobOrderUpdates.get(i).longitude == null || DetailOrder.jobOrderUpdates.get(i).latitude == null) {
                    markers.add(null);
                } else {
                    if (DetailOrder.jobOrderUpdates.get(i).latitude.equals("0.0") || DetailOrder.jobOrderUpdates.get(i).equals("0.0")) {
                        markers.add(null);
                    } else {
                        Double lat = Double.valueOf(DetailOrder.jobOrderUpdates.get(i).latitude), longi = Double.valueOf(DetailOrder.jobOrderUpdates.get(i).longitude);
                        LatLng currentLocation = new LatLng(lat, longi);

                        if (lastLocation == null) lastLocation = currentLocation;

                        int icon = R.drawable.loc;
                        String statusIndex = DetailOrder.jobOrderUpdates.get(i).status.substring(0, 1);
                        switch (statusIndex) {
                            case "1":
                                icon = R.drawable.loc_1;
                                break;
                            case "2":
                                icon = R.drawable.loc_2;
                                break;
                            case "3":
                                icon = R.drawable.loc_3;
                                break;
                            case "4":
                                icon = R.drawable.loc_4;
                                break;
                            case "5":
                                icon = R.drawable.loc_5;
                                break;
                            case "6":
                                icon = R.drawable.loc_6;
                                break;
                        }

                        MarkerOptions marker = new MarkerOptions()
                                .position(currentLocation)
                                .title(DetailOrder.jobOrderUpdates.get(i).status)
                                .snippet(getString(R.string.last_update_on) + " " + Utility.formatDateFromstring(Utility.dateDBLongFormat, Utility.LONG_DATE_TIME_FORMAT, DetailOrder.jobOrderUpdates.get(i).time))
                                .icon(BitmapDescriptorFactory.fromResource(icon));
                        markers.add(mMap.addMarker(marker));
                        isPinned = true;
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

//                        if (i >= 1) {
//
////                            Double originLat = Double.valueOf(DetailOrder.jobOrderUpdates.get(i - 1).latitude), originLong = Double.valueOf(DetailOrder.jobOrderUpdates.get(i - 1).longitude);
////                            drawDirection(originLat, originLong, lat, longi);
//                        }
                    }

                }
            }
            if (DetailOrder.jobOrderUpdates.size() > 0) {
                int lastIndex = DetailOrder.jobOrderUpdates.size() - 1;
                drawDirection(DetailOrder.jobOrderUpdates.get(lastIndex).name, DetailOrder.jobOrderUpdates.get(0).name, DetailOrder.jobOrder.driver);
            }
        }
        if (!isPinned) {
            RelativeLayout mapHolder = (RelativeLayout)findViewById(R.id.mapholder);
            mapHolder.setVisibility(View.GONE);
        }

        //update driver mark road
        if (lastUpdateDriverRoute != null) {
            int lastIndex = lastUpdateDriverRoute.size() - 1;
            Double lat = Double.valueOf(lastUpdateDriverRoute.get(lastIndex).lat), longi = Double.valueOf(lastUpdateDriverRoute.get(lastIndex).lo);
            lastLocation = new LatLng(lat,longi);
            MarkerOptions marker = new MarkerOptions()
                    .position(lastLocation)
                    .title(getString(R.string.last_position))
                    .snippet(getString(R.string.last_update_on) + " " + Utility.formatDateFromstring(Utility.dateDBLongFormat,Utility.LONG_DATE_TIME_FORMAT,lastUpdateDriverRoute.get(lastIndex).creation))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.loc_truck));
            markers.add(mMap.addMarker(marker));
//
//            if (lat < minLat) minLat = lat;
//            else if (lat > maxLat) maxLat = lat;
//            if (longi < minLong) minLong = longi;
//            else if (longi > maxLong) maxLong = longi;
//

            //DRAW LAST JOU TO DRIVER POSITION
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.rgb(88,114,47)).
                    width(10);
            for (int i=0;i<lastUpdateDriverRoute.size();i++) {
                longi = Double.valueOf(lastUpdateDriverRoute.get(i).lo);
                lat = Double.valueOf(lastUpdateDriverRoute.get(i).lat);

                polylineOptions.add(new LatLng(lat,longi));
            }

            polylinePaths.add(mMap.addPolyline(polylineOptions));

        } else {
            if (DetailOrder.jobOrderUpdates.size() > 0 && lastLocation != null) {
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                        lastLocation, mMap.getCameraPosition().zoom);
                mMap.animateCamera(location);
                int lastIndex = markers.size() - 1;
                Marker marker = markers.get(lastIndex);
                if (marker != null)
                    marker.showInfoWindow();
            }
        }


//        LatLng minLoc = new LatLng(minLat,minLong), maxLoc = new LatLng(maxLat, maxLong);
//        mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(minLoc, maxLoc));
        mMap.setMinZoomPreference(5f);

        focusOnDriver();



    }

    void drawDirection(String startjou, String endjou, String driver) {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<RouteResponse> callRoute = api.getRoute(startjou,endjou,driver);
        callRoute.enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(),response,"")) {
                    RouteResponse routeResponse = response.body();
                    if (routeResponse != null) {
                        List<RouteData> routeList = routeResponse.data;
                        PolylineOptions polylineOptions = new PolylineOptions().
                                geodesic(true).
                                color(Color.rgb(88,114,47)).
                                width(10);
                        if (routeList != null) {
                            for (int i = 0; i < routeList.size(); i++) {
                                Double lo = Double.valueOf(routeList.get(i).lo);
                                Double lat = Double.valueOf(routeList.get(i).lat);

                                polylineOptions.add(new LatLng(lat, lo));
                            }

                            polylinePaths.add(mMap.addPolyline(polylineOptions));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {

            }
        });
    }

    void drawDirection(Double originLat, Double originLong, Double destinationLat, Double destinationLong) {
        DirectionFinder directionFinder = new DirectionFinder(new DirectionFinderListener() {
            @Override
            public void onDirectionFinderStart() {

            }

            @Override
            public void onDirectionFinderSuccess(List<Route> routes) {
                for (Route route : routes) {

                    PolylineOptions polylineOptions = new PolylineOptions().
                            geodesic(true).
                            color(Color.rgb(88,114,47)).
                            width(10);

                    for (int i = 0; i < route.points.size(); i++)
                        polylineOptions.add(route.points.get(i));

                    polylinePaths.add(mMap.addPolyline(polylineOptions));
                }
            }
        },originLat + "," + originLong,destinationLat + "," + destinationLong);
        try {
            directionFinder.execute();
        } catch (UnsupportedEncodingException err) {

        }
    }

    void focusOnDriver() {
        if (lastUpdateDriverRoute != null) {
            int lastIndex = lastUpdateDriverRoute.size() - 1;
            Double lat = Double.valueOf(lastUpdateDriverRoute.get(lastIndex).lat), longi = Double.valueOf(lastUpdateDriverRoute.get(lastIndex).lo);
            LatLng lastLocation = new LatLng(lat,longi);
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    lastLocation, mMap.getCameraPosition().zoom);
            mMap.animateCamera(location);
            lastIndex = markers.size()-1;
            markers.get(lastIndex).showInfoWindow();
        }
    }

    public void refreshDriverPosition(String driver) {
        if (driver.equals(DetailOrder.jobOrder.driver)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getDriverPosition();
                }
            }, 2000);
        }
    }

    //API
    void getDriverPosition() {
        if (DetailOrder.jobOrder.status.equals(JobOrderStatus.DONE) || DetailOrder.jobOrder.status.equals(JobOrderStatus.REJECTED)) return;
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);

        if (DetailOrder.jobOrderUpdates.size() > 0) {
            Call<RouteResponse> callLastRoute = api.getLastRoute(DetailOrder.jobOrderUpdates.get(0).name, DetailOrder.jobOrder.driver);
            callLastRoute.enqueue(new Callback<RouteResponse>() {
                @Override
                public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                    if (Utility.utility.catchResponse(getApplicationContext(), response, "")) {
                        RouteResponse routeResponse = response.body();
                        if (routeResponse != null) {
                            if (routeResponse.data.size() > 0) {
                                lastUpdateDriverRoute = routeResponse.data;
                                drawJOUpdateMarker();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<RouteResponse> call, Throwable t) {

                }
            });
        }
    }
}
