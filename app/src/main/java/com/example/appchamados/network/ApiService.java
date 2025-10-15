package com.example.appchamados.network;

import com.example.appchamados.models.LoginRequest;
import com.example.appchamados.models.LoginResponse;
import com.example.appchamados.models.RegisterRequest;
import com.example.appchamados.models.RegisterResponse;

public class ApiService {
    @GET("api/usuario/perfil/{usuarioId}")
    Call<PerfilResponse> getPerfil(@Path("usuarioId") int usuarioId);

    @PUT("api/usuario/perfil/{usuarioId}")
    Call<RegisterResponse> atualizarPerfil(@Path("usuarioId") int usuarioId, @Body AtualizacaoPerfilRequest request);

}
