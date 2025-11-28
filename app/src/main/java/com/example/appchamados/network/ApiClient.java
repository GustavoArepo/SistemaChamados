package com.example.appchamados.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    // ========================================
    // CONFIGURAÇÃO DA URL BASE
    // ========================================

    // Para EMULADOR Android (localhost da máquina host)
    // private static final String BASE_URL = "http://10.0.2.2:5000/api/";

    // Para DISPOSITIVO FÍSICO na mesma rede (troque pelo IP da sua máquina)
    // private static final String BASE_URL = "http://192.168.1.100:5000/api/";

    // Para PRODUÇÃO (quando tiver hospedado)
    // private static final String BASE_URL = "https://seusite.com/api/";

    // ✅ URL ATUAL - ALTERE CONFORME NECESSÁRIO
    private static final String BASE_URL = "http://10.0.2.2:5000/api/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}