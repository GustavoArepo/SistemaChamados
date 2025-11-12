package com.example.appchamados.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.appchamados.R;
import com.example.appchamados.models.ChamadoImagem;

import java.time.Instant;
import java.util.List;

public class ImagensAdapter extends RecyclerView.Adapter<ImagensAdapter.ImagemViewHolder> {

    private List<ChamadoImagem> imagens;
    private OnImagemClickListener listener;

    public interface OnImagemClickListener {
        void onImagemClick(ChamadoImagem imagem);
        void onDeletarClick(ChamadoImagem imagem);
    }

    public ImagensAdapter(List<ChamadoImagem> imagens, OnImagemClickListener listener) {
        this.imagens = imagens;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_imagem, parent, false);
        return new ImagemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagemViewHolder holder, int position) {
        ChamadoImagem imagem = imagens.get(position);
        holder.bind(imagem, listener);
    }

    @Override
    public int getItemCount() {
        return imagens.size();
    }

    public void atualizarLista(List<ChamadoImagem> novasImagens) {
        this.imagens = novasImagens;
        notifyDataSetChanged();
    }

    static class ImagemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImagem;
        private TextView tvNomeArquivo;
        private ImageButton btnDeletar;

        public ImagemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagem = itemView.findViewById(R.id.ivImagem);
            tvNomeArquivo = itemView.findViewById(R.id.tvNomeArquivo);
            btnDeletar = itemView.findViewById(R.id.btnDeletarImagem);
        }

        public void bind(ChamadoImagem imagem, OnImagemClickListener listener) {
            tvNomeArquivo.setText(imagem.getNomeArquivo());

            String url = imagem.getUrlCompleta();
            Log.d("IMAGENS", "Tentando carregar imagem: " + url);

            // ✅ CARREGAR IMAGEM COM GLIDE
            Glide.with(itemView.getContext())
                    .load(url)
                    .placeholder(R.drawable.ic_photo_placeholder)
                    .error(R.drawable.ic_photo_placeholder)
                    .into(ivImagem);

            // ✅ CARREGAR IMAGEM COM GLIDE
            Glide.with(itemView.getContext())
                    .load(imagem.getUrlCompleta())
                    .placeholder(R.drawable.ic_photo_placeholder)
                    .error(R.drawable.ic_photo_placeholder)
                    .into(ivImagem);

            // ✅ CLIQUE NA IMAGEM (para visualizar)
            ivImagem.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onImagemClick(imagem);
                }
            });

            // ✅ CLIQUE PARA DELETAR
            btnDeletar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeletarClick(imagem);
                }
            });
        }
    }
}