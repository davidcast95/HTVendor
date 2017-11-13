package com.huang.android.logistic.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by David Wibisono on 8/24/2017.
 */

public class MyCookieJar implements CookieJar,Parcelable {

    private List<Cookie> cookies;

    public MyCookieJar(){

    }

    protected MyCookieJar(Parcel in) {
        cookies = new ArrayList<Cookie>();
        in.readList(cookies,null);
    }

    public static final Creator<MyCookieJar> CREATOR = new Creator<MyCookieJar>() {
        @Override
        public MyCookieJar createFromParcel(Parcel in) {
            return new MyCookieJar(in);
        }

        @Override
        public MyCookieJar[] newArray(int size) {
            return new MyCookieJar[size];
        }
    };

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        this.cookies =  cookies;
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        if (cookies != null)
            return cookies;
        return new ArrayList<Cookie>();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(cookies);
    }
}
