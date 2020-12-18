package com.ugps.alcoolougasolina;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class ANPServiceProvider {
    private static final String gasolinaId = "487*GASOLINA@COMUM";
    private static final String etanolId = "643*ETANOL@HIDRATADO";

    private final OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .callTimeout(3, TimeUnit.SECONDS)
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .cookieJar(new CookieJar());

    private final Handler handler = new Handler();

    private final Map<String, String> formData = new HashMap<>();

    public void getCaptcha(@NonNull final Callback<Bitmap> callback) {
        new Thread(new Runnable() {
            private Bitmap bitmap = null;

            @Override
            public void run() {
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

    public void getEstados(@NonNull final Callback<List<OptionModel<String>>> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<OptionModel<String>> items = new ArrayList<>();
                final ResponseBody body = request("http://preco.anp.gov.br/include/Resumo_Por_Estado_Index.asp");
                if (body != null) {
                    try {
                        Document document = Jsoup.parse(body.string());

                        Elements options = document
                                .getElementsByAttributeValue("name", "selEstado")
                                .first()
                                .getElementsByTag("option");

                        for (Element option : options) {
                            items.add(new OptionModel<>(option.val(), option.text()));
                        }

                        Elements hiddens = document
                                .getElementById("frmAberto")
                                .getElementsByAttributeValue("type", "hidden");

                        formData.clear();
                        for (Element hidden : hiddens) {
                            formData.put(hidden.attr("name"), hidden.val());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // dispatch to original thread
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!items.isEmpty()) {
                            items.add(0, new OptionModel<>("", "Selecione um Estado"));
                            callback.onResult(items);
                        } else {
                            callback.onError("Não foi possível carregar os estados");
                        }
                    }
                });
            }
        }).start();
    }

    public void getMunicipios(@NonNull String estado, @NonNull String captcha, @NonNull final Callback<List<OptionModel<PrecosModel>>> callback) {

        formData.put("selEstado", estado);
        formData.put("txtValor", captcha);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<OptionModel<PrecosModel>> items = new ArrayList<>();
                try {
                    Map<String, Double> precosGasolina = getPrecos(gasolinaId);
                    Map<String, Double> precosEtanol = getPrecos(etanolId);

                    TreeSet<String> sorted = new TreeSet<>(precosGasolina.keySet());

                    for (String cidade : sorted) {
                        Double pGasolina = precosGasolina.get(cidade);
                        Double pEtanol = precosEtanol.get(cidade);

                        if (pGasolina != null && pEtanol != null) {
                            items.add(new OptionModel<>(new PrecosModel(pGasolina, pEtanol), cidade));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // dispatch to original thread
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!items.isEmpty()) {
                            PrecosModel stub = new PrecosModel(0, 0);
                            items.add(0, new OptionModel<>(stub, "Selecione um Município"));
                            callback.onResult(items);
                        } else {
                            callback.onError("Não foi possível carregar os municípios");
                        }
                    }
                });
            }
        }).start();
    }

    private Map<String, Double> getPrecos(String combustivel) throws Exception {

        formData.put("selCombustivel", combustivel);

        Map<String, Double> cidadePreco = new HashMap<>();

        final ResponseBody body = request("http://preco.anp.gov.br/include/Resumo_Por_Estado_Municipio.asp", formData);
        if (body != null) {
            Elements rows = Jsoup
                    .parse(body.string())
                    .getElementById("frmAberto")
                    .getElementsByTag("tr");

            for (Element row : rows) {
                Elements cols = row.getElementsByTag("td");
                if (!cols.isEmpty()) {
                    Element firstCol = cols.first().getElementsByTag("a").first();
                    if (firstCol != null) {
                        String cidade = firstCol.text();
                        Double preco = Double.valueOf(cols.get(2).text().replaceAll(",", "."));
                        cidadePreco.put(cidade, preco);
                    }
                }
            }
        }

        return cidadePreco;
    }

    @Nullable
    private ResponseBody request(String url) {
        return request(url, null);
    }

    @Nullable
    private ResponseBody request(String url, @Nullable Map<String, String> formData) {
        OkHttpClient http = builder.build();
        Request.Builder req = new Request.Builder();

        if (formData != null) {
            FormBody.Builder formBuilder = new FormBody.Builder();

            for (Map.Entry<String, String> entry : formData.entrySet()) {
                formBuilder.add(entry.getKey(), entry.getValue());
            }

            req.post(formBuilder.build());
        }

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
