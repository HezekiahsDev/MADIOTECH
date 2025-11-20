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
import android.widget.Toast;

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

  // ADDED: Declaration for the transaction history button
  private LinearLayout btnBoxTransactionHistory;

  // Double back to exit variables
  private boolean doubleBackToExitPressedOnce = false;
  private static final int BACK_PRESS_INTERVAL = 2000; // 2 seconds

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
    MaterialCardView cardAirtimeTopup = findViewById(R.id.cardAirtimeTopup);
    MaterialCardView cardBuyData = findViewById(R.id.cardBuyData);
    LinearLayout cardFundWallet = findViewById(R.id.cardFundWallet);

    // ADDED: Find the Transaction History LinearLayout by its ID
    btnBoxTransactionHistory = findViewById(R.id.btnBoxTransactionHistory);

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

    // ADDED: Set OnClickListener for the Transaction History button
    btnBoxTransactionHistory.setOnClickListener(v -> {
      startActivity(new Intent(DashboardActivity.this, TransactionsActivity.class));
    });

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
            runOnUiThread(() -> dashboardWalletBalance.setText("--"));
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        runOnUiThread(() -> dashboardWalletBalance.setText("--"));
      }
    }).start();
  }

  private void fetchRecentTransactions() {
    String apiKey = userViewModel.getApiKey();
    if (apiKey == null || apiKey.isEmpty()) {
      return; // Handle case if API key is not available
    }

    // Construct URL for the transactions endpoint
    String url = "https://madiotech.com.ng/api/user/transactions.php";

    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", apiKey) // Add the API key as the Authorization header
            .build();

    new Thread(() -> {
      try {
        Response response = client.newCall(request).execute();
        if (response.isSuccessful() && response.body() != null) {
          String jsonResponse = response.body().string();

          // Parse the response (assuming it's JSON and contains a "transactions" key)
          JSONObject jsonObject = new JSONObject(jsonResponse);

          // Check the response code
          int code = jsonObject.optInt("code", -1);
          if (code == 200) {
            // Fetch transactions from the response
            String transactions = jsonObject.optString("transactions", "[]");

            // Update the transactions section in the UI on the main thread
            runOnUiThread(() -> {
              // Assuming there is a TextView or RecyclerView to display transactions
              // Replace this comment with code to update the UI with transactions
              Toast.makeText(this, "Transactions fetched successfully", Toast.LENGTH_SHORT).show();
            });
          } else {
            // Handle case if the code is not 200 (successful)
            runOnUiThread(() -> Toast.makeText(this, "Failed to fetch transactions", Toast.LENGTH_SHORT).show());
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        runOnUiThread(() -> Toast.makeText(this, "Error fetching transactions", Toast.LENGTH_SHORT).show());
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
  public void onBackPressed() {
    if (doubleBackToExitPressedOnce) {
      super.onBackPressed();
      return;
    }

    this.doubleBackToExitPressedOnce = true;
    Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

    // Reset the flag after BACK_PRESS_INTERVAL milliseconds
    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, BACK_PRESS_INTERVAL);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    handler.removeCallbacks(walletBalanceRunnable);  // Stop periodic updates
  }
}