package com.logisticsmarketplace.android.vendor.Lihat_Profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.logisticsmarketplace.android.vendor.API.API;
import com.logisticsmarketplace.android.vendor.Model.MyCookieJar;
import com.logisticsmarketplace.android.vendor.Model.ProfilVendor.Profil;
import com.logisticsmarketplace.android.vendor.Model.ProfilVendor.ProfilResponse;
import com.logisticsmarketplace.android.vendor.R;
import com.logisticsmarketplace.android.vendor.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfile extends Fragment {

    TextView nameTextEdit, phoneTextEdit, emailTextEdit;

    public MyProfile() {
        // Required empty public constructor
    }
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_view_profile, container, false);
        nameTextEdit = (TextView)v.findViewById(R.id.name);
        phoneTextEdit = (TextView)v.findViewById(R.id.telephone);
        emailTextEdit = (TextView)v.findViewById(R.id.email);

        getProfile();
        return v;
    }

    //API Connectivity
    void getProfile() {
        MyCookieJar cookieJar = Utility.utility.getCookieFromPreference(this.getActivity());
        API api = Utility.utility.getAPIWithCookie(cookieJar);
        String name = Utility.utility.getLoggedName(this.getActivity());
        Call<ProfilResponse> profilResponseCall = api.getProfile("[[\"Vendor\",\"nama\",\"=\",\""+name+"\"]]");
        profilResponseCall.enqueue(new Callback<ProfilResponse>() {
            @Override
            public void onResponse(Call<ProfilResponse> call, Response<ProfilResponse> response) {
                if (Utility.utility.catchResponse(getActivity().getApplicationContext(), response)) {
                    ProfilResponse profilResponse = response.body();
                    List<Profil> profils = profilResponse.data;
                    if (profils.size() > 0) {
                        Profil profil = profils.get(0);
                        nameTextEdit.setText(profil.name);
                        phoneTextEdit.setText(profil.phone);
                        emailTextEdit.setText(profil.address);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfilResponse> call, Throwable t) {

            }
        });
    }

}
