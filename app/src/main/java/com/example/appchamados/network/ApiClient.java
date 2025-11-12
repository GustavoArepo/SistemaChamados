package com.example.appchamados.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    // ✅ URL BASE CORRIGIDA - com /api/ no final
    private static final String BASE_URL = "http://10.0.2.2:5257/api/"; // Para emulador Android

    //private static final String BASE_URL = "http://192.168.15.9:5000/api/"; // Para dispositivo físico

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Configurar logging para debug (opcional)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Configurar cliente HTTP
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging) // Adicionar logging
                    .connectTimeout(30, TimeUnit.SECONDS) // Timeout de conexão
                    .readTimeout(30, TimeUnit.SECONDS)    // Timeout de leitura
                    .writeTimeout(30, TimeUnit.SECONDS)   // Timeout de escrita
                    .build();

            // Configurar Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // ✅ Já inclui /api/
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Método para obter o serviço da API
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}