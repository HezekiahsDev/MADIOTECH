package com.example.madiotech;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.madiotech.api.ApiService;
import com.example.madiotech.api.LoginResponse;
import com.example.madiotech.api.RetrofitClient;
import com.example.madiotech.vtu.VtuNetwork;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AirtimeActivity extends BaseActivity {

    private Spinner spinnerNetworkProviders;
    private Spinner spinnerAirtimeType;
    private EditText editTextAirtimeAmount;
    private EditText editTextAirtimePhonenumber;
    private Button textViewProceedPurchase;
    private UserViewModel userViewModel;
    private List<VtuNetwork> vtuNetworks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airtime);

        // Initialize views
        spinnerNetworkProviders = findViewById(R.id.spinnerNetworkProviders);
        spinnerAirtimeType = findViewById(R.id.spinnerAirtimeType);
        editTextAirtimeAmount = findViewById(R.id.editTextAirtimeAmount);
        editTextAirtimePhonenumber = findViewById(R.id.editTextAirtimePhonenumber);
        textViewProceedPurchase = findViewById(R.id.textViewProceedPurchase);

        // Airtime types (still static)
        ArrayAdapter<CharSequence> airtimeTypeAdapter = ArrayAdapter.createFromResource(this,
            R.array.airtime_types, android.R.layout.simple_spinner_item);
        airtimeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAirtimeType.setAdapter(airtimeTypeAdapter);

        // Setup ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Observe user login data to get API key
        userViewModel.getUser().observe(this, new Observer<LoginResponse>() {
            @Override
            public void onChanged(LoginResponse loginResponse) {
                if (loginResponse != null) {
                    fetchNetworkProviders(loginResponse.getApiKey());
                }
            }
        });

        textViewProceedPurchase.setOnClickListener(v -> proceedWithPurchase());
    }

    private void fetchNetworkProviders(String apiKey) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        String bearerToken = "Bearer " + apiKey;

        apiService.fetchVtuNetworks(bearerToken).enqueue(new Callback<List<VtuNetwork>>() {
            @Override
            public void onResponse(Call<List<VtuNetwork>> call, Response<List<VtuNetwork>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    vtuNetworks = response.body();

                    List<String> networkNames = new ArrayList<>();
                    for (VtuNetwork network : vtuNetworks) {
                        networkNames.add(network.getNetwork()); // You can also use getNetworkCode()
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AirtimeActivity.this,
                        android.R.layout.simple_spinner_item, networkNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerNetworkProviders.setAdapter(adapter);
                } else {
                    Toast.makeText(AirtimeActivity.this, "Failed to load networks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<VtuNetwork>> call, Throwable t) {
                Toast.makeText(AirtimeActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedWithPurchase() {
        String selectedNetworkProvider = spinnerNetworkProviders.getSelectedItem().toString();
        String selectedAirtimeType = spinnerAirtimeType.getSelectedItem().toString();
        String airtimeAmount = editTextAirtimeAmount.getText().toString();
        String phoneNumber = editTextAirtimePhonenumber.getText().toString();

        // TODO: Use selected values for purchase logic
        Toast.makeText(this, "Selected: " + selectedNetworkProvider, Toast.LENGTH_SHORT).show();
    }
}
