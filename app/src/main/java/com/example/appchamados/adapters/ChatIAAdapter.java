package com.example.appchamados.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appchamados.R;
import com.example.appchamados.models.MensagemIA;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatIAAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_USUARIO = 1;
    private static final int TIPO_ASSISTENTE = 2;

    private List<MensagemIA> mensagens;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public ChatIAAdapter(List<MensagemIA> mensagens) {
        this.mensagens = mensagens;
    }

    @Override
    public int getItemViewType(int position) {
        return mensagens.get(position).isUsuario() ? TIPO_USUARIO : TIPO_ASSISTENTE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TIPO_USUARIO) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensagem_usuario, parent, false);
            return new UsuarioViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensagem_assistente, parent, false);
            return new AssistenteViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MensagemIA mensagem = mensagens.get(position);

        if (holder instanceof UsuarioViewHolder) {
            ((UsuarioViewHolder) holder).bind(mensagem);
        } else if (holder instanceof AssistenteViewHolder) {
            ((AssistenteViewHolder) holder).bind(mensagem);
        }
    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    public void adicionarMensagem(MensagemIA mensagem) {
        mensagens.add(mensagem);
        notifyItemInserted(mensagens.size() - 1);
    }

    // ✅ VIEWHOLDER PARA MENSAGENS DO USUÁRIO
    class UsuarioViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMensagem, tvHora;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensagem = itemView.findViewById(R.id.tvMensagem);
            tvHora = itemView.findViewById(R.id.tvHora);
        }

        public void bind(MensagemIA mensagem) {
            tvMensagem.setText(mensagem.getTexto());
            tvHora.setText(formatarHora(mensagem.getTimestamp()));
        }
    }

    // ✅ VIEWHOLDER PARA MENSAGENS DO ASSISTENTE
    class AssistenteViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMensagem, tvHora;

        public AssistenteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensagem = itemView.findViewById(R.id.tvMensagem);
            tvHora = itemView.findViewById(R.id.tvHora);
        }

        public void bind(MensagemIA mensagem) {
            tvMensagem.setText(mensagem.getTexto());
            tvHora.setText(formatarHora(mensagem.getTimestamp()));
        }
    }

    private String formatarHora(long timestamp) {
        return timeFormat.format(new Date(timestamp));
    }
}