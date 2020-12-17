package com.ugps.alcoolougasolina;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class ANPServiceProvider {
    private final OkHttpClient.Builder builder = new OkHttpClient.Builder().cookieJar(new CookieJar());
    private final Handler handler = new Handler();

    public void getCaptcha(@NonNull final Callback<Bitmap> callback) {
        new Thread(new Runnable() {
            private Bitmap bitmap = null;

            @Override
            public void run() {
                // just to get the cookies, ignore results for now
                request("http://preco.anp.gov.br/include/Resumo_Por_Estado_Index.asp");

                final ResponseBody body = request("http://preco.anp.gov.br/include/imagem.asp");
                if (body != null) {
                    bitmap = BitmapFactory.decodeStream(body.byteStream());
                }

                // dispatch to original thread
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap != null) {
                            callback.onResult(bitmap);
                        } else {
                            callback.onError("Não foi possível carregar o captcha");
                        }
                    }
                });
            }
        }).start();
    }

    private ResponseBody request(String url) {
        OkHttpClient http = builder.build();
        Request.Builder req = new Request.Builder();
        Call call = http.newCall(req.url(url).build());
        try {
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    interface Callback<T> {
        void onResult(T data);

        void onError(String error);
    }
}
