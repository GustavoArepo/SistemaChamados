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

public class EditarChamadoActivity extends AppCompatActivity {

    private ActivityCriarChamadoBinding binding;
    private TextInputEditText etTitulo, etDescricao;
    private Button btnSalvar;
    private ProgressBar progressBar;

    private Chamado chamado;
    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCriarChamadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Pegar ID do usuário logado
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        usuarioId = prefs.getInt("userId", -1);

        // Receber chamado da tela anterior
        chamado = (Chamado) getIntent().getSerializableExtra("CHAMADO");

        if (chamado == null) {
            Toast.makeText(this, "Chamado não encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeComponents();
        carregarDadosChamado();
        setupClickListeners();
    }

    private void initializeComponents() {
        etTitulo = binding.etTitulo;
        etDescricao = binding.etDescricao;
        btnSalvar = binding.btnSalvar;
        progressBar = binding.progressBar;

        // Mudar título da tela
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Editar Chamado");
        }
        btnSalvar.setText("Atualizar Chamado");
    }

    private void carregarDadosChamado() {
        etTitulo.setText(chamado.getTitulo());
        etDescricao.setText(chamado.getDescricao());
    }

    private void setupClickListeners() {
        btnSalvar.setOnClickListener(v -> atualizarChamado());

        // Botão voltar na ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void atualizarChamado() {
        String novoTitulo = etTitulo.getText().toString().trim();
        String novaDescricao = etDescricao.getText().toString().trim();

        // Validações
        if (novoTitulo.isEmpty()) {
            etTitulo.setError("Título é obrigatório");
            return;
        }

        if (novaDescricao.isEmpty()) {
            etDescricao.setError("Descrição é obrigatória");
            return;
        }

        showLoading(true);

        // Atualizar objeto chamado
        chamado.setTitulo(novoTitulo);
        chamado.setDescricao(novaDescricao);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RegisterResponse> call = apiService.atualizarChamado(chamado.getId(), chamado);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                showLoading(false);

                Log.d("EDITAR_CHAMADO", "Resposta: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse atualizarResponse = response.body();

                    if (atualizarResponse.isSuccess()) {
                        Log.d("EDITAR_CHAMADO", "Chamado atualizado com sucesso!");
                        Toast.makeText(EditarChamadoActivity.this, "Chamado atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Log.e("EDITAR_CHAMADO", "Erro: " + atualizarResponse.getMessage());
                        Toast.makeText(EditarChamadoActivity.this, "Erro: " + atualizarResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("EDITAR_CHAMADO", "Erro HTTP: " + response.code());
                    Toast.makeText(EditarChamadoActivity.this, "Erro no servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                showLoading(false);
                Log.e("EDITAR_CHAMADO", "Falha: " + t.getMessage());
                Toast.makeText(EditarChamadoActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSalvar.setEnabled(!show);
        btnSalvar.setText(show ? "Atualizando..." : "Atualizar Chamado");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}