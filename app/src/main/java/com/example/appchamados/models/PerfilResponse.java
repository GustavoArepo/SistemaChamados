package com.example.appchamados.models;

public class PerfilResponse {
    private boolean success;
    private String message;
    private UsuarioPerfil perfil;

    // Getters e Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UsuarioPerfil getPerfil() {
        return perfil;
    }

    public void setPerfil(UsuarioPerfil perfil) {
        this.perfil = perfil;
    }

    public static class UsuarioPerfil {
        private int id;
        private String nome;
        private String email;
        private String dataCadastro;
        private int totalChamados;
        private int chamadosAbertos;
        private int chamadosResolvidos;

        // Getters e Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getDataCadastro() { return dataCadastro; }
        public void setDataCadastro(String dataCadastro) { this.dataCadastro = dataCadastro; }

        public int getTotalChamados() { return totalChamados; }
        public void setTotalChamados(int totalChamados) { this.totalChamados = totalChamados; }

        public int getChamadosAbertos() { return chamadosAbertos; }
        public void setChamadosAbertos(int chamadosAbertos) { this.chamadosAbertos = chamadosAbertos; }

        public int getChamadosResolvidos() { return chamadosResolvidos; }
        public void setChamadosResolvidos(int chamadosResolvidos) { this.chamadosResolvidos = chamadosResolvidos; }
    }
}