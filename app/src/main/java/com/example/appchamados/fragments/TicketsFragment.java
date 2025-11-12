package com.example.appchamados.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.appchamados.R;
import com.example.appchamados.activities.ChamadoDetailActivity;
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
import com.example.appchamados.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
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
    private SwipeRefreshLayout swipeRefreshLayout;

    private ChamadosAdapter adapter;
    private List<Chamado> listaChamados = new ArrayList<>();
    private int usuarioId;
    private SessionManager sessionManager;

    private MaterialButton btnFiltroTodos, btnFiltroAbertos, btnFiltroAndamento, btnFiltroResolvidos;
    private List<Chamado> listaChamadosCompleta = new ArrayList<>();
    private String filtroAtual = "TODOS";

    private static final int REQUEST_EDITAR_CHAMADO = 1001;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTicketsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ✅ CORREÇÃO: Usar SessionManager sem verificar token
        sessionManager = new SessionManager(requireContext());
        usuarioId = sessionManager.getUserId();

        Log.d("TICKETS_DEBUG", "=== SESSION MANAGER INFO ===");
        Log.d("TICKETS_DEBUG", "Usuario ID: " + usuarioId);
        Log.d("TICKETS_DEBUG", "Usuário logado: " + sessionManager.isLoggedIn());
        Log.d("TICKETS_DEBUG", "Nome do usuário: " + sessionManager.getUserName());
        Log.d("TICKETS_DEBUG", "Email do usuário: " + sessionManager.getUserEmail());

        initializeComponents();
        setupRecyclerView();
        setupClickListeners();
        setupPullToRefresh();
        setupFiltros();

        if (usuarioId != -1 && sessionManager.isLoggedIn()) {
            carregarChamados();
        } else {
            Log.e("TICKETS_DEBUG", "Usuário não logado - ID: " + usuarioId + ", isLoggedIn: " + sessionManager.isLoggedIn());
            Toast.makeText(getContext(), "Usuário não está logado!", Toast.LENGTH_LONG).show();
        }
    }

    private void initializeComponents() {
        recyclerViewChamados = binding.recyclerViewChamados;
        progressBar = binding.progressBar;
        layoutEmpty = binding.layoutEmpty;
        fabNovoChamado = binding.fabNovoChamado;
        swipeRefreshLayout = binding.swipeRefreshLayout;

        btnFiltroTodos = binding.btnFiltroTodos;
        btnFiltroAbertos = binding.btnFiltroAbertos;
        btnFiltroAndamento = binding.btnFiltroAndamento;
        btnFiltroResolvidos = binding.btnFiltroResolvidos;
    }

    private void setupFiltros() {
        btnFiltroTodos.setOnClickListener(v -> aplicarFiltro("TODOS"));
        btnFiltroAbertos.setOnClickListener(v -> aplicarFiltro("ABERTO"));
        btnFiltroAndamento.setOnClickListener(v -> aplicarFiltro("EM_ANDAMENTO"));
        btnFiltroResolvidos.setOnClickListener(v -> aplicarFiltro("RESOLVIDO"));

        selecionarFiltro(btnFiltroTodos);
    }

    private void aplicarFiltro(String filtro) {
        if (!isAdded() || getContext() == null) {
            return;
        }

        filtroAtual = filtro;
        Log.d("TICKETS_DEBUG", "Aplicando filtro: " + filtro);

        List<Chamado> listaFiltrada;

        switch (filtro) {
            case "ABERTO":
                listaFiltrada = filtrarPorStatus("Aberto");
                selecionarFiltro(btnFiltroAbertos);
                break;
            case "EM_ANDAMENTO":
                listaFiltrada = filtrarPorStatus("Em Andamento");
                selecionarFiltro(btnFiltroAndamento);
                break;
            case "RESOLVIDO":
                listaFiltrada = filtrarPorStatus("Resolvido");
                selecionarFiltro(btnFiltroResolvidos);
                break;
            default:
                listaFiltrada = new ArrayList<>(listaChamadosCompleta);
                selecionarFiltro(btnFiltroTodos);
                break;
        }

        adapter.atualizarLista(listaFiltrada);
        verificarListaVazia();
    }

    private void selecionarFiltro(MaterialButton botaoSelecionado) {
        if (!isAdded() || getContext() == null) {
            return;
        }

        MaterialButton[] botoes = {btnFiltroTodos, btnFiltroAbertos, btnFiltroAndamento, btnFiltroResolvidos};

        for (MaterialButton botao : botoes) {
            botao.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.transparent)));
            botao.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            botao.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
        }

        botaoSelecionado.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
        botaoSelecionado.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        botaoSelecionado.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
    }

    private List<Chamado> filtrarPorStatus(String status) {
        List<Chamado> filtrados = new ArrayList<>();
        for (Chamado chamado : listaChamadosCompleta) {
            if (chamado.getStatus().equalsIgnoreCase(status)) {
                filtrados.add(chamado);
            }
        }
        return filtrados;
    }

    private void setupRecyclerView() {
        adapter = new ChamadosAdapter(listaChamados, this);
        recyclerViewChamados.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewChamados.setAdapter(adapter);
    }

    private void setupPullToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            carregarChamados();
        });

        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }

    private void setupClickListeners() {
        fabNovoChamado.setOnClickListener(v -> {
            // ✅ Verificar se usuário está logado antes de criar chamado
            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(getContext(), "Você precisa estar logado para criar um chamado!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getActivity(), CriarChamadoActivity.class);
            startActivity(intent);
        });
    }

    private void carregarChamados() {
        Log.d("TICKETS_DEBUG", "=== INICIANDO CARREGAMENTO ===");
        Log.d("TICKETS_DEBUG", "Usuario ID: " + usuarioId);

        if (!isAdded() || getContext() == null) {
            Log.e("TICKETS_DEBUG", "Fragment não está attached ao contexto");
            return;
        }

        // ✅ Verificação simplificada - só verifica se está logado
        if (!sessionManager.isLoggedIn()) {
            Log.e("TICKETS_DEBUG", "Usuário não está logado - abortando carregamento");
            Toast.makeText(getContext(), "Usuário não está logado!", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            showLoading(false);
            return;
        }

        if (!swipeRefreshLayout.isRefreshing()) {
            showLoading(true);
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Log.d("TICKETS_DEBUG", "Fazendo requisição para usuário ID: " + usuarioId);

        Call<ChamadosResponse> call = apiService.getChamadosPorUsuario(usuarioId);

        call.enqueue(new Callback<ChamadosResponse>() {
            @Override
            public void onResponse(Call<ChamadosResponse> call, Response<ChamadosResponse> response) {
                swipeRefreshLayout.setRefreshing(false);
                showLoading(false);

                Log.d("TICKETS_DEBUG", "=== RESPOSTA DA API ===");
                Log.d("TICKETS_DEBUG", "Código HTTP: " + response.code());
                Log.d("TICKETS_DEBUG", "Sucesso: " + response.isSuccessful());

                if (!isAdded() || getContext() == null) {
                    Log.e("TICKETS_DEBUG", "Fragment não está mais attached - abortando");
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    ChamadosResponse chamadosResponse = response.body();
                    Log.d("TICKETS_DEBUG", "Success: " + chamadosResponse.isSuccess());
                    Log.d("TICKETS_DEBUG", "Message: " + chamadosResponse.getMessage());

                    // ✅ LOG CRÍTICO: Verificar se a lista está nula
                    Log.d("TICKETS_DEBUG", "Chamados é nulo? " + (chamadosResponse.getChamados() == null));

                    if (chamadosResponse.getChamados() != null) {
                        Log.d("TICKETS_DEBUG", "Quantidade de chamados: " + chamadosResponse.getChamados().size());
                    } else {
                        Log.e("TICKETS_DEBUG", "LISTA DE CHAMADOS É NULA!");
                    }

                    if (chamadosResponse.isSuccess()) {
                        listaChamados = chamadosResponse.getChamados();

                        // ✅ VERIFICAÇÃO DE SEGURANÇA
                        if (listaChamados == null) {
                            Log.e("TICKETS_DEBUG", "listaChamados é nula - criando lista vazia");
                            listaChamados = new ArrayList<>();
                        }

                        listaChamadosCompleta = new ArrayList<>(listaChamados);

                        Log.d("TICKETS_DEBUG", "Chamados recebidos: " + listaChamados.size());
                        Log.d("TICKETS_DEBUG", "Lista completa: " + listaChamadosCompleta.size());

                        for (Chamado chamado : listaChamados) {
                            Log.d("TICKETS_DEBUG", "Chamado: " + chamado.getId() + " - " + chamado.getTitulo() + " - Status: " + chamado.getStatus());
                        }

                        adapter.atualizarLista(listaChamados);
                        verificarListaVazia();

                        Log.d("TICKETS_DEBUG", "Adapter atualizado com " + adapter.getItemCount() + " itens");

                    } else {
                        Log.e("TICKETS_DEBUG", "Erro na resposta: " + chamadosResponse.getMessage());
                        Toast.makeText(getContext(), "Erro: " + chamadosResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("TICKETS_DEBUG", "Resposta não sucedida ou body nulo");
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("TICKETS_DEBUG", "Error Body: " + errorBody);
                            Toast.makeText(getContext(), "Erro HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("TICKETS_DEBUG", "Erro ao ler error body: " + e.getMessage());
                        }
                    } else {
                        Toast.makeText(getContext(), "Erro: resposta vazia", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChamadosResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                showLoading(false);

                if (!isAdded() || getContext() == null) return;

                Log.e("TICKETS_DEBUG", "Falha na requisição: " + t.getMessage());
                t.printStackTrace();
                Toast.makeText(getContext(), "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }    private void verificarListaVazia() {
        if (!isAdded() || getContext() == null) {
            return;
        }

        int tamanhoLista = adapter != null ? adapter.getItemCount() : 0;
        Log.d("TICKETS_DEBUG", "verificarListaVazia - Itens no adapter: " + tamanhoLista);

        if (tamanhoLista == 0) {
            Log.d("TICKETS_DEBUG", "Mostrando layout vazio");
            recyclerViewChamados.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            Log.d("TICKETS_DEBUG", "Mostrando lista com " + tamanhoLista + " itens");
            recyclerViewChamados.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewChamados.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onEditarClick(Chamado chamado) {
        Intent intent = new Intent(getActivity(), EditarChamadoActivity.class);
        intent.putExtra("CHAMADO", chamado);
        startActivityForResult(intent, REQUEST_EDITAR_CHAMADO);
    }

    @Override
    public void onExcluirClick(Chamado chamado) {
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
                        // Usuário cancelou
                    }
                });
    }

    @Override
    public void onChamadoClick(Chamado chamado) {
        Intent intent = new Intent(requireActivity(), ChamadoDetailActivity.class);
        intent.putExtra("CHAMADO_ID", chamado.getId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        // ✅ Atualizar session manager no resume
        if (sessionManager != null) {
            usuarioId = sessionManager.getUserId();
        }

        if (usuarioId != -1 && sessionManager.isLoggedIn()) {
            carregarChamados();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void excluirChamado(Chamado chamado) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RegisterResponse> call = apiService.deletarChamado(chamado.getId());

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (!isAdded() || getContext() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse excluirResponse = response.body();
                    if (excluirResponse.isSuccess()) {
                        Toast.makeText(getContext(), "Chamado excluído com sucesso!", Toast.LENGTH_SHORT).show();
                        carregarChamados();
                    } else {
                        Toast.makeText(getContext(), "Erro: " + excluirResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Erro no servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                if (!isAdded() || getContext() == null) return;
                Toast.makeText(getContext(), "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDITAR_CHAMADO && resultCode == getActivity().RESULT_OK) {
            carregarChamados();
            Toast.makeText(getContext(), "Chamado atualizado!", Toast.LENGTH_SHORT).show();
        }
    }

    // ✅ MÉTODO OPCIONAL: Redirecionar para login
    private void redirectToLogin() {
        // Intent intent = new Intent(getActivity(), LoginActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // startActivity(intent);
        // getActivity().finish();
    }
}