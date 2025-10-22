package com.example.appchamados.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.appchamados.R;
import com.example.appchamados.databinding.FragmentProfile2Binding;
import com.example.appchamados.models.AtualizacaoPerfilRequest;
import com.example.appchamados.models.PerfilResponse;
import com.example.appchamados.network.ApiClient;
import com.example.appchamados.network.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfile2Binding binding;
    private TextView tvNome, tvEmail, tvTotalChamados, tvChamadosAbertos, tvChamadosResolvidos;
    private TextInputEditText etNome, etEmail, etSenhaAtual, etNovaSenha;
    private Button btnSalvar;
    private ProgressBar progressBar;

    // TODO: Substituir pelo ID real do usuário logado
    private int usuarioId = 5; // Temporário - depois vamos pegar do login

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfile2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeComponents();
        setupClickListeners();
        carregarPerfil();
    }

    private void initializeComponents() {
        tvNome = binding.tvNome;
        tvEmail = binding.tvEmail;
        tvTotalChamados = binding.tvTotalChamados;
        tvChamadosAbertos = binding.tvChamadosAbertos;
        tvChamadosResolvidos = binding.tvChamadosResolvidos;

        etNome = binding.etNome;
        etEmail = binding.etEmail;
        etSenhaAtual = binding.etSenhaAtual;
        etNovaSenha = binding.etNovaSenha;

        btnSalvar = binding.btnSalvar;
        progressBar = binding.progressBar;
    }

    private void setupClickListeners() {
        btnSalvar.setOnClickListener(v -> atualizarPerfil());
    }

    private void carregarPerfil() {
        showLoading(true);
        Log.d("PERFIL", "Buscando perfil do usuário ID: " + usuarioId);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<PerfilResponse> call = apiService.getPerfil(usuarioId);

        call.enqueue(new Callback<PerfilResponse>() {
            @Override
            public void onResponse(Call<PerfilResponse> call, Response<PerfilResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    PerfilResponse perfilResponse = response.body();

                    if (perfilResponse.isSuccess()) {
                        exibirDadosPerfil(perfilResponse.getPerfil());
                    } else {
                        Toast.makeText(getContext(), "Erro: " + perfilResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Erro ao carregar perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PerfilResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void exibirDadosPerfil(PerfilResponse.UsuarioPerfil perfil) {
        // Cabeçalho
        tvNome.setText(perfil.getNome());
        tvEmail.setText(perfil.getEmail());

        // Estatísticas
        tvTotalChamados.setText(String.valueOf(perfil.getTotalChamados()));
        tvChamadosAbertos.setText(String.valueOf(perfil.getChamadosAbertos()));
        tvChamadosResolvidos.setText(String.valueOf(perfil.getChamadosResolvidos()));

        // Formulário de edição
        etNome.setText(perfil.getNome());
        etEmail.setText(perfil.getEmail());

        // Limpar campos de senha
        etSenhaAtual.setText("");
        etNovaSenha.setText("");
    }

    private void atualizarPerfil() {
        String nome = etNome.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String senhaAtual = etSenhaAtual.getText().toString().trim();
        String novaSenha = etNovaSenha.getText().toString().trim();

        // Validações básicas
        if (nome.isEmpty()) {
            etNome.setError("Nome é obrigatório");
            return;
        }

        if (email.isEmpty() || !email.contains("@")) {
            etEmail.setError("Email inválido");
            return;
        }

        // Se informou nova senha, deve informar a atual
        if (!novaSenha.isEmpty() && senhaAtual.isEmpty()) {
            etSenhaAtual.setError("Informe a senha atual para alterar");
            return;
        }

        // Validar tamanho da nova senha
        if (!novaSenha.isEmpty() && novaSenha.length() < 8) {
            etNovaSenha.setError("Nova senha deve ter no mínimo 8 caracteres");
            return;
        }

        showLoading(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        AtualizacaoPerfilRequest request = new AtualizacaoPerfilRequest(nome, email, senhaAtual, novaSenha);

        Call<com.example.appchamados.models.RegisterResponse> call = apiService.atualizarPerfil(usuarioId, request);

        call.enqueue(new Callback<com.example.appchamados.models.RegisterResponse>() {
            @Override
            public void onResponse(Call<com.example.appchamados.models.RegisterResponse> call,
                                   Response<com.example.appchamados.models.RegisterResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    com.example.appchamados.models.RegisterResponse updateResponse = response.body();

                    if (updateResponse.isSuccess()) {
                        Toast.makeText(getContext(), "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                        // Atualizar dados exibidos
                        tvNome.setText(nome);
                        tvEmail.setText(email);
                    } else {
                        Toast.makeText(getContext(), "Erro: " + updateResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.appchamados.models.RegisterResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnSalvar.setEnabled(false);
            btnSalvar.setText("Carregando...");
        } else {
            progressBar.setVisibility(View.GONE);
            btnSalvar.setEnabled(true);
            btnSalvar.setText("Salvar Alterações");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}