package com.example.madiotech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class DashboardActivity extends BaseActivity {

    private TextView dashboardName;
    private TextView logoutTextView;
    private TextView dashboardWalletBalance;
    private TextView accountLevelTextView;
    private LinearLayout btnBoxAirtimeTopup;
    private LinearLayout btnBoxBuyData;
    private LinearLayout btnBoxFundWallet;

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Find the views by ID
        dashboardName = findViewById(R.id.dashboardName);
        logoutTextView = findViewById(R.id.Logout); // Correct initialization
        dashboardWalletBalance = findViewById(R.id.dashboardWalletBallance);
        accountLevelTextView = findViewById(R.id.textViewUpgradeAccount);
        btnBoxAirtimeTopup = findViewById(R.id.btnBoxAirtimeTopup);
        btnBoxBuyData = findViewById(R.id.btnBoxBuyData);
        btnBoxFundWallet = findViewById(R.id.btnBoxFundWallet);

        // Retrieve the username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString(KEY_USERNAME, "User");
        dashboardName.setText(username);

        // Retrieve the wallet balance for the user
        double walletBalance = getWalletBalance(username);
        // Update the TextView with the retrieved balance
        dashboardWalletBalance.setText(String.format(Locale.getDefault(), "%.2f", walletBalance));

        // Retrieve the user account level
        String accountLevel = getAccountLevel(username);
        accountLevelTextView.setText(String.format("Upgrade Your Account\n(Current Level: %s)", accountLevel));

        // Set OnClickListeners
        // Set OnClickListener for the Logout button
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });

        // Set OnClickListener for btnBoxAirtimeTopup
        btnBoxFundWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, FundwalletActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for btnBoxAirtimeTopup
        btnBoxAirtimeTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, AirtimeActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for btnBoxAirtimeTopup
        btnBoxBuyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, BuyDataActivity.class);
                startActivity(intent);
            }
        });

    }

    // Functions
    private double getWalletBalance(String username) {
        // Retrieve wallet balance from DB
        // Query DB through API endpoint
        return 100000; // Assumed value for demonstration
    }

    private String getAccountLevel(String username) {
        // Simulate server retrieval with a dummy value
        // In a real application, perform a network request or query your database
        return "Reseller"; // Assumed value for demonstration
    }

    private void handleLogout() {
        // Clear login session and username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Start the MainActivity
        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Optionally finish the DashboardActivity
    }
}
