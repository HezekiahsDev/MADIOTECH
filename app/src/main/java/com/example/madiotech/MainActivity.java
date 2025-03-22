package com.example.madiotech;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.madiotech.api.LoginResponse;

public class MainActivity extends BaseActivity {

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Check if user exists in SQLite
        userViewModel.getUser().observe(this, new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse user) {
                if (user != null) {
                    // If user is logged in, go to Dashboard and exit MainActivity
                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // If no user is found, load login/signup UI
                    setContentView(R.layout.activity_main);
                    setupUI();
                }
            }
        });
    }

    private void setupUI() {
        // Setup UI elements after ensuring the user is not logged in
        findViewById(R.id.MainLogin).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        findViewById(R.id.MainSignup).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignupActivity.class));
        });
    }
}
