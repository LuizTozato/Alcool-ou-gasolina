package com.ugps.alcoolougasolina;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class CookieJar implements okhttp3.CookieJar {
    private List<Cookie> cookies;

    @Override
    public void saveFromResponse(@NonNull HttpUrl httpUrl, @NonNull List<Cookie> cookies) {
        this.cookies = cookies;
    }

    @NonNull
    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl httpUrl) {
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }
}
