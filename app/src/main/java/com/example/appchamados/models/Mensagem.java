package com.example.appchamados.models;

import com.google.gson.annotations.SerializedName;

public class Mensagem {
    @SerializedName("id")
    private int id;

    @SerializedName("chamadoId")
    private int chamadoId;

    @SerializedName("usuarioId")
    private int usuarioId;

    @SerializedName("texto")
    private String texto;

    @SerializedName("dataEnvio")
    private String dataEnvio;

    @SerializedName("ehAtendente")
    private boolean ehAtendente;

    @SerializedName("lida")
    private boolean lida;

    // Getters
    public int getId() { return id; }
    public int getChamadoId() { return chamadoId; }
    public int getUsuarioId() { return usuarioId; }
    public String getTexto() { return texto; }
    public String getDataEnvio() { return dataEnvio; }
    public boolean isEhAtendente() { return ehAtendente; }
    public boolean isLida() { return lida; }
}