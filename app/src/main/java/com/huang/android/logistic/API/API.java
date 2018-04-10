package com.huang.android.logistic.API;

import com.huang.android.logistic.Model.APILogData;
import com.huang.android.logistic.Model.Communication.CommunicationCreation;
import com.huang.android.logistic.Model.Communication.CommunicationData;
import com.huang.android.logistic.Model.Communication.CommunicationResponse;
import com.huang.android.logistic.Model.Driver.AllDriverResponse;
import com.huang.android.logistic.Model.Driver.Driver;
import com.huang.android.logistic.Model.Driver.DriverBackgroundUpdateResponse;
import com.huang.android.logistic.Model.Driver.DriverResponse;
import com.huang.android.logistic.Model.History.HistoryResponse;
import com.huang.android.logistic.Model.JobOrder.GetJobOrderResponse;
import com.huang.android.logistic.Model.JobOrder.JobOrderData;
import com.huang.android.logistic.Model.JobOrder.JobOrderMetaDataResponse;
import com.huang.android.logistic.Model.JobOrder.JobOrderResponse;
import com.huang.android.logistic.Model.JobOrder.JobOrderSingleResponse;
import com.huang.android.logistic.Model.JobOrderRoute.JobOrderRouteResponse;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateCreation;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateData;
import com.huang.android.logistic.Model.JobOrderUpdate.JobOrderUpdateResponse;
import com.huang.android.logistic.Model.JobOrderUpdateImage.JobOrderUpdateImageResponse;
import com.huang.android.logistic.Model.Login.LoginUserPermissionResponse;
import com.huang.android.logistic.Model.Login.VendorLogin;
import com.huang.android.logistic.Model.ProfilVendor.ProfilResponse;
import com.huang.android.logistic.Model.Truck.TruckResponse;
import com.huang.android.logistic.Model.User.UserResponse;
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
import retrofit2.http.Url;


public interface API {
//    public final String BASE_URL = "http://172.104.49.99";
//    public final String BASE_URL = "http://172.104.163.118";
    public final String BASE_URL = "http://system.digitruk.com";

    //API Log
    @POST("/api/resource/API Log")
    Call<JSONObject> sendAPILog(@Body APILogData apiLogData);

    //USER
    @GET("/api/method/logistic_marketplace.api.validate_email")
    Call<UserResponse> validateEmail(@Query("email") String email);

    //TRUCK
    @GET("/api/resource/Truck?fields=[\"name\",\"nopol\",\"status\",\"type\",\"tahun\",\"volume\",\"merek\",\"note\",\"vendor\",\"lambung\"]")
    Call<TruckResponse> getTruck(@Query("filters") String filters, @Query("limit_start") String start);

    //JOB ORDER UPDATE
    @GET("/api/method/logistic_marketplace.api.get_job_order_update")
    Call<JobOrderUpdateResponse> getJOUpdate(@Query("job_order") String job_order);
    @GET("/api/resource/Job Order/{path}")
    Call<JobOrderSingleResponse> getSpecifiedJO(@Path("path") String name);
    @POST("/api/resource/Job Order Update")
    Call<JobOrderUpdateCreation> insertUpdateJO(@Body JobOrderUpdateData jobOrderUpdateData);

    @POST("/api/method/logistic_marketplace.job_order.image")
    Call<ResponseBody> uploadImage(@Body HashMap<String,String> data);


    //DRIVER
    @GET("/api/method/logistic_marketplace.api.get_driver")
    Call<DriverResponse> getDriver(@Query("vendor") String vendor,@Query("ref") String ref , @Query("start") String start);

    @GET("/api/method/logistic_marketplace.api.get_driver?status=Tersedia")
    Call<DriverResponse> getActiveDriver(@Query("vendor") String vendor,@Query("ref") String ref , @Query("start") String start);
    @GET("/api/resource/Driver?fields=[\"name\",\"nama\",\"email\",\"address\",\"phone\",\"status\"]&limit_page_length=100000")
    Call<AllDriverResponse> getAllDriver(@Query("filters") String filters);
    @POST("/api/resource/Driver")
    Call<JSONObject> registerDriver(@Body Driver newDriver);
    @PUT("/api/resource/Driver/{id}")
    Call<JSONObject> updateDriver(@Path("id") String id, @Body HashMap<String , String> change);
    @GET("/api/resource/Driver Background Update?fields=[\"lo\",\"lat\",\"last_update\"]&limit_page_length=1")
    Call<DriverBackgroundUpdateResponse> getBackgroundUpdate(@Query("filters") String filters);

