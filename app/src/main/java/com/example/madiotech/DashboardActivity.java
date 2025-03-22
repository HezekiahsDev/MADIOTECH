package com.example.madiotech;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.madiotech.api.LoginResponse;

public class DashboardActivity extends BaseActivity {

    private TextView dashboardName, dashboardWalletBalance, accountLevelTextView, logoutTextView;
    private LinearLayout btnBoxAirtimeTopup, btnBoxBuyData, btnBoxFundWallet;
    private UserViewModel userViewModel; // ViewModel to fetch user data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Find views
        dashboardName = findViewById(R.id.dashboardName);
        logoutTextView = findViewById(R.id.Logout);
        dashboardWalletBalance = findViewById(R.id.dashboardWalletBallance);
        accountLevelTextView = findViewById(R.id.textViewUpgradeAccount);
        btnBoxAirtimeTopup = findViewById(R.id.btnBoxAirtimeTopup);
        btnBoxBuyData = findViewById(R.id.btnBoxBuyData);
        btnBoxFundWallet = findViewById(R.id.btnBoxFundWallet);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Observe user data from Room
        userViewModel.getUser().observe(this, new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse user) {
                if (user != null) {
                    dashboardName.setText(user.getUsername());
                    dashboardWalletBalance.setText(user.getWallet());
                    accountLevelTextView.setText(String.format("Upgrade Your Account\n(Current Level: %s)", user.getUserLevel()));
                }
            }
        });

        // Set OnClickListeners
        logoutTextView.setOnClickListener(v -> showLogoutDialog()); // Updated this line!

        btnBoxFundWallet.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, FundwalletActivity.class)));
        btnBoxAirtimeTopup.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, AirtimeActivity.class)));
        btnBoxBuyData.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, BuyDataActivity.class)));
    }

    private void showLogoutDialog() {
        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.confirm_logout, null);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Make background transparent
        dialog.show();

        // Find buttons inside the dialog
        Button btnGoBack = dialogView.findViewById(R.id.buttonConfirmLogoutGoBack);
        Button btnLogout = dialogView.findViewById(R.id.buttonConfirmLogout);

        // Handle "Go Back" button
        btnGoBack.setOnClickListener(v -> dialog.dismiss());

        // Handle "Log Out" button
        btnLogout.setOnClickListener(v -> {
            dialog.dismiss();
            handleLogout(); // Call logout method after closing dialog
        });
    }

    private void handleLogout() {
        // Clear user data from Room
        new Thread(() -> {
            AppDatabase.getInstance(getApplicationContext()).userDao().deleteAllUsers();
        }).start();

        // Start the MainActivity
        startActivity(new Intent(DashboardActivity.this, MainActivity.class));
        finish();
    }
}
