package com.example.appchamados.models;

import com.google.gson.annotations.SerializedName;

public class MensagemResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("mensagem")
    private Mensagem mensagem;

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Mensagem getMensagem() { return mensagem; }
}