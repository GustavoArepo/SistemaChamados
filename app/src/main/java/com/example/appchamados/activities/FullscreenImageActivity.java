package com.example.appchamados.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.appchamados.R;
import com.example.appchamados.models.ChamadoImagem;

public class FullscreenImageActivity extends AppCompatActivity {

    private ImageView ivFullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        ivFullscreen = findViewById(R.id.ivFullscreen);
        ImageButton btnClose = findViewById(R.id.btnClose);

        // ✅ OBTER DADOS DA INTENT
        String imagemUrl = getIntent().getStringExtra("IMAGEM_URL");
        String imagemNome = getIntent().getStringExtra("IMAGEM_NOME");

        if (imagemUrl != null) {
            // ✅ CARREGAR IMAGEM EM TELA CHEIA
            Glide.with(this)
                    .load(imagemUrl)
                    .into(ivFullscreen);

            if (imagemNome != null) {
                setTitle(imagemNome);
            }
        }

        // ✅ CLIQUE PARA FECHAR
        ivFullscreen.setOnClickListener(v -> finish());
        btnClose.setOnClickListener(v -> finish());
    }
}