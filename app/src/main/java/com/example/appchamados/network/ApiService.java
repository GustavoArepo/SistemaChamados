package com.example.appchamados.network;

import com.example.appchamados.models.LoginRequest;
import com.example.appchamados.models.LoginResponse;
import com.example.appchamados.models.RegisterRequest;
import com.example.appchamados.models.RegisterResponse;
import com.example.appchamados.models.PerfilResponse;
import com.example.appchamados.models.AtualizacaoPerfilRequest;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // Autenticação
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    // Perfil
    @GET("api/usuario/perfil/{usuarioId}")
    Call<PerfilResponse> getPerfil(@Path("usuarioId") int usuarioId);

    @PUT("api/usuario/perfil/{usuarioId}")
    Call<RegisterResponse> atualizarPerfil(@Path("usuarioId") int usuarioId, @Body AtualizacaoPerfilRequest request);

}
