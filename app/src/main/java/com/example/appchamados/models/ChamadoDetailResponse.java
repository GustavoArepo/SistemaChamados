package com.example.appchamados.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChamadoDetailResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("chamado")
    private ChamadoDetail chamado;

    @SerializedName("historico")
    private List<HistoricoItem> historico;

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public ChamadoDetail getChamado() { return chamado; }
    public List<HistoricoItem> getHistorico() { return historico; }
}

