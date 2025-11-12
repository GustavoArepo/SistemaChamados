package com.example.appchamados.models;

import com.google.gson.annotations.SerializedName;

// Response para upload de imagem
public class ImagemResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("imagem")
    private ChamadoImagem imagem;

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public ChamadoImagem getImagem() { return imagem; }
}
