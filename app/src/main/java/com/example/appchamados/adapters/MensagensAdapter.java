package com.example.appchamados.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appchamados.R;
import com.example.appchamados.models.Mensagem;
import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MensagemViewHolder> {

    private List<Mensagem> mensagens;
    private int usuarioLogadoId;

    public MensagensAdapter(List<Mensagem> mensagens, int usuarioLogadoId) {
        this.mensagens = mensagens;
        this.usuarioLogadoId = usuarioLogadoId;
    }

    @Override
    public int getItemViewType(int position) {
        Mensagem mensagem = mensagens.get(position);
        // ✅ 0 = Mensagem do usuário logado (direita)
        // ✅ 1 = Mensagem do atendente (esquerda)
        return mensagem.getUsuarioId() == usuarioLogadoId ? 0 : 1;
    }

    @NonNull
    @Override
    public MensagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ✅ LAYOUT DIFERENTE PARA REMETENTE/DESTINATÁRIO
        int layoutRes = viewType == 0 ? R.layout.item_mensagem_direita : R.layout.item_mensagem_esquerda;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new MensagemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensagemViewHolder holder, int position) {
        Mensagem mensagem = mensagens.get(position);
        holder.bind(mensagem);
    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    public void atualizarLista(List<Mensagem> novasMensagens) {
        this.mensagens = novasMensagens;
        notifyDataSetChanged();
    }

    public void adicionarMensagem(Mensagem mensagem) {
        this.mensagens.add(mensagem);
        notifyItemInserted(mensagens.size() - 1);
    }

    static class MensagemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMensagem, tvHora;

        public MensagemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensagem = itemView.findViewById(R.id.tvMensagem);
            tvHora = itemView.findViewById(R.id.tvHora);
        }

        public void bind(Mensagem mensagem) {
            tvMensagem.setText(mensagem.getTexto());
            tvHora.setText(formatarHora(mensagem.getDataEnvio()));
        }

        private String formatarHora(String dataEnvio) {
            try {
                // Formato simples: "HH:mm"
                return dataEnvio.substring(11, 16);
            } catch (Exception e) {
                return dataEnvio;
            }
        }
    }
}