package com.example.appchamados.models;

import com.google.gson.annotations.SerializedName;

public class ChamadoDetail {
    @SerializedName("id")
    private int id;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("descricao")
    private String descricao;

    @SerializedName("status")
    private String status;

    @SerializedName("prioridade")
    private String prioridade;

    @SerializedName("criado_em")
    private String criadoEm;

    @SerializedName("atualizado_em")
    private String atualizadoEm;

    @SerializedName("usuario_id")
    private int usuarioId;

    @SerializedName("usuario_nome")
    private String usuarioNome;

    @SerializedName("tecnico_id")
    private Integer tecnicoId;

    @SerializedName("tecnico_nome")
    private String tecnicoNome;

    // Getters
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getStatus() { return status; }
    public String getPrioridade() { return prioridade; }
    public String getCriadoEm() { return criadoEm; }
    public String getAtualizadoEm() { return atualizadoEm; }
    public int getUsuarioId() { return usuarioId; }
    public String getUsuarioNome() { return usuarioNome; }
    public Integer getTecnicoId() { return tecnicoId; }
    public String getTecnicoNome() { return tecnicoNome; }
}
