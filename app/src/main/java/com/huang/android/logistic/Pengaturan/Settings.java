package com.huang.android.logistic.Pengaturan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.huang.android.logistic.R;
import com.huang.android.logistic.SplashScreen;
import com.huang.android.logistic.Utility;

import java.util.ArrayList;
import java.util.List;

public class Settings extends Fragment {

    public Settings() {
        // Required empty public constructor
    }
    View v;
    private Spinner spinner;
    private Spinner spinner2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_settings, container, false);
        setHasOptionsMenu(true);
        List<String> categories = new ArrayList<String>();

        SharedPreferences prefs = this.getActivity().getSharedPreferences("LanguageSwitch", Context.MODE_PRIVATE);
        String language = prefs.getString("language","English");
        categories.add("English");
//        if(language.contentEquals("English")){
//            categories.add("Bahasa Indonesia");
//        }
//        else {
//            categories.add("Bahasa Indonesia");
//            categories.add("English");
//        }

        spinner = (Spinner) v.findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),
                R.layout.spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(dataAdapter);

        List<String> categories2 = new ArrayList<String>();
        categories2.add("5 Menit");
        categories2.add("30 Menit");
        categories2.add("1 Jam");
        categories2.add("2 Jam");
        spinner2 = (Spinner) v.findViewById(R.id.spinner2);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this.getActivity(),
                R.layout.spinner_item, categories2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner2.setAdapter(dataAdapter2);

        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_checklist, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_yes:
                String bahasa = spinner.getSelectedItem().toString();
                Utility.utility.savelanguage(getActivity(), bahasa);
                getActivity().startActivity(new Intent(getActivity().getApplicationContext(),SplashScreen.class));
                getActivity().finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

}
