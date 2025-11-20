package com.example.madiotech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar loginProgressBar;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.editTextLoginEmailAddress);
        passwordEditText = findViewById(R.id.editTextLoginPassword);
        loginButton = findViewById(R.id.LoginPageLogin);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        TextView registerTextView = findViewById(R.id.textViewClickableRegister);
        TextView forgotPasswordTextView = findViewById(R.id.textViewClickableForgotPassword);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Set click listener for login button
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

        // Observe loading state
        loginViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                showLoading();
            } else {
                hideLoading();
            }
        });

        // Observe login result
        loginViewModel.getLoginResult().observe(this, loginResponse -> {
            // Login process completed, hide loading
            loginViewModel.setLoading(false);

            if (loginResponse != null && "success".equalsIgnoreCase(loginResponse.getStatus())) {
                // Save username to SharedPreferences
                getSharedPreferences("MyPrefs", MODE_PRIVATE)
                        .edit()
                        .putString("KEY_USERNAME", usernameEditText.getText().toString().trim())
                        .putString("KEY_PALMPAY", loginResponse.getPalmpay())
                        .putString("KEY_9PSB", loginResponse.getNinePsb())

                        .apply();

                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            }
            else {
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

        // Show loading and disable button
        loginViewModel.setLoading(true);

        // Attempt login
        loginViewModel.login(username, password);
    }

    private void showLoading() {
        loginButton.setText(""); // Clear button text while loading
        loginProgressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        usernameEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
    }

    private void hideLoading() {
        loginButton.setText("Log In");
        loginProgressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);
        usernameEditText.setEnabled(true);
        passwordEditText.setEnabled(true);
    }
}