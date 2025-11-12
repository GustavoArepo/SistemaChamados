package com.example.appchamados.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appchamados.R;
import com.example.appchamados.adapters.ChatIAAdapter;
import com.example.appchamados.models.MensagemIA;
import com.example.appchamados.utils.GeminiAssistant; // ✅ MUDOU PARA GEMINI
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;

public class ChatIAFragment extends Fragment {
    private static final String TAG = "ChatIAFragment";

    private GeminiAssistant assistente; // ✅ MUDOU PARA GEMINI
    private EditText etPergunta;
    private RecyclerView rvConversa;
    private View layoutBoasVindas, layoutLoading;
    private ChatIAAdapter adapter;
    private List<MensagemIA> mensagens = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chat_ia, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inicializarComponentes(view);
        inicializarAssistente(view);
        configurarSugestoes(view);
    }

    private void inicializarComponentes(View view) {
        etPergunta = view.findViewById(R.id.etPergunta);
        rvConversa = view.findViewById(R.id.rvConversa);
        layoutBoasVindas = view.findViewById(R.id.layoutBoasVindas);
        layoutLoading = view.findViewById(R.id.layoutLoading);

        // ✅ CONFIGURAR RECYCLERVIEW
        adapter = new ChatIAAdapter(mensagens);
        rvConversa.setLayoutManager(new LinearLayoutManager(getContext()));
        rvConversa.setAdapter(adapter);

        // ✅ BOTÃO ENVIAR
        view.findViewById(R.id.btnEnviar).setOnClickListener(v -> enviarPergunta());

        // ✅ BOTÃO VOLTAR
        view.findViewById(R.id.btnVoltar).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // ✅ ENVIAR COM ENTER
        etPergunta.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                enviarPergunta();
                return true;
            }
            return false;
        });
    }

    private void inicializarAssistente(View view) {
        assistente = new GeminiAssistant(requireContext()); // ✅ INSTANCIA GEMINI

        TextView tvStatusIA = view.findViewById(R.id.tvStatusIA);

        if (!assistente.estaDisponivel()) {
            tvStatusIA.setText("🔴 Offline");
            tvStatusIA.setTextColor(getResources().getColor(R.color.red));
            Log.e(TAG, "Gemini não disponível");

            adicionarMensagem("Sistema",
                    "🔑 **Gemini não configurado**\n\n" +
                            "A API Key já está no código.\n" +
                            "Se não funcionar, verifique:\n" +
                            "1. Conexão com internet\n" +
                            "2. API Key válida no código\n" +
                            "3. Permissões de internet no manifest",
                    false);
        } else {
            String status = "🟢 " + assistente.getModeloAtual();
            tvStatusIA.setText(status);
            tvStatusIA.setTextColor(getResources().getColor(R.color.green));
            Log.d(TAG, "✅ Gemini: " + status);

            adicionarMensagem("Gemini",
                    "👋 **Olá! Sou o Gemini 2.0 Flash**\n\n" +
                            "🤖 **Assistente de IA do Google**\n\n" +
                            "Posso ajudar com:\n" +
                            "• Problemas técnicos de TI\n" +
                            "• Dúvidas sobre sistemas\n" +
                            "• Suporte geral\n" +
                            "• Programação e código\n\n" +
                            "💡 **Como posso ajudar?**",
                    false);
        }
    }

    private void configurarSugestoes(View view) {
        Chip chip1 = view.findViewById(R.id.chipSugestao1);
        Chip chip2 = view.findViewById(R.id.chipSugestao2);
        Chip chip3 = view.findViewById(R.id.chipSugestao3);

        chip1.setOnClickListener(v -> enviarPerguntaSugerida(chip1.getText().toString()));
        chip2.setOnClickListener(v -> enviarPerguntaSugerida(chip2.getText().toString()));
        chip3.setOnClickListener(v -> enviarPerguntaSugerida(chip3.getText().toString()));
    }

    private void enviarPerguntaSugerida(String pergunta) {
        etPergunta.setText(pergunta);
        enviarPergunta();
    }

    private void enviarPergunta() {
        String pergunta = etPergunta.getText().toString().trim();

        if (TextUtils.isEmpty(pergunta)) {
            return;
        }

        if (!assistente.estaDisponivel()) {
            adicionarMensagem("Sistema", "❌ Gemini não está disponível no momento", false);
            return;
        }

        // ✅ ESCONDER BOAS-VINDAS NA PRIMEIRA MENSAGEM
        if (layoutBoasVindas.getVisibility() == View.VISIBLE) {
            layoutBoasVindas.setVisibility(View.GONE);
            rvConversa.setVisibility(View.VISIBLE);
        }

        // ✅ ADICIONAR MENSAGEM DO USUÁRIO
        adicionarMensagem("Você", pergunta, true);
        etPergunta.setText("");

        // ✅ MOSTRAR LOADING
        layoutLoading.setVisibility(View.VISIBLE);

        // ✅ FAZER PERGUNTA PARA GEMINI
        assistente.perguntar(pergunta, new GeminiAssistant.AICallback() {
            @Override
            public void onSuccess(String resposta) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        layoutLoading.setVisibility(View.GONE);
                        adicionarMensagem("Gemini", resposta, false);
                        Log.d(TAG, "✅ Resposta recebida do Gemini");
                    });
                }
            }

            @Override
            public void onError(String erro) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        layoutLoading.setVisibility(View.GONE);
                        adicionarMensagem("Gemini", "❌ Erro: " + erro, false);
                        Log.e(TAG, "Erro Gemini: " + erro);
                    });
                }
            }
        });
    }

    private void adicionarMensagem(String remetente, String texto, boolean isUsuario) {
        MensagemIA mensagem = new MensagemIA(remetente, texto, isUsuario, System.currentTimeMillis());
        mensagens.add(mensagem);
        adapter.notifyItemInserted(mensagens.size() - 1);

        // ✅ ROLAR PARA A ÚLTIMA MENSAGEM
        rvConversa.postDelayed(() -> {
            rvConversa.smoothScrollToPosition(mensagens.size() - 1);
        }, 100);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (assistente != null && getView() != null) {
            TextView tvStatusIA = getView().findViewById(R.id.tvStatusIA);
            if (assistente.estaDisponivel()) {
                tvStatusIA.setText("🟢 " + assistente.getModeloAtual());
                tvStatusIA.setTextColor(getResources().getColor(R.color.green));
            }
        }
    }
}