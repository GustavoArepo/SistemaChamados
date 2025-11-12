package com.example.appchamados.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchamados.R;
import com.google.android.material.appbar.MaterialToolbar;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSend;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat); // ✅ CERTIFIQUE-SE que é o layout correto

        initViews();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // LOGS PARA DEBUG
        android.util.Log.d("ChatActivity", "Toolbar: " + (toolbar != null ? "OK" : "NULO"));
        android.util.Log.d("ChatActivity", "RecyclerView: " + (rvMessages != null ? "OK" : "NULO"));
        android.util.Log.d("ChatActivity", "EditText: " + (etMessage != null ? "OK" : "NULO"));
        android.util.Log.d("ChatActivity", "Button: " + (btnSend != null ? "OK" : "NULO"));

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void setupRecyclerView() {
        // ✅ VERIFICAÇÃO DE SEGURANÇA
        if (rvMessages != null) {
            rvMessages.setLayoutManager(new LinearLayoutManager(this));
            // rvMessages.setAdapter(adapter); // Comente temporariamente se não tem adapter ainda
        } else {
            // Log de erro para debug
            android.util.Log.e("ChatActivity", "RecyclerView é nulo! Verifique o layout.");
        }
    }

    private void setupClickListeners() {
        if (btnSend != null) {
            btnSend.setOnClickListener(v -> {
                // Lógica de envio de mensagem
                if (etMessage != null) {
                    String message = etMessage.getText().toString().trim();
                    if (!message.isEmpty()) {
                        // enviarMensagem(message);
                        etMessage.setText(""); // Limpa o campo
                    }
                }
            });
        }
    }
}