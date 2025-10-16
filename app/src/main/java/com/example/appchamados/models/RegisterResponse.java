package com.example.appchamados.models;

public class RegisterResponse {
        private boolean success;
        private String message;

        // Getters e Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
}
