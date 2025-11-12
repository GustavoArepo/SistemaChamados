package com.example.appchamados.models;

import com.google.gson.annotations.SerializedName;

public class MensagemRequest {
    @SerializedName("chamadoId")
    private int chamadoId;

    @SerializedName("usuarioId")
    private int usuarioId;

    @SerializedName("texto")
    private String texto;

    @SerializedName("ehAtendente")
    private boolean ehAtendente;

    public MensagemRequest(int chamadoId, int usuarioId, String texto, boolean ehAtendente) {
        this.chamadoId = chamadoId;
        this.usuarioId = usuarioId;
        this.texto = texto;
        this.ehAtendente = ehAtendente;
    }

    // Getters (opcionais para Retrofit)
    public int getChamadoId() { return chamadoId; }
    public int getUsuarioId() { return usuarioId; }
    public String getTexto() { return texto; }
    public boolean isEhAtendente() { return ehAtendente; }
}