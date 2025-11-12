package com.example.appchamados.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appchamados.R;
import com.example.appchamados.models.Chamado;
import java.util.List;

public class ChamadosAdapter extends RecyclerView.Adapter<ChamadosAdapter.ChamadoViewHolder> {

    private List<Chamado> chamados;
    private OnChamadoClickListener listener;

    public interface OnChamadoClickListener {
        void onEditarClick(Chamado chamado);
        void onExcluirClick(Chamado chamado);
        void onChamadoClick(Chamado chamado);
    }

    public ChamadosAdapter(List<Chamado> chamados, OnChamadoClickListener listener) {
        this.chamados = chamados;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChamadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chamado, parent, false);
        return new ChamadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChamadoViewHolder holder, int position) {
        Chamado chamado = chamados.get(position);
        holder.bind(chamado, listener);
    }

    @Override
    public int getItemCount() {
        return chamados.size();
    }

    public void atualizarLista(List<Chamado> novosChamados) {
        this.chamados = novosChamados;
        notifyDataSetChanged();
    }

    public void removerChamado(int position) {
        if (position >= 0 && position < chamados.size()) {
            chamados.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, chamados.size());
        }
    }

    public void atualizarChamado(int position, Chamado chamadoAtualizado) {
        if (position >= 0 && position < chamados.size()) {
            chamados.set(position, chamadoAtualizado);
            notifyItemChanged(position);
        }
    }

    static class ChamadoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitulo, tvDescricao, tvStatus, tvData;
        private ImageButton btnEditar, btnExcluir;

        public ChamadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDescricao = itemView.findViewById(R.id.tvDescricao);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvData = itemView.findViewById(R.id.tvData);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }

        public void bind(Chamado chamado, OnChamadoClickListener listener) {
            tvTitulo.setText(chamado.getTitulo());
            tvDescricao.setText(chamado.getDescricao());
            tvStatus.setText(chamado.getStatus());

            // Formatar data (se tiver)
            if (chamado.getDataAbertura() != null && !chamado.getDataAbertura().isEmpty()) {
                String dataFormatada = formatarData(chamado.getDataAbertura());
                tvData.setText(dataFormatada);
            }

            // Cor do status
            int bgRes = R.drawable.bg_status_aberto;
            switch (chamado.getStatus()) {
                case "Em Andamento":
                    bgRes = R.drawable.bg_status_andamento;
                    break;
                case "Resolvido":
                    bgRes = R.drawable.bg_status_resolvido;
                    break;
            }
            tvStatus.setBackgroundResource(bgRes);

            // Cliques
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChamadoClick(chamado);
                }
            });
            btnEditar.setOnClickListener(v -> listener.onEditarClick(chamado));
            btnExcluir.setOnClickListener(v -> listener.onExcluirClick(chamado));
        }

        private String formatarData(String data) {
            try {
                // Formato simples - você pode melhorar depois
                return data.substring(0, 10); // Pega apenas a data (YYYY-MM-DD)
            } catch (Exception e) {
                return data;
            }
        }
    }
}