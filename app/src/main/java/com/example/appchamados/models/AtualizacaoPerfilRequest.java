package com.example.appchamados.models;

public class AtualizacaoPerfilRequest {
    private String nome;
    private String email;
    private String senhaAtual;
    private String novaSenha;

    public AtualizacaoPerfilRequest(String nome, String email, String senhaAtual, String novaSenha) {
        this.nome = nome;
        this.email = email;
        this.senhaAtual = senhaAtual;
        this.novaSenha = novaSenha;
    }
    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaAtual() { return senhaAtual; }
    public void setSenhaAtual(String senhaAtual) { this.senhaAtual = senhaAtual; }

    public String getNovaSenha() { return novaSenha; }
    public void setNovaSenha(String novaSenha) { this.novaSenha = novaSenha; }
}
