package com.example.appchamados.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appchamados.R;
import com.example.appchamados.databinding.ActivityCriarChamadoBinding;
import com.example.appchamados.models.Chamado;
import com.example.appchamados.models.RegisterResponse;
import com.example.appchamados.network.ApiClient;
import com.example.appchamados.network.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CriarChamadoActivity extends AppCompatActivity {

    private ActivityCriarChamadoBinding binding;
    private TextInputEditText etTitulo, etDescricao;
    private Button btnSalvar;
    private ProgressBar progressBar;
    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCriarChamadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Pegar ID do usuário logado
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        usuarioId = prefs.getInt("userId", -1);

        initializeComponents();
        setupClickListeners();
    }

    private void initializeComponents() {
        etTitulo = binding.etTitulo;
        etDescricao = binding.etDescricao;
        btnSalvar = binding.btnSalvar;
        progressBar = binding.progressBar;
    }

    private void setupClickListeners() {
        btnSalvar.setOnClickListener(v -> criarChamado());

        // Botão voltar na ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void criarChamado() {
        String titulo = etTitulo.getText().toString().trim();
        String descricao = etDescricao.getText().toString().trim();

        // Validações
        if (titulo.isEmpty()) {
            etTitulo.setError("Título é obrigatório");
            return;
        }

        if (descricao.isEmpty()) {
            etDescricao.setError("Descrição é obrigatória");
            return;
        }

        if (usuarioId == -1) {
            Toast.makeText(this, "Usuário não logado", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        Chamado novoChamado = new Chamado(titulo, descricao, usuarioId);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RegisterResponse> call = apiService.criarChamado(novoChamado);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                showLoading(false);

                Log.d("CRIAR_CHAMADO", "Resposta: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse criarResponse = response.body();

                    if (criarResponse.isSuccess()) {
                        Log.d("CRIAR_CHAMADO", "Chamado criado com sucesso!");
                        Toast.makeText(CriarChamadoActivity.this, "Chamado criado com sucesso!", Toast.LENGTH_SHORT).show();
                        finish(); // Voltar para lista
                    } else {
                        Log.e("CRIAR_CHAMADO", "Erro: " + criarResponse.getMessage());
                        Toast.makeText(CriarChamadoActivity.this, "Erro: " + criarResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("CRIAR_CHAMADO", "Erro HTTP: " + response.code());
                    Toast.makeText(CriarChamadoActivity.this, "Erro no servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                showLoading(false);
                Log.e("CRIAR_CHAMADO", "Falha: " + t.getMessage());
                Toast.makeText(CriarChamadoActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSalvar.setEnabled(!show);
        btnSalvar.setText(show ? "Criando..." : "Criar Chamado");
    }

    // Método temporário para ver o token

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}