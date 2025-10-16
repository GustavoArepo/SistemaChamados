package com.example.appchamados.models;

public class LoginResponse {
    private boolean success;
    private String message;
    private User user;

    // Getters e Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public static class User {
        private int id;
        private String nome;
        private String email;
        private String dataCadastro;

        // Getters e Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getDataCadastro() { return dataCadastro; }
        public void setDataCadastro(String dataCadastro) { this.dataCadastro = dataCadastro; }
    }
}
