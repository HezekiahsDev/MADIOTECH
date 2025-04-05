package com.example.madiotech;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.editTextLoginEmailAddress);
        passwordEditText = findViewById(R.id.editTextLoginPassword);
        Button loginButton = findViewById(R.id.LoginPageLogin);
        TextView registerTextView = findViewById(R.id.textViewClickableRegister);
        TextView forgotPasswordTextView = findViewById(R.id.textViewClickableForgotPassword);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginButton.setOnClickListener(v -> handleLogin());

        // Set click listener for registerTextView
        // Navigate to SignupActivity when clicking "Sign Up"
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish(); // Close LoginActivity
        });
        // Navigate to ForgotPasswordActivity when clicking "Forgot Password"
        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });


        loginViewModel.getLoginResult().observe(this, loginResponse -> {
            if (loginResponse != null && "success".equalsIgnoreCase(loginResponse.getStatus())) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            } else {
                String errorMessage = (loginResponse != null) ? loginResponse.getMessage() : "Login failed. Please try again.";
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty()) {
            usernameEditText.setError("Username is required");
            usernameEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        loginViewModel.login(username, password);
    }
}
