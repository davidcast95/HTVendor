package com.huang.android.logistic.API;

import com.huang.android.logistic.Model.Driver.Driver;
import com.huang.android.logistic.Model.Driver.DriverResponse;
import com.huang.android.logistic.Model.History.HistoryResponse;
import com.huang.android.logistic.Model.JobOrder.JobOrderData;
import com.huang.android.logistic.Model.JobOrder.JobOrderResponse;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateCreation;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateData;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateResponse;
import com.huang.android.logistic.Model.Login.LoginUserPermissionResponse;
import com.huang.android.logistic.Model.Login.VendorLogin;
import com.huang.android.logistic.Model.ProfilVendor.ProfilResponse;
import com.huang.android.logistic.Model.Truck.TruckResponse;
import com.huang.android.logistic.Model.Vendor.VendorContactPersonData;
import com.huang.android.logistic.Model.Vendor.VendorContactPersonResponse;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface API {
    //    public final String BASE_URL = "http://172.104.166.12";
    public final String BASE_URL = "http://172.104.163.118";

    //TRUCK
    @GET("/api/resource/Truck?fields=[\"name\",\"nopol\",\"status\",\"type\",\"tahun\",\"volume\",\"merek\",\"note\",\"vendor\",\"lambung\"]")
    Call<TruckResponse> getTruck(@Query("filters") String filters);

    //JOB ORDER UPDATE
    @GET("/api/resource/Job Order Update?fields=[\"name\",\"waktu\",\"lo\",\"lat\",\"note\",\"job_order\",\"docstatus\",\"status\",\"vendor\",\"principle\"]")
    Call<JobOrderUpdateResponse> getJOUpdate(@Query("filters") String filters, @Query("limit_page_length") String limit);
    @POST("/api/resource/Job Order Update")
    Call<JobOrderUpdateCreation> insertUpdateJO(@Body JobOrderUpdateData jobOrderUpdateData);

    @POST("/api/method/logistic_marketplace.job_order.image")
    Call<ResponseBody> uploadImage(@Body HashMap<String,String> data);


    //DRIVER
    @GET("/api/resource/Driver?fields=[\"name\",\"nama\",\"email\",\"address\",\"phone\",\"status\"]")
    Call<DriverResponse> getDriver(@Query("filters") String filters);
    @POST("/api/resource/Driver")
    Call<JSONObject> registerDriver(@Body Driver newDriver);
    @PUT("/api/resource/Driver/{id}")
    Call<JSONObject> updateDriver(@Path("id") String id, @Body HashMap<String , String> change);

    //JOB ORDER
    @PUT("/api/resource/Job Order/{id}")
    Call<org.json.JSONObject> updateJobOrder(@Path("id") String id, @Body HashMap<String , String> change);
    @GET("/api/resource/Job Order?fields=[\"reference\",\"status\",\"name\",\"driver_nama\",\"driver_phone\", \"principle\",\"vendor\",\"pick_location\",\"delivery_location\",\"nama_principle_cp\",\"telp_principle_cp\",\"nama_vendor_cp\",\"telp_vendor_cp\",\"pick_date\",\"expected_delivery\",\"goods_information\",\"notes\",\"accept_date\",\"suggest_truck_type\",\"strict\",\"estimate_volume\",\"truck\",\"truck_lambung\",\"truck_type\",\"truck_volume\",\"driver\",\"kota_pengambilan\",\"alamat_pengambilan\",\"kode_distributor_pengambilan\",\"nama_gudang_pengambilan\",\"kota_pengiriman\",\"alamat_pengiriman\",\"kode_distributor_pengiriman\",\"nama_gudang_pengiriman\"]")
    Call<JobOrderResponse> getJobOrder(@Query("filters") String filters);
    @POST("/api/resource/Job Order")
    Call<org.json.JSONObject> submitJobOrder(@Body JobOrderData data);

    //VENDOR
    @POST("/api/resource/Vendor Contact Person")
    Call<org.json.JSONObject> insertVendorCP(@Body VendorContactPersonData data);
    @GET("/api/resource/Vendor Contact Person?fields=[\"vendor\",\"nama\",\"telp\"]")
    Call<VendorContactPersonResponse> getPrincipleCP(@Query("filters") String filters);

    //old
    @GET("/api/resource/Vendor?fields=[\"nama\",\"telp\",\"alamat\",\"email\"]")
    Call<ProfilResponse> getProfile(@Query("filters") String filter);


    @GET("/api/method/logistic.job_order.history")
    Call<HistoryResponse> getHistory(@Query("jaid") String jaid);

    @GET("/api/method/login")
    Call<VendorLogin> login(@Query("usr") String usr, @Query("pwd") String pwd, @Query("device") String device);
    @GET("/api/resource/User Permission/?fields=[\"for_value\",\"allow\"]")
    Call<LoginUserPermissionResponse> loginPermission(@Query("filters") String filters);



    @GET("/api/method/logistic.job_order.list")
    Call<JobOrderResponse> getJobOrderList(@Query("type") String type, @Query("typeid") String typeid, @Query("status") String status);
}
