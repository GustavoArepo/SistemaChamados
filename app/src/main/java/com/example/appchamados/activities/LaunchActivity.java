package com.example.appchamados.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appchamados.R;
import com.example.appchamados.utils.SessionManager;

public class LaunchActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1000; // 1 segundo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        SessionManager session = new SessionManager(this);

        new Handler().postDelayed(() -> {
            if (session.isLoggedIn()) {
                // Usuário já está logado - ir para Main
                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
            } else {
                // Usuário não logado - ir para Login
                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
            }
            finish();
        }, SPLASH_DELAY);
    }
}