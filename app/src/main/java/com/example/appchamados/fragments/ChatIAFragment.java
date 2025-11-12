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
import com.example.appchamados.utils.HuggingFaceAssistant; // ✅ IMPORT CORRETO
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;

public class ChatIAFragment extends Fragment {
    private static final String TAG = "ChatIAFragment";

    private HuggingFaceAssistant assistente; // ✅ MUDOU PARA HuggingFaceAssistant
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
        assistente.diagnostico();

        TextView tvStatusIA = view.findViewById(R.id.tvStatusIA);
        if (!assistente.estaDisponivel()) {
            tvStatusIA.setText("🔴 Offline");
            tvStatusIA.setTextColor(getResources().getColor(R.color.red));

            // ✅ MENSAGEM MAIS ESPECÍFICA
            String mensagemErro = criarMensagemErro();
            adicionarMensagem("Sistema", mensagemErro, false);

        } else {
            String status = "🟢 " + assistente.getModeloAtual();
            tvStatusIA.setText(status);
            tvStatusIA.setTextColor(getResources().getColor(R.color.green));

            adicionarMensagem("Assistente",
                    "👋 **Assistente de TI Online!**\n\n" +
                            "Estou pronto para ajudar com problemas técnicos.\n\n" +
                            "💡 **Como posso ajudar?**",
                    false);
        }

        // ✅ CONFIGURAR RECYCLERVIEW
        adapter = new ChatIAAdapter(mensagens);
        rvConversa.setLayoutManager(new LinearLayoutManager(getContext()));
        rvConversa.setAdapter(adapter);

        // ✅ BOTÃO ENVIAR
        view.findViewById(R.id.btnEnviar).setOnClickListener(v -> enviarPergunta());

        // ✅ BOTÃO VOLTAR - FECHA O FRAGMENT
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
        assistente = new HuggingFaceAssistant(requireContext());

        TextView tvStatusIA = view.findViewById(R.id.tvStatusIA);

        if (!assistente.estaDisponivel()) {
            tvStatusIA.setText("🔴 Offline");
            tvStatusIA.setTextColor(getResources().getColor(R.color.red));
            Log.e(TAG, "Hugging Face não disponível");

            adicionarMensagem("Sistema",
                    "🔑 **Configuração Necessária**\n\n" +
                            "Para usar o assistente IA:\n" +
                            "1. Acesse huggingface.co\n" +
                            "2. Crie conta gratuita\n" +
                            "3. Gere Access Token\n" +
                            "4. Cole em HuggingFaceAssistant.java\n" +
                            "   (linha ~25: apiKey = \"sua_chave_aqui\")",
                    false);
        } else {
            String status = "🟢 " + assistente.getModeloAtual();
            tvStatusIA.setText(status);
            tvStatusIA.setTextColor(getResources().getColor(R.color.green));
            Log.d(TAG, "✅ Hugging Face: " + status);

            adicionarMensagem("Assistente",
                    "👋 **Olá! Sou seu assistente de TI**\n\n" +
                            "🔧 **Especialidades:**\n" +
                            "• Problemas de senha e acesso\n" +
                            "• Conexão de internet e rede\n" +
                            "• Performance do sistema\n" +
                            "• Configurações de software\n" +
                            "• Erros e troubleshooting\n\n" +
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
            adicionarMensagem("Sistema", "❌ Configure a API Key do Hugging Face primeiro", false);
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
        assistente.perguntar(pergunta, new HuggingFaceAssistant.AICallback() { // ✅ CALLBACK CORRETO
            @Override
            public void onSuccess(String resposta) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        layoutLoading.setVisibility(View.GONE);
                        adicionarMensagem("Assistente", resposta, false);
                        Log.d(TAG, "✅ Resposta recebida do Hugging Face");
                    });
                }
            }

            @Override
            public void onError(String erro) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        layoutLoading.setVisibility(View.GONE);
                        adicionarMensagem("Assistente", "❌ Erro: " + erro, false);
                        Log.e(TAG, "Erro Hugging Face: " + erro);
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

    // ✅ MÉTODO PARA ATUALIZAR QUANDO VOLTAR AO FRAGMENT
    @Override
    public void onResume() {
        super.onResume();
        // Recarregar status se necessário
        if (assistente != null && getView() != null) {
            TextView tvStatusIA = getView().findViewById(R.id.tvStatusIA);
            if (assistente.estaDisponivel()) {
                tvStatusIA.setText("● Online - HF");
                tvStatusIA.setTextColor(getResources().getColor(R.color.green));
            }
        }
    }

    private String criarMensagemErro() {
        return "🔧 **Configuração da API Key**\n\n" +
                "Para ativar o assistente IA:\n\n" +
                "1. **Acesse:** huggingface.co\n" +
                "2. **Crie conta** gratuita\n" +
                "3. **Vá em Settings → Access Tokens**\n" +
                "4. **Crie New Token** (nome: AppChamados)\n" +
                "5. **Copie o token** (começa com hf_...)\n\n" +
                "6. **No código, edite:**\n" +
                "   `HuggingFaceAssistant.java`\n" +
                "   **Linha ~25:**\n" +
                "   `private String apiKey = \"COLE_A_KEY_AQUI\";`\n\n" +
                "7. **Salve e reinicie o app**";
    }
}