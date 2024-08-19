package com.example.madiotech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeBackActivity extends AppCompatActivity {

    private EditText passwordEditText;
    private ImageView loginButton;
    private TextView registerTextView;
    private TextView usernameTextView;

    private static final String VALID_PASSWORD = "admin";
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomeback);

        // Find the views by ID
        passwordEditText = findViewById(R.id.editTextLoginPassword);
        loginButton = findViewById(R.id.imageViewLoginButton);
        registerTextView = findViewById(R.id.textViewClickableRegister);
        usernameTextView = findViewById(R.id.welcomBackUsername);

        // Retrieve and set the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString(KEY_USERNAME, "User");
        usernameTextView.setText(username);

        // Set OnClickListener for the Login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        // Set OnClickListener for the Register TextView
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the SignupActivity
                Intent intent = new Intent(WelcomeBackActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleLogin() {
        String password = passwordEditText.getText().toString().trim();

        if (password.equals(VALID_PASSWORD)) {
            // Store login session and timestamp
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_logged_in", true);
            editor.putLong("login_timestamp", System.currentTimeMillis());
            editor.apply();

            // Start the DashboardActivity
            Intent intent = new Intent(WelcomeBackActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish(); // Optionally finish the WelcomeBackActivity
        } else {
            // Show error message
            Toast.makeText(WelcomeBackActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
        }
    }
}
