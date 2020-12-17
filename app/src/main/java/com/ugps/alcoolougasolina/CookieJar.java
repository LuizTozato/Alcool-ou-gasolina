package com.ugps.alcoolougasolina;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class CookieJar implements okhttp3.CookieJar {
    private List<Cookie> cookies;

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> cookies) {
        this.cookies = cookies;
    }

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }
}
