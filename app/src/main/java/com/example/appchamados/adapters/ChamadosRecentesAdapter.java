package com.example.appchamados.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appchamados.R;
import com.example.appchamados.models.Chamado;
import java.util.List;

public class ChamadosRecentesAdapter extends RecyclerView.Adapter<ChamadosRecentesAdapter.ViewHolder> {

    private List<Chamado> listaChamados;
    private OnChamadoClickListener listener;

    public interface OnChamadoClickListener {
        void onChamadoClick(Chamado chamado);
    }

    public ChamadosRecentesAdapter(List<Chamado> listaChamados, OnChamadoClickListener listener) {
        this.listaChamados = listaChamados;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chamado_recente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chamado chamado = listaChamados.get(position);
        holder.bind(chamado);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChamadoClick(chamado);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaChamados.size();
    }

    public void atualizarLista(List<Chamado> novaLista) {
        this.listaChamados = novaLista;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitulo, tvStatus, tvData, tvIdChamado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvData = itemView.findViewById(R.id.tvData);
            tvIdChamado = itemView.findViewById(R.id.tvIdChamado);
        }

        public void bind(Chamado chamado) {
            // Título
            tvTitulo.setText(chamado.getTitulo());

            // ID
            tvIdChamado.setText("ID #" + chamado.getId());

            // Status
            String status = chamado.getStatus().toUpperCase();
            tvStatus.setText(status);

            // Data
            String data = chamado.getDataAbertura() != null ?
                    formatarData(chamado.getDataAbertura()) : "Sem data";
            tvData.setText(data);

            // Configurar cor e background do status
            configurarStatus(tvStatus, chamado.getStatus());
        }

        private void configurarStatus(TextView tvStatus, String status) {
            int textColor, bgTint;

            switch (status.toLowerCase()) {
                case "aberto":
                    textColor = 0xFF3B82F6;
                    bgTint = 0x1A3B82F6; // 10% alpha
                    break;
                case "em andamento":
                    textColor = 0xFFF59E0B;
                    bgTint = 0x1AF59E0B;
                    break;
                case "resolvido":
                    textColor = 0xFF10B981;
                    bgTint = 0x1A10B981;
                    break;
                case "fechado":
                    textColor = 0xFF6B7280;
                    bgTint = 0x1A6B7280;
                    break;
                default:
                    textColor = 0xFF64748B;
                    bgTint = 0x1A64748B;
            }

            tvStatus.setTextColor(textColor);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                tvStatus.getBackground().setTint(bgTint);
            }
        }

        private String formatarData(String data) {
            // Exemplo simples - você pode melhorar com SimpleDateFormat
            if (data.length() >= 10) {
                return data.substring(0, 10); // yyyy-MM-dd -> yyyy-MM-dd
            }
            return data;
        }
    }
}