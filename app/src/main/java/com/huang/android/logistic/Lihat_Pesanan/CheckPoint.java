package com.huang.android.logistic.Lihat_Pesanan;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Maps.DirectionFinderListener;
import com.huang.android.logistic.Maps.Route;
import com.huang.android.logistic.Model.Driver.DriverStatus;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateCreation;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateData;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckPoint extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    int REQUEST_CAMERA = 1;
    Double longi = 0.0,lat = 0.0;

    private Spinner spinner;
    private GoogleMap mMap;
    ProgressBar loading;
    EditText notesEditText;
    GridView gridView;
    CheckPointAdapter checkPointAdapter;
    List<String> categories = new ArrayList<String>();
    String joid, principle, vendor, driver;
    Uri imageUri;
    List<Bitmap> bufferListImages;

    Button klik;

    GoogleApiClient mGoogleApiClient;
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        super.onCreate(savedInstanceState);
        setTitle(R.string.checkpoint);
        setContentView(R.layout.activity_check_point);

        loading=(ProgressBar)findViewById(R.id.loading);
        loading.setVisibility(View.GONE);
        gridView = (GridView) findViewById(R.id.photo);

        notesEditText = (EditText)findViewById(R.id.notes);
        Intent intent = getIntent();
        joid = intent.getStringExtra("joid");
        principle = intent.getStringExtra("principle");
        vendor = intent.getStringExtra("vendor");
        driver = intent.getStringExtra("driver");

        getNextStage();
        bufferListImages = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        klik = (Button)findViewById(R.id.addphoto);
        klik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takephoto();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void takephoto(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA);
            }
        } else {
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageUri = generateTimeStampPhotoFileUri();
            camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(camera,1);
        }

    }

    private Uri generateTimeStampPhotoFileUri() {

        Uri photoFileUri = null;
        File outputDir = getPhotoDirectory();
        if (outputDir != null) {

            File photoFile = new File(outputDir, System.currentTimeMillis()
                    + ".jpg");
            photoFileUri = Uri.fromFile(photoFile);
        }
        return photoFileUri;
    }

    private File getPhotoDirectory() {
        File outputDir = null;
        File photoDir = getCacheDir(); //Environment.getDataDirectory();
        outputDir = new File(photoDir, getString(R.string.app_name));
        if (!outputDir.exists())
            if (!outputDir.mkdirs()) {
                Toast.makeText(
                        this,
                        "Failed to create directory "
                                + outputDir.getAbsolutePath(),
                        Toast.LENGTH_SHORT).show();
                outputDir = null;
            }
        return outputDir;
    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent,
                                View view,
                                int position, long id){
            int idi= (int) id;
            Log.e("ID HAPUS",id+"");
            Log.e("ID HAPUS",idi+"");
            dialog(idi);
        }
    };

    public void dialog(int id) {
        final int idhapus=id;
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm)
                .setMessage(R.string.confirmdeletfoto)
                .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        bufferListImages.remove(idhapus);
                        Log.e("SIZE", bufferListImages.size()+"");
                        checkPointAdapter = new CheckPointAdapter(getApplicationContext(),R.layout.activity_check_point_photo_list, bufferListImages);
                        gridView.setAdapter(checkPointAdapter);
                        gridView.setOnItemClickListener(onListClick);
                        klik.setVisibility(View.VISIBLE);

                    }
                })
                .setNegativeButton(R.string.popup_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null){


//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            image1.setImageBitmap(imageBitmap);
//            bufferListImages.add(imageBitmap);

//            if(bufferListImages.size()==5){
//                klik.setVisibility(View.INVISIBLE);
//            }

        }
        if (requestCode == 1) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                bufferListImages.add(bitmap);
                checkPointAdapter = new CheckPointAdapter(getApplicationContext(), R.layout.activity_check_point_photo_list, bufferListImages);
                gridView.setAdapter(checkPointAdapter);
                gridView.setOnItemClickListener(onListClick);
            } catch (IOException eio) {

            }
        }
    }

    void updateStateUI() {
        String notes = notesEditText.getText().toString();
//        if (notes.equals("")) {
//            Toast.makeText(getApplicationContext(),"Notes cannot be empty",Toast.LENGTH_SHORT).show();
//        } else {
//
//        }
        updateStatus();
    }
    //API
    void updateStatus() {
        klik.setEnabled(false);

        loading.setVisibility(View.VISIBLE);
        klik.setEnabled(false);
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
        jobOrderUpdateData.principle = principle;
        jobOrderUpdateData.vendor = vendor;
        String status = spinner.getSelectedItem().toString();
        jobOrderUpdateData.status = status;

        if (status.equals("6. Pekerjaan Selesai")) {
            updateJOStatus();
        }
        String a = new Gson().toJson(jobOrderUpdateData);
        Call<JobOrderUpdateCreation> callInsertUpdateJO = api.insertUpdateJO(jobOrderUpdateData);
        callInsertUpdateJO.enqueue(new Callback<JobOrderUpdateCreation>() {
            @Override
            public void onResponse(Call<JobOrderUpdateCreation> call, Response<JobOrderUpdateCreation> response) {
                loading.setVisibility(View.GONE);
                klik.setEnabled(true);
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {

                    String updateJOID = response.body().data.id;
                    uploadImage(updateJOID);

                }
            }

            @Override
            public void onFailure(Call<JobOrderUpdateCreation> call, Throwable t) {
                loading.setVisibility(View.GONE);
                klik.setEnabled(true);
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });
    }

    public String convertImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        Log.e("BASE 64",encoded);
        return encoded;
    }

    void uploadImage(final String updateJOID) {
        if (bufferListImages.size() > 0) {
            MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
            API api = Utility.utility.getAPIWithCookie(cookieJar);
            String base64 = convertImage(bufferListImages.get(0));
            HashMap<String,String> data = new HashMap<>();
            data.put("job_order_update",updateJOID);
            data.put("filedata",base64);
            Call<ResponseBody> callUploadImage = api.uploadImage(data);
            callUploadImage.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    bufferListImages.remove(0);
                    uploadImage(updateJOID);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    loading.setVisibility(View.GONE);
                    setResult(RESULT_CANCELED);
                    finish();

                }
            });


        } else {
            Toast.makeText(getApplicationContext(), joid + " has been updated",Toast.LENGTH_SHORT);
            setResult(RESULT_OK);
            finish();
        }
    }

    void updateJOStatus() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        HashMap<String,String> statusJSON = new HashMap<>();
        statusJSON.put("status","Selesai");
        Call<JSONObject> callUpdateJO = api.updateJobOrder(joid, statusJSON);
        callUpdateJO.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    updateDriver();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });
    }
    void updateDriver() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        HashMap<String,String> statusJSON = new HashMap<>();
        statusJSON.put("status", DriverStatus.AVAILABLE);
        String a = new Gson().toJson(statusJSON);
        Call<JSONObject> callUpdateJO = api.updateDriver(driver, statusJSON);
        callUpdateJO.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {

                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });
    }


    public void getNextStage() {
        categories.add("1. Menuju ke Lokasi Muat");
        categories.add("2. Tiba di Lokasi Muat");
        categories.add("3. Proses Muat Selesai");
        categories.add("4. Tiba di Lokasi Bongkar");
        categories.add("5. Proses Bongkar Selesai");
        categories.add("6. Pekerjaan Selesai");
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (mGoogleApiClient == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
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
//                        mMap.setMyLocationEnabled(true);
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


    void updateBackground() {

    }


    LocationRequest mLocationRequest;
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
