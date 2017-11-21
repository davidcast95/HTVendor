package com.huang.android.logistic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Model.Login.LoginUserPermission;
import com.huang.android.logistic.Model.Login.LoginUserPermissionResponse;
import com.huang.android.logistic.Model.Login.VendorLogin;
import com.huang.android.logistic.Model.MyCookieJar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.utility.getLanguage(this);
        setContentView(R.layout.activity_login);
        loading=(ProgressBar)findViewById(R.id.loading);
    }

    String user,pw;
    SharedPreferences mPrefs;
    ProgressBar loading;

    public void clicklogin(View v) {

        EditText username= (EditText)findViewById(R.id.username);
        user= username.getText().toString();
        EditText password= (EditText)findViewById(R.id.password);
        pw= password.getText().toString();

        if(user.isEmpty()||pw.isEmpty()){
            Context c = getApplicationContext();
            Toast.makeText(c,"username or password cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else {
            loading.setVisibility(View.VISIBLE);
            doLogin();
        }
    }


    public void doLogin() {
        final MyCookieJar cookieJar = new MyCookieJar();
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<VendorLogin> login = api.login(user,pw,"mobile");
        final Activity activity = this;
        login.enqueue(new Callback<VendorLogin>() {
            @Override
            public void onResponse(Call<VendorLogin> call, Response<VendorLogin> response) {
                if (response.code() == 200) {
                    VendorLogin vendorLogin = response.body();
                    Utility.utility.saveLoggedName(vendorLogin.full_name, activity);
                    Utility.utility.saveCookieJarToPreference(cookieJar, activity);
                    checkPermission();
                } else {
                    Toast.makeText(getApplicationContext(),"Username or password is invalid",Toast.LENGTH_SHORT).show();
                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<VendorLogin> call, Throwable throwable) {
                loading.setVisibility(View.GONE);
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });
    }


    void checkPermission() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Call<LoginUserPermissionResponse> loginUserPermissionResponseCall = api.loginPermission("[[\"User Permission\",\"allow\",\"=\",\"Vendor\"]]");
        final Activity thisActivity = this;
        loginUserPermissionResponseCall.enqueue(new Callback<LoginUserPermissionResponse>() {
            @Override
            public void onResponse(Call<LoginUserPermissionResponse> call, Response<LoginUserPermissionResponse> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    List<LoginUserPermission> data = response.body().data;
                    if (data.size() > 0) {
                        Utility.utility.saveLoggedName(data.get(0).value,thisActivity);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),"Username or password is invalid",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginUserPermissionResponse> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });
    }
}
