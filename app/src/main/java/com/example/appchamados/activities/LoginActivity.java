package com.example.appchamados.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appchamados.databinding.ActivityLoginBinding;
import com.example.appchamados.models.LoginRequest;
import com.example.appchamados.models.LoginResponse;
import com.example.appchamados.network.ApiClient;
import com.example.appchamados.network.ApiService;
import com.example.appchamados.network.ConnectionTester;
import com.google.android.material.textfield.TextInputEditText;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d("LOGIN_ACTIVITY", "=== INICIANDO LOGIN ACTIVITY ===");

        initializeComponents();

        // ✅ TESTE: Preencher automaticamente para debug
        preencherCredenciaisAutomaticamente();

        // ✅ TESTE: Verificar conexão com API (AGORA CORRETO)
        testarConexaoComAPI();

        setupClickListeners();
    }


    private void initializeComponents() {
        etEmail = binding.etEmail;
        etPassword = binding.etPassword;
        btnLogin = binding.btnLogin;
        tvRegister = binding.tvRegister;

        Log.d("LOGIN_ACTIVITY", "Componentes inicializados");
    }

    private void testarConexaoComAPI() {
        new Thread(() -> {
            try {
                Log.d("LOGIN_ACTIVITY", "=== TESTANDO CONEXÃO DIRETAMENTE ===");

                URL url = new URL("http://10.0.2.2:5257/api/auth/login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Mesmas credenciais que usamos no app
                String jsonInputString = "{\"email\": \"admin@email.com\", \"senha\": \"12345678\"}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                Log.d("LOGIN_ACTIVITY", "TESTE DIRETO - Código: " + responseCode);

                if (responseCode == 200) {
                    Log.d("LOGIN_ACTIVITY", "✅ TESTE DIRETO - LOGIN FUNCIONA!");
                } else {
                    Log.e("LOGIN_ACTIVITY", "❌ TESTE DIRETO - Erro: " + responseCode);
                }

                connection.disconnect();

            } catch (Exception e) {
                Log.e("LOGIN_ACTIVITY", "❌ TESTE DIRETO - Falha: " + e.getMessage());
            }
        }).start();
    }

    private void preencherCredenciaisAutomaticamente() {
        // ✅ PREENCHA AUTOMATICAMENTE PARA TESTE
        etEmail.setText("admin@email.com");
        etPassword.setText("12345678");

        Log.d("AUTO_FILL", "Credenciais preenchidas automaticamente");
        Log.d("AUTO_FILL", "Email: " + etEmail.getText().toString());
        Log.d("AUTO_FILL", "Senha: " + etPassword.getText().toString());
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            Log.d("LOGIN_ACTIVITY", "Botão Login clicado");
            attemptLogin();
        });

        tvRegister.setOnClickListener(v -> {
            Log.d("LOGIN_ACTIVITY", "Texto Cadastrar clicado");
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // ✅ LOGS DETALHADOS PARA DEBUG
        Log.d("LOGIN_MANUAL", "=== TENTATIVA DE LOGIN MANUAL ===");
        Log.d("LOGIN_MANUAL", "Email digitado: '" + email + "'");
        Log.d("LOGIN_MANUAL", "Senha digitada: '" + password + "'");
        Log.d("LOGIN_MANUAL", "Tamanho do email: " + email.length());
        Log.d("LOGIN_MANUAL", "Tamanho da senha: " + password.length());

        // Validações
        if (!isValidEmail(email)) {
            etEmail.setError("Email deve conter @ e .com");
            Log.e("LOGIN_MANUAL", "❌ Email inválido - não contém @ e .com");
            return;
        }

        if (!isValidPassword(password)) {
            etPassword.setError("Senha deve ter no mínimo 8 caracteres");
            Log.e("LOGIN_MANUAL", "❌ Senha inválida - tamanho: " + password.length());
            return;
        }

        Log.d("LOGIN_MANUAL", "✅ Validações passadas - chamando API...");

        showLoading(true);

        try {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            LoginRequest loginRequest = new LoginRequest(email, password);

            // ✅ LOG DA REQUISIÇÃO
            Log.d("LOGIN_MANUAL", "Enviando para API:");
            Log.d("LOGIN_MANUAL", " - Email: '" + email + "'");
            Log.d("LOGIN_MANUAL", " - Senha: '" + password + "'");

            Call<LoginResponse> call = apiService.login(loginRequest);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    showLoading(false);

                    Log.d("LOGIN_MANUAL", "=== RESPOSTA DA API ===");
                    Log.d("LOGIN_MANUAL", "Código HTTP: " + response.code());
                    Log.d("LOGIN_MANUAL", "Mensagem: " + response.message());

                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();
                        Log.d("LOGIN_MANUAL", "Success: " + loginResponse.isSuccess());
                        Log.d("LOGIN_MANUAL", "Message: " + loginResponse.getMessage());

                        if (loginResponse.isSuccess()) {
                            Log.d("LOGIN_MANUAL", "✅ LOGIN MANUAL BEM-SUCEDIDO!");
                            Log.d("LOGIN_MANUAL", "Usuário: " + loginResponse.getUser().getNome());
                            Log.d("LOGIN_MANUAL", "ID: " + loginResponse.getUser().getId());

                            Toast.makeText(LoginActivity.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

                            // Navegar para MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Log.e("LOGIN_MANUAL", "❌ Login falhou: " + loginResponse.getMessage());
                            Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("LOGIN_MANUAL", "❌ Resposta não sucedida ou corpo vazio");
                        Log.e("LOGIN_MANUAL", "Código: " + response.code());
                        Log.e("LOGIN_MANUAL", "Mensagem: " + response.message());

                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                            Log.e("LOGIN_MANUAL", "Error Body: " + errorBody);
                        } catch (Exception e) {
                            Log.e("LOGIN_MANUAL", "Erro ao ler errorBody: " + e.getMessage());
                        }

                        Toast.makeText(LoginActivity.this, "Erro no servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    showLoading(false);
                    Log.e("LOGIN_MANUAL", "❌ FALHA NA REQUISIÇÃO");
                    Log.e("LOGIN_MANUAL", "Erro: " + t.getMessage());
                    t.printStackTrace();

                    Toast.makeText(LoginActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            showLoading(false);
            Log.e("LOGIN_MANUAL", "❌ EXCEÇÃO NA REQUISIÇÃO");
            Log.e("LOGIN_MANUAL", "Erro: " + e.getMessage());
            e.printStackTrace();

            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(String email) {
        boolean isValid = email.contains("@") && email.contains(".com") && email.length() > 6;
        Log.d("VALIDACAO_EMAIL", "Email '" + email + "' é válido? " + isValid);
        return isValid;
    }

    private boolean isValidPassword(String password) {
        boolean isValid = password.length() >= 8;
        Log.d("VALIDACAO_SENHA", "Senha com " + password.length() + " caracteres é válida? " + isValid);
        return isValid;
    }

    private void showLoading(boolean show) {
        if (show) {
            btnLogin.setText("Conectando...");
            btnLogin.setEnabled(false);
            Log.d("LOADING", "Mostrando loading...");
        } else {
            btnLogin.setText("Entrar");
            btnLogin.setEnabled(true);
            Log.d("LOADING", "Escondendo loading...");
        }
    }
}