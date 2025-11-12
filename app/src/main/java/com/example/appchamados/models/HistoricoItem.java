package com.example.appchamados.models;

import com.google.gson.annotations.SerializedName;

public class HistoricoItem {
    @SerializedName("id")
    private int id;

    @SerializedName("acao")
    private String acao;

    @SerializedName("descricao")
    private String descricao;

    @SerializedName("data")
    private String data;

    @SerializedName("usuario_nome")
    private String usuarioNome;

    // Getters
    public int getId() { return id; }
    public String getAcao() { return acao; }
    public String getDescricao() { return descricao; }
    public String getData() { return data; }
    public String getUsuarioNome() { return usuarioNome; }
}
