package com.example.appchamados.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Response para lista de mensagens
public class MensagensResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("mensagens")
    private List<Mensagem> mensagens;

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<Mensagem> getMensagens() { return mensagens; }
}
