package com.example.appchamados.models;

import java.util.List;

public class ChamadosResponse {
    private boolean success;
    private String message;
    private List<Chamado> chamados;

    // Getters e Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<Chamado> getChamados() { return chamados; }
    public void setChamados(List<Chamado> chamados) { this.chamados = chamados; }
}