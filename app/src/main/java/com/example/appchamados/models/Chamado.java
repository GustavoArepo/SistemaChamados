package com.example.appchamados.models;

import java.io.Serializable;

public class Chamado implements Serializable{
    private int id;
    private String titulo;
    private String descricao;
    private int usuarioId;
    private String status;
    private String dataAbertura;
    private String dataFechamento;

    // Constructor
    public Chamado(String titulo, String descricao, int usuarioId) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.usuarioId = usuarioId;
        this.status = "Aberto";
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(String dataAbertura) { this.dataAbertura = dataAbertura; }

    public String getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(String dataFechamento) { this.dataFechamento = dataFechamento; }
}