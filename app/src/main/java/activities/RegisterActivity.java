package activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appchamados.databinding.ActivityRegisterBinding;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private TextInputEditText etName, etEmail, etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeComponents();
        setupClickListeners();

}

    private void initializeComponents() {
        etName = binding.etName;
        etEmail = binding.etEmail;
        etPassword = binding.etPassword;
        btnRegister = binding.btnRegister;

    }
    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Nome é obrigatório");
            return;
        }

        if (!isValidEmail(email)) {
            etEmail.setError("Email deve conter @ e .com");
            return;
        }

        if (!isValidPassword(password)) {
            etPassword.setError("Senha deve ter no mínimo 8 caracteres");
            return;
        }

        // Simulação de cadastro bem-sucedido
        registerUser(name, email, password);

    }

    private void registerUser(String name, String email, String password) {
        // Aqui você salvaria os dados no banco de dados
        // Por enquanto, vamos apenas simular o cadastro

        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();

        // Redireciona para a tela de login
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("registered_email", email); // Opcional: preencher email no login
        startActivity(intent);
        finish(); // Fecha a tela de cadastro
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".com") && email.length() > 6;
    }
}

