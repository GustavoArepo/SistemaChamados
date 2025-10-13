package activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.example.appchamados.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity{

    private ActivityLoginBinding binding;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeComponents();
        setupClickListeners();

}
    private void initializeComponents() {
        etEmail = binding.etEmail;
        etPassword = binding.etPassword;
        btnLogin = binding.btnLogin;
        tvRegister = binding.tvRegister; // Adicione este TextView no XML
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        // Listener para cadastro
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!isValidEmail(email)) {
            etEmail.setError("Email deve conter @ e .com");
            return;
        }

        if (!isValidPassword(password)) {
            etPassword.setError("Senha deve ter no mínimo 8 caracteres");
            return;
        }

        // Simulação de autenticação
        if (authenticateUser(email, password)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Fecha a LoginActivity
        } else {
            Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".com") && email.length() > 6;
    }

    private boolean authenticateUser(String email, String password) {
        // Aqui você pode implementar uma verificação real
        // Por enquanto, vamos usar credenciais fixas para teste
        return email.equals("user@example.com") && password.equals("12345678");
    }


}

  


