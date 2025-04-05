package com.example.madiotech;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;

import org.json.JSONObject;

import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DashboardActivity extends BaseActivity {

    private TextView dashboardName;
  private TextView dashboardWalletBalance;
  private TextView accountLevelTextView;
  private UserViewModel userViewModel; // ViewModel to fetch user data

    private final Handler handler = new Handler();
    private final Runnable walletBalanceRunnable = new Runnable() {
        @Override
        public void run() {
            fetchWalletBalance();
            handler.postDelayed(this, 600000); // Refresh every 10 minutes
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Find views
        dashboardName = findViewById(R.id.dashboardName);
      TextView logoutTextView = findViewById(R.id.Logout);
        dashboardWalletBalance = findViewById(R.id.dashboardWalletBallance);
        accountLevelTextView = findViewById(R.id.textViewUpgradeAccount);
      MaterialCardView cardAirtimeTopup =
          findViewById(R.id.cardAirtimeTopup);
      MaterialCardView cardBuyData = findViewById(R.id.cardBuyData);
      LinearLayout cardFundWallet = findViewById(R.id.cardFundWallet);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Observe user data from Room
        userViewModel.getUser().observe(this, user -> {
            if (user != null) {
                dashboardName.setText(user.getUsername() != null ? user.getUsername() : "Guest");
                accountLevelTextView.setText(
                    String.format("Upgrade Your Account\n(Current Level: %s)",
                        user.getUserLevel() != null ? user.getUserLevel() : "Basic"));

                // Store the API key for later use in wallet balance fetching
                userViewModel.setApiKey(user.getApiKey());
                // Fetch wallet balance immediately after user is available
                fetchWalletBalance();
            }
        });

        // Set OnClickListeners
        logoutTextView.setOnClickListener(v -> showLogoutDialog());

        cardFundWallet.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, FundwalletActivity.class)));
        cardAirtimeTopup.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, AirtimeActivity.class)));
        cardBuyData.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, BuyDataActivity.class)));

        // Start periodic wallet balance updates
        handler.post(walletBalanceRunnable);
    }

    private void fetchWalletBalance() {
        String apiKey = userViewModel.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            return; // Handle case if API key is not available
        }

        // Construct URL for the balance endpoint
        String url = "https://madiotech.com.ng/api/balance/?apikey=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();

                    // Parse the response (assuming it's JSON and contains a "wallet_balance" key)
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    // Check the response code
                    int code = jsonObject.optInt("code", -1);
                    if (code == 200) {
                        // Fetch wallet_balance from the response
                        String walletBalance = jsonObject.optString("wallet_balance", "₦0.00");

                        // Update the wallet balance in the UI on the main thread
                        runOnUiThread(() -> dashboardWalletBalance.setText("₦" + walletBalance));
                    } else {
                        // Handle case if the code is not 200 (successful)
                        runOnUiThread(() -> dashboardWalletBalance.setText("Error fetching balance"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> dashboardWalletBalance.setText("Error fetching balance"));
            }
        }).start();
    }

    private void showLogoutDialog() {
        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.confirm_logout, null);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Make background transparent
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
        new Thread(() -> AppDatabase.getInstance(getApplicationContext()).userDao().deleteAllUsers()).start();

        // Start the MainActivity
        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(walletBalanceRunnable);  // Stop periodic updates
    }
}
