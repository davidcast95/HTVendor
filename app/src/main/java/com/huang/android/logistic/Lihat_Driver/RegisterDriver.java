package com.huang.android.logistic.Lihat_Driver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.huang.android.logistic.API.API;
import com.huang.android.logistic.Model.Driver.Driver;
import com.huang.android.logistic.Model.MyCookieJar;
import com.huang.android.logistic.R;
import com.huang.android.logistic.Utility;
import com.google.gson.Gson;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by davidwibisono on 9/13/17.
 */

public class RegisterDriver extends AppCompatActivity {

    ProgressBar loading;
    EditText nameET, emailET, phoneET, addressET, passwordET;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.utility.getLanguage(this);
        super.onCreate(savedInstanceState);
        setTitle("Register Driver");
        setContentView(R.layout.activity_register_driver);

        //binding to layout
        loading=(ProgressBar)findViewById(R.id.loading);
        nameET=(EditText)findViewById(R.id.reg_driver_name);
        emailET=(EditText)findViewById(R.id.reg_driver_email);
        phoneET=(EditText)findViewById(R.id.reg_driver_phone);
        addressET=(EditText)findViewById(R.id.reg_driver_address);
        registerButton=(Button)findViewById(R.id.reg_driver_button);
        passwordET=(EditText)findViewById(R.id.reg_driver_password);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStateUI();
            }
        });

        updateStateUI();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.detail_sopir_titlebar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void updateStateUI() {
        loading.setVisibility(View.GONE);
        String name = nameET.getText().toString(),
                phone = phoneET.getText().toString(),
                address = addressET.getText().toString(),
                email = emailET.getText().toString(),
                password = passwordET.getText().toString();
        Boolean valid = true;
        if (name.equals("")) {
            Toast.makeText(getApplicationContext(),"name is required",Toast.LENGTH_SHORT).show();
            valid=false;
        }
        if (phone.equals("")) {
            Toast.makeText(getApplicationContext(),"phone is required",Toast.LENGTH_SHORT).show();
            valid=false;
        }
        if (address.equals("")) {
            Toast.makeText(getApplicationContext(),"address is required",Toast.LENGTH_SHORT).show();
            valid=false;
        }
        if (email.equals("")) {
            Toast.makeText(getApplicationContext(),"email is required",Toast.LENGTH_SHORT).show();
            valid=false;
        }
        if (password.equals("")) {
            Toast.makeText(getApplicationContext(),"password is required",Toast.LENGTH_SHORT).show();
            valid=false;
        }
        if (valid) {
            registerDriver(name,phone,address,email,password);
        }
    }



    //API
    void registerDriver(String name, String phone, String address, String email, String password) {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this);
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        Driver newDriver = new Driver();
        newDriver.nama = name;
        newDriver.address = address;
        newDriver.email = email;
        newDriver.phone = phone;
        newDriver.vendor = Utility.utility.getLoggedName(this);
        newDriver.password = password;
        Gson gson = new Gson();
        String json = gson.toJson(newDriver);
        Call<JSONObject> callRegisterDriver = api.registerDriver(newDriver);
        callRegisterDriver.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (Utility.utility.catchResponse(getApplicationContext(), response)) {
                    Toast.makeText(getApplicationContext(), "New driver has been registered", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Utility.utility.showConnectivityUnstable(getApplicationContext());
            }
        });

    }
}
