package com.example.appchamados.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChamadoImagem {
    @SerializedName("id")
    private int id;

    @SerializedName("chamadoId")
    private int chamadoId;

    @SerializedName("nomeArquivo")
    private String nomeArquivo;

    @SerializedName("caminhoArquivo")
    private String caminhoArquivo;

    @SerializedName("dataUpload")
    private String dataUpload;

    // Getters
    public int getId() { return id; }
    public int getChamadoId() { return chamadoId; }
    public String getNomeArquivo() { return nomeArquivo; }
    public String getCaminhoArquivo() { return caminhoArquivo; }
    public String getDataUpload() { return dataUpload; }

    // ✅ URL completa da imagem
    public String getUrlCompleta() {
        return "http://10.0.2.2:5257" + caminhoArquivo; // Ajuste conforme sua API
    }
}

