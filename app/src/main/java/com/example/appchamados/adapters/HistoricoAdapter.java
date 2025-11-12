package com.example.appchamados.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appchamados.R;
import com.example.appchamados.models.HistoricoItem;
import java.util.List;

public class HistoricoAdapter extends RecyclerView.Adapter<HistoricoAdapter.ViewHolder> {

    private List<HistoricoItem> historicoList;

    public HistoricoAdapter(List<HistoricoItem> historicoList) {
        this.historicoList = historicoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historico, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistoricoItem item = historicoList.get(position);

        holder.tvAcao.setText(item.getAcao());
        holder.tvDescricao.setText(item.getDescricao());
        holder.tvData.setText(formatarData(item.getData()));
        holder.tvUsuario.setText("Por: " + item.getUsuarioNome());
    }

    @Override
    public int getItemCount() {
        return historicoList != null ? historicoList.size() : 0;
    }

    private String formatarData(String data) {
        try {
            return data.substring(0, 16).replace("T", " "); // Formato: YYYY-MM-DD HH:MM
        } catch (Exception e) {
            return data;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAcao, tvDescricao, tvData, tvUsuario;

        public ViewHolder(View itemView) {
            super(itemView);
            tvAcao = itemView.findViewById(R.id.tvAcao);
            tvDescricao = itemView.findViewById(R.id.tvDescricao);
            tvData = itemView.findViewById(R.id.tvData);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
        }
    }
}