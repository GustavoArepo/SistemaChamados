package com.example.appchamados.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appchamados.R;
import com.example.appchamados.activities.ChamadoDetailActivity;
import com.example.appchamados.activities.CriarChamadoActivity;
import com.example.appchamados.activities.MainActivity;
import com.example.appchamados.adapters.ChamadosRecentesAdapter;
import com.example.appchamados.databinding.FragmentHomeBinding;
import com.example.appchamados.models.Chamado;
import com.example.appchamados.models.ChamadosResponse;
import com.example.appchamados.network.ApiClient;
import com.example.appchamados.network.ApiService;
import com.example.appchamados.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements ChamadosRecentesAdapter.OnChamadoClickListener {

    private FragmentHomeBinding binding;
    private SessionManager sessionManager;
    private ChamadosRecentesAdapter adapter;
    private List<Chamado> listaRecentes = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        setupUserInfo();
        setupRecyclerView();
        setupClickListeners();
        carregarDadosDashboard();
    }

    private void setupUserInfo() {
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();

        binding.tvUserName.setText(userName.isEmpty() ? "Usuário" : userName);
        binding.tvWelcome.setText(userEmail.isEmpty() ? "Bem-vindo!" : "Bem-vindo de volta!");

        // Esconder badge de notificações por enquanto
        binding.tvNotificationBadge.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        adapter = new ChamadosRecentesAdapter(listaRecentes, this);
        binding.recyclerViewRecentes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewRecentes.setAdapter(adapter);
        binding.recyclerViewRecentes.setNestedScrollingEnabled(false);
    }

    private void setupClickListeners() {
        // NOVO CHAMADO
        binding.btnNovoChamado.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CriarChamadoActivity.class);
            startActivity(intent);
        });

        // VER TODOS OS CHAMADOS
        binding.btnVerTodos.setOnClickListener(v -> {
            // Navegar para TicketsFragment
            if (getActivity() != null && getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTickets();
            }
        });

        // VER TODOS RECENTES
        binding.tvVerTodosRecentes.setOnClickListener(v -> {
            if (getActivity() != null && getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToTickets();
            }
        });

        // CRIAR PRIMEIRO CHAMADO (quando lista vazia)
        binding.btnCriarPrimeiroChamado.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CriarChamadoActivity.class);
            startActivity(intent);
        });
    }

    private void carregarDadosDashboard() {
        if (!sessionManager.isLoggedIn()) {
            Log.e("HOME_DEBUG", "Usuário não logado");
            return;
        }

        int usuarioId = sessionManager.getUserId();
        Log.d("HOME_DEBUG", "Carregando dashboard para usuário: " + usuarioId);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ChamadosResponse> call = apiService.getChamadosPorUsuario(usuarioId);

        call.enqueue(new Callback<ChamadosResponse>() {
            @Override
            public void onResponse(Call<ChamadosResponse> call, Response<ChamadosResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChamadosResponse chamadosResponse = response.body();

                    if (chamadosResponse.isSuccess() && chamadosResponse.getChamados() != null) {
                        List<Chamado> todosChamados = chamadosResponse.getChamados();

                        // ✅ ATUALIZAR ESTATÍSTICAS
                        atualizarEstatisticas(todosChamados);

                        // ✅ ATUALIZAR LISTA RECENTES (últimos 3)
                        atualizarChamadosRecentes(todosChamados);

                    } else {
                        Log.e("HOME_DEBUG", "Erro na resposta da API");
                    }
                } else {
                    Log.e("HOME_DEBUG", "Falha ao carregar dados: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ChamadosResponse> call, Throwable t) {
                Log.e("HOME_DEBUG", "Erro de conexão: " + t.getMessage());
            }
        });
    }

    private void atualizarEstatisticas(List<Chamado> chamados) {
        int abertos = 0;
        int andamento = 0;
        int resolvidos = 0;

        for (Chamado chamado : chamados) {
            switch (chamado.getStatus().toLowerCase()) {
                case "aberto":
                    abertos++;
                    break;
                case "em andamento":
                    andamento++;
                    break;
                case "resolvido":
                    resolvidos++;
                    break;
            }
        }

        binding.tvAbertos.setText(String.valueOf(abertos));
        binding.tvAndamento.setText(String.valueOf(andamento));
        binding.tvResolvidos.setText(String.valueOf(resolvidos));

        Log.d("HOME_DEBUG", "Estatísticas - Abertos: " + abertos +
                ", Andamento: " + andamento + ", Resolvidos: " + resolvidos);
    }

    private void atualizarChamadosRecentes(List<Chamado> todosChamados) {
        // Pegar últimos 3 chamados (ou menos)
        int limite = Math.min(todosChamados.size(), 3);
        listaRecentes.clear();

        for (int i = 0; i < limite; i++) {
            listaRecentes.add(todosChamados.get(i));
        }

        adapter.atualizarLista(listaRecentes);
        verificarListaVazia();

        Log.d("HOME_DEBUG", "Chamados recentes carregados: " + listaRecentes.size());
    }

    private void verificarListaVazia() {
        if (listaRecentes.isEmpty()) {
            binding.recyclerViewRecentes.setVisibility(View.GONE);
            binding.layoutEmptyRecentes.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerViewRecentes.setVisibility(View.VISIBLE);
            binding.layoutEmptyRecentes.setVisibility(View.GONE);
        }
    }

    @Override
    public void onChamadoClick(Chamado chamado) {
        Log.d("HOME_DEBUG", "Navegando para detalhes do chamado: " + chamado.getId());

        // ✅ VERIFICAÇÃO DE SEGURANÇA
        if (!isAdded() || getActivity() == null) {
            Log.e("HOME_DEBUG", "Fragment não está attached - abortando navegação");
            return;
        }

        try {
            Intent intent = new Intent(getActivity(), ChamadoDetailActivity.class);
            intent.putExtra("CHAMADO_ID", chamado.getId());
            intent.putExtra("CHAMADO_TITULO", chamado.getTitulo());

            Log.d("HOME_DEBUG", "Iniciando ChamadoDetailActivity para ID: " + chamado.getId());
            startActivity(intent);

        } catch (Exception e) {
            Log.e("HOME_DEBUG", "Erro ao navegar para detalhes: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro ao abrir chamado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recarregar dados quando o fragment for revisitado
        carregarDadosDashboard();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}