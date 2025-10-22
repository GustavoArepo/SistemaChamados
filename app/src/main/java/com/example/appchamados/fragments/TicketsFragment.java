package com.example.appchamados.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appchamados.R;
import com.example.appchamados.activities.CriarChamadoActivity;
import com.example.appchamados.activities.EditarChamadoActivity;
import com.example.appchamados.adapters.ChamadosAdapter;
import com.example.appchamados.databinding.FragmentTicketsBinding;
import com.example.appchamados.models.Chamado;
import com.example.appchamados.models.ChamadosResponse;
import com.example.appchamados.models.RegisterResponse;
import com.example.appchamados.network.ApiClient;
import com.example.appchamados.network.ApiService;
import com.example.appchamados.utils.ConfirmDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketsFragment extends Fragment implements ChamadosAdapter.OnChamadoClickListener {

    private FragmentTicketsBinding binding;
    private RecyclerView recyclerViewChamados;
    private ProgressBar progressBar;
    private View layoutEmpty;
    private FloatingActionButton fabNovoChamado;

    private ChamadosAdapter adapter;
    private List<Chamado> listaChamados = new ArrayList<>();
    private int usuarioId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTicketsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Pegar ID do usuário logado
        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", requireContext().MODE_PRIVATE);
        usuarioId = prefs.getInt("userId", -1);

        initializeComponents();
        setupRecyclerView();
        setupClickListeners();

        if (usuarioId != -1) {
            carregarChamados();
        } else {
            Log.e("TICKETS", "Usuário não logado");
        }
    }

    private void initializeComponents() {
        recyclerViewChamados = binding.recyclerViewChamados;
        progressBar = binding.progressBar;
        layoutEmpty = binding.layoutEmpty;
        fabNovoChamado = binding.fabNovoChamado;
    }

    private void setupRecyclerView() {
        adapter = new ChamadosAdapter(listaChamados, this);
        recyclerViewChamados.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewChamados.setAdapter(adapter);
    }

    private void setupClickListeners() {
        fabNovoChamado.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CriarChamadoActivity.class);
            startActivity(intent);
        });
    }

    private void carregarChamados() {
        Log.d("TICKETS", "Carregando chamados para usuário: " + usuarioId);
        showLoading(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ChamadosResponse> call = apiService.getChamadosPorUsuario(usuarioId);

        call.enqueue(new Callback<ChamadosResponse>() {
            @Override
            public void onResponse(Call<ChamadosResponse> call, Response<ChamadosResponse> response) {
                showLoading(false);

                Log.d("TICKETS", "Resposta da API - Código: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ChamadosResponse chamadosResponse = response.body();

                    if (chamadosResponse.isSuccess()) {
                        listaChamados = chamadosResponse.getChamados();
                        Log.d("TICKETS", "Chamados carregados: " + listaChamados.size());

                        adapter.atualizarLista(listaChamados);
                        verificarListaVazia();

                    } else {
                        Log.e("TICKETS", "Erro na resposta: " + chamadosResponse.getMessage());
                    }
                } else {
                    Log.e("TICKETS", "Erro HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ChamadosResponse> call, Throwable t) {
                showLoading(false);
                Log.e("TICKETS", "Falha na requisição: " + t.getMessage());
            }
        });
    }

    private void verificarListaVazia() {
        if (listaChamados.isEmpty()) {
            recyclerViewChamados.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerViewChamados.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewChamados.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    // Implementação dos cliques
    @Override
    public void onEditarClick(Chamado chamado) {
        Log.d("TICKETS", "Editar chamado: " + chamado.getTitulo());

        Intent intent = new Intent(getActivity(), EditarChamadoActivity.class);
        intent.putExtra("CHAMADO", chamado);
        startActivityForResult(intent, REQUEST_EDITAR_CHAMADO);
    }

    @Override
    public void onExcluirClick(Chamado chamado) {
        Log.d("TICKETS", "Excluir chamado: " + chamado.getTitulo());

        ConfirmDialog.show(requireContext(),
                "Excluir Chamado",
                "Tem certeza que deseja excluir o chamado \"" + chamado.getTitulo() + "\"?",
                new ConfirmDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        excluirChamado(chamado);
                    }

                    @Override
                    public void onCancel() {
                        // Usuário cancelou, não faz nada
                    }
                });
    }

    @Override
    public void onChamadoClick(Chamado chamado) {
        Log.d("TICKETS", "Clicou no chamado: " + chamado.getTitulo());
        // Podemos implementar uma tela de detalhes depois
        mostrarDetalhesChamado(chamado);
    }


    @Override
    public void onResume() {
        super.onResume();
        // Recarregar chamados quando voltar para a tela
        if (usuarioId != -1) {
            carregarChamados();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static final int REQUEST_EDITAR_CHAMADO = 1001;

    private void excluirChamado(Chamado chamado) {
        Log.d("TICKETS", "Excluindo chamado ID: " + chamado.getId());

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RegisterResponse> call = apiService.deletarChamado(chamado.getId());

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                Log.d("TICKETS", "Resposta exclusão: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse excluirResponse = response.body();

                    if (excluirResponse.isSuccess()) {
                        Toast.makeText(getContext(), "Chamado excluído com sucesso!", Toast.LENGTH_SHORT).show();
                        carregarChamados(); // Recarregar lista
                    } else {
                        Toast.makeText(getContext(), "Erro: " + excluirResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Erro no servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Log.e("TICKETS", "Falha na exclusão: " + t.getMessage());
                Toast.makeText(getContext(), "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDetalhesChamado(Chamado chamado) {
        // Dialog simples com detalhes - podemos melhorar depois
        String detalhes = "Título: " + chamado.getTitulo() + "\n\n" +
                "Descrição: " + chamado.getDescricao() + "\n\n" +
                "Status: " + chamado.getStatus() + "\n" +
                "Data: " + (chamado.getDataAbertura() != null ?
                chamado.getDataAbertura().substring(0, 10) : "N/A");

        new AlertDialog.Builder(requireContext())
                .setTitle("Detalhes do Chamado")
                .setMessage(detalhes)
                .setPositiveButton("OK", null)
                .show();
    }

    // Adicione este método para tratar o retorno da edição
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDITAR_CHAMADO && resultCode == getActivity().RESULT_OK) {
            // Chamado foi editado, recarregar lista
            carregarChamados();
            Toast.makeText(getContext(), "Chamado atualizado!", Toast.LENGTH_SHORT).show();
        }
    }
}