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

        // Check if a user is logged in
        userViewModel.getUser().observe(this, new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse user) {
                // --- MODIFICATION IS HERE ---

                if (user != null) {
                    // If a user exists in the local database, they are considered logged in.
                    // The 4-hour check and navigation to WelcomeBackActivity have been removed.
                    // We now go directly to the Dashboard.
                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish(); // Important: finish MainActivity so the user can't press back to it.

                } else {
                    // If no user is logged in, load the login/signup UI.
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