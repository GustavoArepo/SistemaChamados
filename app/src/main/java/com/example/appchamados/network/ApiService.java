package com.example.appchamados.network;

import com.example.appchamados.models.Chamado;
import com.example.appchamados.models.ChamadoDetailResponse;
import com.example.appchamados.models.ChamadosResponse;
import com.example.appchamados.models.FCMTokenRequest;
import com.example.appchamados.models.ImagemResponse;
import com.example.appchamados.models.ImagensResponse;
import com.example.appchamados.models.LoginRequest;
import com.example.appchamados.models.LoginResponse;
import com.example.appchamados.models.Mensagem;
import com.example.appchamados.models.MensagemRequest;
import com.example.appchamados.models.MensagemResponse;
import com.example.appchamados.models.MensagensResponse;
import com.example.appchamados.models.RegisterRequest;
import com.example.appchamados.models.RegisterResponse;
import com.example.appchamados.models.PerfilResponse;
import com.example.appchamados.models.AtualizacaoPerfilRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ========================================
    // AUTENTICAÇÃO
    // ========================================
    @POST("authapi/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("authapi/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    // ========================================
    // PERFIL
    // ========================================
    @GET("usuarioapi/perfil/{usuarioId}")
    Call<PerfilResponse> getPerfil(@Path("usuarioId") int usuarioId);

    @PUT("usuarioapi/perfil/{usuarioId}")
    Call<RegisterResponse> atualizarPerfil(@Path("usuarioId") int usuarioId, @Body AtualizacaoPerfilRequest request);

    // ========================================
    // CHAMADOS
    // ========================================
    @GET("chamadosapi/usuario/{usuarioId}")
    Call<ChamadosResponse> getChamadosPorUsuario(@Path("usuarioId") int usuarioId);

    @POST("chamadosapi")
    Call<RegisterResponse> criarChamado(@Body Chamado chamado);

    @PUT("chamadosapi/{chamadoId}")
    Call<RegisterResponse> atualizarChamado(@Path("chamadoId") int chamadoId, @Body Chamado chamado);

    @DELETE("chamadosapi/{chamadoId}")
    Call<RegisterResponse> deletarChamado(@Path("chamadoId") int chamadoId);

    @GET("chamadosapi/{id}")
    Call<ChamadoDetailResponse> getChamadoDetail(@Path("id") int chamadoId);

    // ========================================
    // MENSAGENS
    // ========================================
    @GET("mensagensapi/chamado/{chamadoId}")
    Call<MensagensResponse> getMensagensPorChamado(@Path("chamadoId") int chamadoId);

    @POST("mensagensapi")
    Call<MensagemResponse> enviarMensagem(@Body MensagemRequest request);

    @PUT("mensagensapi/{id}/ler")
    Call<RegisterResponse> marcarMensagemComoLida(@Path("id") int mensagemId);

    @GET("mensagensapi/chamado/{chamadoId}/nao-lidas")
    Call<MensagensResponse> getMensagensNaoLidas(@Path("chamadoId") int chamadoId, @Query("usuarioId") int usuarioId);

    // ========================================
    // FCM TOKEN
    // ========================================
    @POST("usuarioapi/fcm-token")
    Call<RegisterResponse> salvarFCMToken(@Body FCMTokenRequest request);

    // ========================================
    // IMAGENS
    // ========================================
    @Multipart
    @POST("imagensapi/upload")
    Call<ImagemResponse> uploadImagem(
            @Part("chamadoId") RequestBody chamadoId,
            @Part MultipartBody.Part arquivo
    );

    @GET("imagensapi/chamado/{chamadoId}")
    Call<ImagensResponse> getImagensPorChamado(@Path("chamadoId") int chamadoId);

    @DELETE("imagensapi/{id}")
    Call<RegisterResponse> deleteImagem(@Path("id") int imagemId);
}