    //COMMUNICATION
    @GET("/api/resource/Communication?fields=[\"creation\",\"sender\",\"sender_full_name\",\"content\"]&limit_page_length=1000")
    Call<CommunicationResponse> getComment(@Query("filters") String filters);
    @POST("/api/resource/Communication")
    Call<CommunicationCreation> insertComment(@Body CommunicationData communicationData);


    //JOB ORDER UPDATE IMAGE
    @GET
    Call<ResponseBody> getImage(@Url String image_link);
    @GET("/api/method/logistic_marketplace.api.get_image_jo_update")
    Call<JobOrderUpdateImageResponse> getJOUpdateImage(@Query("jod_name") String jod_name);

    //JOB ORDER
    @PUT("/api/resource/Job Order/{id}")
    Call<org.json.JSONObject> updateJobOrder(@Path("id") String id, @Body HashMap<String , String> change);
//    @GET("/api/resource/Job Order?fields=[\"modified\",\"reference\",\"status\",\"name\",\"driver_nama\",\"driver_phone\", \"principle\",\"vendor\",\"pick_location\",\"delivery_location\",\"nama_principle_cp\",\"telp_principle_cp\",\"nama_vendor_cp\",\"telp_vendor_cp\",\"pick_date\",\"expected_delivery\",\"goods_information\",\"notes\",\"accept_date\",\"suggest_truck_type\",\"strict\",\"estimate_volume\",\"truck\",\"truck_lambung\",\"truck_type\",\"truck_volume\",\"driver\",\"kota_pengambilan\",\"alamat_pengambilan\",\"kode_distributor_pengambilan\",\"nama_gudang_pengambilan\",\"kota_pengiriman\",\"alamat_pengiriman\",\"kode_distributor_pengiriman\",\"nama_gudang_pengiriman\"]")
//    Call<JobOrderResponse> getJobOrder(@Query("filters") String filters,@Query("limit_start") String start);
    @GET("/api/method/logistic_marketplace.api.get_job_order")
    Call<GetJobOrderResponse> getJobOrder(@Query("status") String status, @Query("vendor") String vendor, @Query("ref") String ref, @Query("start") String start);
    @GET("/api/method/logistic_marketplace.api.get_job_order_count?role=vendor")
    Call<JobOrderMetaDataResponse> getJobOrderCount(@Query("id") String id);
    @GET("/api/method/logistic_marketplace.api.get_job_order_by")
    Call<GetJobOrderResponse> getJobOrderBy(@Query("name") String name);

    @POST("/api/resource/Job Order")
    Call<org.json.JSONObject> submitJobOrder(@Body JobOrderData data);

    //JOB ORDER ROUTE
    @GET("/api/resource/Job Order Route?fields=[\"location\",\"warehouse_name\",\"distributor_code\",\"city\",\"address\",\"contact\",\"nama\",\"phone\",\"type\",\"item_info\",\"remark\",\"order_index\",\"job_order\"]&limit_page_length=1000")
    Call<JobOrderRouteResponse> getJobOrderRoute(@Query("filters") String filters);

    //VENDOR
    @POST("/api/resource/Vendor Contact Person")
    Call<org.json.JSONObject> insertVendorCP(@Body VendorContactPersonData data);
    @GET("/api/resource/Vendor Contact Person?fields=[\"name\",\"vendor\",\"nama\",\"telp\"]")
    Call<VendorContactPersonResponse> getPrincipleCP(@Query("filters") String filters);

    @GET("/api/method/logistic_marketplace.api.get_user")
    Call<ProfilResponse> getProfile(@Query("vendor") String vendor);


    @GET("/api/method/logistic.job_order.history")
    Call<HistoryResponse> getHistory(@Query("jaid") String jaid);

    @GET("/api/method/login")
    Call<VendorLogin> login(@Query("usr") String usr, @Query("pwd") String pwd, @Query("device") String device);
    @GET("/api/resource/User Permission/?fields=[\"for_value\",\"allow\"]")
    Call<LoginUserPermissionResponse> loginPermission(@Query("filters") String filters);



    @GET("/api/method/logistic.job_order.list")
    Call<JobOrderResponse> getJobOrderList(@Query("type") String type, @Query("typeid") String typeid, @Query("status") String status);
}
