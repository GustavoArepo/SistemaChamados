package com.example.appchamados.models;

public class FCMTokenRequest {
    private int usuarioId;
    private String fcmToken;

    public FCMTokenRequest(int usuarioId, String fcmToken) {
        this.usuarioId = usuarioId;
        this.fcmToken = fcmToken;
    }

    // Getters e Setters
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
}