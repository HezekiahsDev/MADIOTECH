// File: app/src/main/java/com/example/madiotech/TransactionsActivity.java
package com.example.madiotech;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.madiotech.api.ApiService;
import com.example.madiotech.api.RetrofitClient;
import com.example.madiotech.data.Transactions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsActivity extends AppCompatActivity {

    private static final String API_LOG_TAG = "API_TRANSACTIONS";
    private static final String DEBUG_TAG = "AdapterDebug";

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerTransactions;
    private TextView textEmpty;
    private TextView tvSummaryValue;
//    private SearchView searchView;
//    private ImageButton btnSearch;

    private TransactionAdapter transactionAdapter;
    private final List<Transactions> transactionList = new ArrayList<>(); // Use final to ensure the reference never changes
    private UserViewModel userViewModel;
    private ApiService apiService;
    private String userApiKey; // Store the API key locally

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        apiService = RetrofitClient.getInstance().create(ApiService.class);

        initializeViews();
        setupRecyclerView();
        setupListeners();
        observeUserData();
    }

    private void initializeViews() {
        swipeRefresh = findViewById(R.id.swipeRefresh);
        recyclerTransactions = findViewById(R.id.recyclerTransactions);
        textEmpty = findViewById(R.id.textEmpty);
        tvSummaryValue = findViewById(R.id.tvSummaryValue);
//        searchView = findViewById(R.id.searchView);
//        btnSearch = findViewById(R.id.btnSearch);
    }

    private void setupRecyclerView() {
        recyclerTransactions.setLayoutManager(new LinearLayoutManager(this));
        // Pass the activity's list to the adapter. They will now share the same list reference.
        transactionAdapter = new TransactionAdapter(this, transactionList);
        recyclerTransactions.setAdapter(transactionAdapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(() -> {
            if (userApiKey != null) {
                fetchTransactions(userApiKey);
            } else {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(this, "Cannot refresh, user data not available.", Toast.LENGTH_SHORT).show();
            }
        });

//        // When the search icon is clicked, hide it and show the SearchView
//        btnSearch.setOnClickListener(view -> {
//            btnSearch.setVisibility(View.GONE);
//            searchView.setVisibility(View.VISIBLE);
//            searchView.requestFocus(); // Automatically focus and open keyboard
//        });

//        // When the user closes the SearchView (clicks the 'X' button)
//        searchView.setOnCloseListener(() -> {
//            searchView.setQuery("", false); // Clear the text
//            searchView.setVisibility(View.GONE);
//            btnSearch.setVisibility(View.VISIBLE);
//            return true; // We've handled the event
//        });

//        // This listener handles the real-time filtering as the user types
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                transactionAdapter.getFilter().filter(newText);
//                // If the user clears the text, closing the keyboard can be a nice touch
//                // but we won't close the search view itself unless they hit the 'X'
//                return true;
//            }
//        });
    }

    private void observeUserData() {
        userViewModel.getUser().observe(this, user -> {
            if (user != null && user.getApiKey() != null) {
                this.userApiKey = user.getApiKey(); // Store the key
                fetchTransactions(this.userApiKey);
            } else {
                Toast.makeText(this, "Could not get user credentials.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTransactions(String apiKey) {
        swipeRefresh.setRefreshing(true);
        String bearerToken = "Bearer " + apiKey;

        Log.d(API_LOG_TAG, "--> Sending Request to fetch transactions...");
        apiService.getTransactions(bearerToken).enqueue(new Callback<List<Transactions>>() {
            @Override
            public void onResponse(Call<List<Transactions>> call, Response<List<Transactions>> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(API_LOG_TAG, "SUCCESS: Received " + response.body().size() + " transactions.");

                    // *** THE CRITICAL FIX IS HERE ***
                    // 1. Update the Activity's list. This is the single source of truth.
                    transactionList.clear();
                    transactionList.addAll(response.body());

                    // 2. Notify the adapter that the underlying data has changed completely.
                    //    We don't need a separate updateList method in the adapter if they share the same list reference.
                    transactionAdapter.notifyDataSetChanged();

                    // 3. Update the UI based on the new state of the list.
                    updateUiState();

                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<List<Transactions>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Log.e(API_LOG_TAG, "API call failed completely.", t);
                Toast.makeText(TransactionsActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                updateUiState(); // Still update UI to show empty state
            }
        });
    }

    private void updateUiState() {
        Log.d(DEBUG_TAG, "updateUiState() called. transactionList size is: " + transactionList.size());
        if (transactionList.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
            recyclerTransactions.setVisibility(View.GONE);
            tvSummaryValue.setText("₦0.00");
        } else {
            textEmpty.setVisibility(View.GONE);
            recyclerTransactions.setVisibility(View.VISIBLE);
            calculateAndSetSummary();
        }
    }

    private void handleApiError(Response<List<Transactions>> response) {
        String errorBody = "Unknown error";
        try {
            if (response.errorBody() != null) {
                errorBody = response.errorBody().string();
            }
        } catch (IOException e) {
            Log.e(API_LOG_TAG, "Error reading error body", e);
        }
        Log.e(API_LOG_TAG, "API Error: " + response.code() + " - " + errorBody);
        Toast.makeText(TransactionsActivity.this, "Failed to load transactions (Code: " + response.code() + ")", Toast.LENGTH_LONG).show();
        updateUiState();
    }

    private void calculateAndSetSummary() {
        double total = 0.0;
        for (Transactions t : transactionList) {
            if (t.getStatus() != null && t.getStatus().equalsIgnoreCase("successful")) {
                try {
                    total += Double.parseDouble(t.getAmount());
                } catch (NumberFormatException e) {
                    Log.e("TransactionSummary", "Could not parse amount: " + t.getAmount());
                }
            }
        }
        tvSummaryValue.setText(String.format(Locale.getDefault(), "₦%,.2f", total));
    }
}