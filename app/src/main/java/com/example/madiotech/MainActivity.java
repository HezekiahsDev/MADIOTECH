package com.example.madiotech;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends BaseActivity {

    private Button loginButton;
    private Button signupButton;

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_LOGIN_TIMESTAMP = "login_timestamp";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final long SESSION_DURATION = 4 * 60 * 60 * 1000; // 4 hours in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        long loginTimestamp = sharedPreferences.getLong(KEY_LOGIN_TIMESTAMP, 0);
        long currentTime = System.currentTimeMillis();

        if (isLoggedIn) {
            if ((currentTime - loginTimestamp) < SESSION_DURATION) {
                // Start the DashboardActivity if the session is still valid
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return;
            } else {
                // Start the WelcomeBackActivity if the session has expired
                Intent intent = new Intent(MainActivity.this, WelcomeBackActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }

        // If not logged in, show the main activity layout
        setContentView(R.layout.activity_main);

        // Find the buttons by ID
        loginButton = findViewById(R.id.MainLogin);
        signupButton = findViewById(R.id.MainSignup);

        // Set OnClickListener for the Login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for the Signup button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the SignupActivity
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}