package com.example.appchamados.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Response para lista de imagens
public class ImagensResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("imagens")
    private List<ChamadoImagem> imagens;

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<ChamadoImagem> getImagens() { return imagens; }
}
