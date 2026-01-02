package com.example.madiotech;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madiotech.data.Transactions;
import com.google.android.material.card.MaterialCardView;
import com.example.madiotech.api.ApiService;
import com.example.madiotech.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;


public class HomeFragment extends Fragment {

    private TextView dashboardName;
    private TextView dashboardWalletBalance;
    private TextView accountLevelTextView;

    private UserViewModel userViewModel;
    private RecyclerView recyclerRecentTransactions;
    private RecentTransactionAdapter recentTransactionAdapter;
    private TextView textRecentSeeAll;
    private TextView textRecentEmpty;
    private List<Transactions> recentTransactions = new ArrayList<>();

    private LinearLayout btnBoxTransactionHistory;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable walletBalanceRunnable = new Runnable() {
        @Override
        public void run() {
            fetchWalletBalance();
            handler.postDelayed(this, 60000); // Refresh every 1 minute
        }
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        dashboardName = view.findViewById(R.id.dashboardName);
        dashboardWalletBalance = view.findViewById(R.id.dashboardWalletBallance);
        accountLevelTextView = view.findViewById(R.id.textViewUpgradeAccount);
        MaterialCardView cardAirtimeTopup = view.findViewById(R.id.cardAirtimeTopup);
        MaterialCardView cardBuyData = view.findViewById(R.id.cardBuyData);
        LinearLayout cardFundWallet = view.findViewById(R.id.cardFundWallet);
        btnBoxTransactionHistory = view.findViewById(R.id.btnBoxTransactionHistory);
        recyclerRecentTransactions = view.findViewById(R.id.recyclerRecentTransactions);
        textRecentEmpty = view.findViewById(R.id.textRecentEmpty);
        textRecentSeeAll = view.findViewById(R.id.textRecentSeeAll);

        setupRecyclerView();

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Observe user data from Room
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                dashboardName.setText(user.getUsername() != null ? user.getUsername() : "Guest");
                String mappedLevel = mapUserLevel(user.getUserLevel());
                accountLevelTextView.setText(
                        String.format("Upgrade Your Account\n(Current Level: %s)", mappedLevel));

                // Store the API key for later use in wallet balance fetching
                userViewModel.setApiKey(user.getApiKey());
                // Fetch wallet balance immediately after user is available
                fetchWalletBalance();
                fetchRecentTransactions();
            }
        });

        // Set OnClickListeners
        cardFundWallet.setOnClickListener(v -> startActivity(new Intent(getActivity(), FundwalletActivity.class)));
        cardAirtimeTopup.setOnClickListener(v -> startActivity(new Intent(getActivity(), AirtimeActivity.class)));
        cardBuyData.setOnClickListener(v -> startActivity(new Intent(getActivity(), BuyDataActivity.class)));

        btnBoxTransactionHistory.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TransactionsActivity.class));
        });
        textRecentSeeAll.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TransactionsActivity.class));
        });

        // Start periodic wallet balance updates
        handler.post(walletBalanceRunnable);
    }

    private void setupRecyclerView() {
        recentTransactionAdapter = new RecentTransactionAdapter(getContext(), recentTransactions);
        recyclerRecentTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerRecentTransactions.setAdapter(recentTransactionAdapter);
    }

    private void fetchWalletBalance() {
        String apiKey = userViewModel.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            return;
        }

        String url = "https://madiotech.com.ng/api/balance/?apikey=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        new Thread(() -> {
            try {
                okhttp3.Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    int code = jsonObject.optInt("code", -1);
                    if (code == 200) {
                        String walletBalance = jsonObject.optString("wallet_balance", "₦0.00");
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> dashboardWalletBalance.setText("₦" + walletBalance));
                        }
                    } else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> dashboardWalletBalance.setText("--"));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> dashboardWalletBalance.setText("--"));
                }
            }
        }).start();
    }

    private void fetchRecentTransactions() {
        String apiKey = userViewModel.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            // No API key: clear UI
            recentTransactions.clear();
            recentTransactionAdapter.updateData(recentTransactions);
            updateRecentUi();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        String bearer = "Bearer " + apiKey;

        apiService.getTransactions(bearer).enqueue(new Callback<List<Transactions>>() {
            @Override
            public void onResponse(Call<List<Transactions>> call, Response<List<Transactions>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Transactions> list = response.body();
                    // Limit to maximum 5 recent transactions
                    List<Transactions> subList = list.size() > 5 ? list.subList(0, 5) : list;
                    // Update the local list and adapter
                    recentTransactions.clear();
                    recentTransactions.addAll(subList);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            recentTransactionAdapter.updateData(recentTransactions);
                            updateRecentUi();
                        });
                    }
                } else {
                    // Clear on failure
                    recentTransactions.clear();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            recentTransactionAdapter.updateData(recentTransactions);
                            updateRecentUi();
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Transactions>> call, Throwable t) {
                t.printStackTrace();
                recentTransactions.clear();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        recentTransactionAdapter.updateData(recentTransactions);
                        updateRecentUi();
                    });
                }
            }
        });
    }

    // Toggle recent transactions UI when data changes
    private void updateRecentUi() {
        if (recentTransactions == null || recentTransactions.isEmpty()) {
            textRecentEmpty.setVisibility(View.VISIBLE);
            recyclerRecentTransactions.setVisibility(View.GONE);
        } else {
            textRecentEmpty.setVisibility(View.GONE);
            recyclerRecentTransactions.setVisibility(View.VISIBLE);
        }
    }


    // Map numeric or string user levels to human-friendly labels
    private String mapUserLevel(String level) {
        if (level == null) return "new user"; // default if null
        switch (level.trim()) {
            case "1":
                return "new user";
            case "2":
                return "Basic user";
            case "3":
                return "Reseller";
            case "4":
                return "Partner";
            case "5":
                return "Premium";
            default:
                // If the stored value is already a label, return it with capitalization fallback
                String lower = level.toLowerCase();
                if (lower.contains("new") || lower.contains("basic") || lower.contains("reseller") || lower.contains("partner") || lower.contains("premium")) {
                    return level;
                }
                // Fallback: treat unknown numeric as "new user"
                return "new user";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(walletBalanceRunnable);
    }
}