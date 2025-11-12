package com.example.appchamados.models;

public class MensagemIA {
    private String remetente;
    private String texto;
    private boolean isUsuario;
    private long timestamp;

    public MensagemIA(String remetente, String texto, boolean isUsuario, long timestamp) {
        this.remetente = remetente;
        this.texto = texto;
        this.isUsuario = isUsuario;
        this.timestamp = timestamp;
    }

    // Getters
    public String getRemetente() { return remetente; }
    public String getTexto() { return texto; }
    public boolean isUsuario() { return isUsuario; }
    public long getTimestamp() { return timestamp; }
}