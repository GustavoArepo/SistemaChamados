package com.example.appchamados.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appchamados.R;
import com.example.appchamados.adapters.ChatIAAdapter;
import com.example.appchamados.models.MensagemIA;
import com.example.appchamados.utils.HuggingFaceAssistant;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;

public class ChatIAActivity extends AppCompatActivity {
    private static final String TAG = "ChatIAActivity";

    private HuggingFaceAssistant assistente; // ✅ MUDOU PARA HuggingFaceAssistant
    private EditText etPergunta;
    private RecyclerView rvConversa;
    private View layoutBoasVindas, layoutLoading;
    private ChatIAAdapter adapter;
    private List<MensagemIA> mensagens = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_ia);

        inicializarComponentes();
        inicializarAssistente();
        configurarSugestoes();
    }

    private void inicializarComponentes() {
        etPergunta = findViewById(R.id.etPergunta);
        rvConversa = findViewById(R.id.rvConversa);
        layoutBoasVindas = findViewById(R.id.layoutBoasVindas);
        layoutLoading = findViewById(R.id.layoutLoading);

        // ✅ CONFIGURAR RECYCLERVIEW
        adapter = new ChatIAAdapter(mensagens);
        rvConversa.setLayoutManager(new LinearLayoutManager(this));
        rvConversa.setAdapter(adapter);

        // ✅ BOTÃO ENVIAR
        findViewById(R.id.btnEnviar).setOnClickListener(v -> enviarPergunta());

        // ✅ BOTÃO VOLTAR
        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());

        // ✅ ENVIAR COM ENTER (opcional)
        etPergunta.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                enviarPergunta();
                return true;
            }
            return false;
        });
    }

    private void inicializarAssistente() {
        // ✅ HUGGING FACE
        assistente = new HuggingFaceAssistant(this); // ✅ MUDOU PARA 'this'

        TextView tvStatusIA = findViewById(R.id.tvStatusIA); // ✅ MUDOU PARA findViewById

        if (!assistente.estaDisponivel()) {
            tvStatusIA.setText("● Offline");
            tvStatusIA.setTextColor(getResources().getColor(R.color.red));
            Log.e(TAG, "Hugging Face não disponível");

            adicionarMensagem("Sistema",
                    "❌ Configure a API Key do Hugging Face\n" +
                            "1. Crie conta em huggingface.co\n" +
                            "2. Gere Access Token\n" +
                            "3. Cole no HuggingFaceAssistant.java (linha 24)",
                    false);
        } else {
            tvStatusIA.setText("● Online - HF");
            tvStatusIA.setTextColor(getResources().getColor(R.color.green));
            Log.d(TAG, "✅ Hugging Face pronto");

            adicionarMensagem("Assistente",
                    "👋 Olá! Sou seu assistente de suporte técnico usando Hugging Face AI.\n\nComo posso ajudar?",
                    false);
        }
    }

    private void configurarSugestoes() {
        Chip chip1 = findViewById(R.id.chipSugestao1);
        Chip chip2 = findViewById(R.id.chipSugestao2);
        Chip chip3 = findViewById(R.id.chipSugestao3);

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
            adicionarMensagem("Sistema", "❌ Configure a API Key do Hugging Face primeiro", false); // ✅ MUDOU MENSAGEM
            return;
        }

        // ✅ ESCONDER BOAS-VINDAS NA PRIMEIRA MENSAGEM
        if (mensagens.isEmpty()) {
            layoutBoasVindas.setVisibility(View.GONE);
            rvConversa.setVisibility(View.VISIBLE);
        }

        // ✅ ADICIONAR MENSAGEM DO USUÁRIO
        adicionarMensagem("Você", pergunta, true);
        etPergunta.setText("");

        // ✅ MOSTRAR LOADING
        layoutLoading.setVisibility(View.VISIBLE);

        // ✅ FAZER PERGUNTA PARA HUGGING FACE
        assistente.perguntar(pergunta, new HuggingFaceAssistant.AICallback() { // ✅ MUDOU CALLBACK
            @Override
            public void onSuccess(String resposta) {
                runOnUiThread(() -> {
                    layoutLoading.setVisibility(View.GONE);
                    adicionarMensagem("Assistente", resposta, false);
                    Log.d(TAG, "✅ Resposta recebida do Hugging Face");
                });
            }

            @Override
            public void onError(String erro) {
                runOnUiThread(() -> {
                    layoutLoading.setVisibility(View.GONE);
                    adicionarMensagem("Assistente", "❌ Erro: " + erro, false);
                    Log.e(TAG, "Erro Hugging Face: " + erro);
                });
            }
        });
    }

    private void adicionarMensagem(String remetente, String texto, boolean isUsuario) {
        MensagemIA mensagem = new MensagemIA(remetente, texto, isUsuario, System.currentTimeMillis());
        mensagens.add(mensagem);
        adapter.notifyItemInserted(mensagens.size() - 1);

        // ✅ ROLAR PARA A ÚLTIMA MENSAGEM COM ANIMAÇÃO SUAVE
        rvConversa.postDelayed(() -> {
            rvConversa.smoothScrollToPosition(mensagens.size() - 1);
        }, 100);
    }
}