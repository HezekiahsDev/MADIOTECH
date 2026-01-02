package com.example.madiotech;

import android.os.Bundle;
import android.text.TextUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private String currentApiKey; // store API key when available

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
                    currentApiKey = loginResponse.getApiKey();
                    fetchNetworkProviders(currentApiKey);
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
        if (vtuNetworks == null || vtuNetworks.isEmpty()) {
            Toast.makeText(this, "Network providers not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPosition = spinnerNetworkProviders.getSelectedItemPosition();
        if (selectedPosition < 0 || selectedPosition >= vtuNetworks.size()) {
            Toast.makeText(this, "Please select a valid network provider", Toast.LENGTH_SHORT).show();
            return;
        }

        VtuNetwork selectedNetwork = vtuNetworks.get(selectedPosition);
        String networkCode = selectedNetwork.getNetworkCode();
        String selectedAirtimeType = spinnerAirtimeType.getSelectedItem() != null
            ? spinnerAirtimeType.getSelectedItem().toString() : "";
        String airtimeAmount = editTextAirtimeAmount.getText().toString().trim();
        String phoneNumber = editTextAirtimePhonenumber.getText().toString().trim();

        // Basic validation
        if (TextUtils.isEmpty(airtimeAmount)) {
            editTextAirtimeAmount.setError("Enter amount");
            editTextAirtimeAmount.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            editTextAirtimePhonenumber.setError("Enter phone number");
            editTextAirtimePhonenumber.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(currentApiKey)) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call buy airtime
        buyAirtime(currentApiKey, airtimeAmount, phoneNumber, networkCode);
    }

    private void buyAirtime(String apiKey, String amount, String phone, String networkCode) {
        // Disable the purchase button until request completes to avoid duplicate submissions
        textViewProceedPurchase.setEnabled(false);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        apiService.buyAirtime(apiKey, amount, phone, networkCode).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // Re-enable the button
                textViewProceedPurchase.setEnabled(true);

                int code = response.code();
                if (code == 200 || code == 201) {
                    String body = response.body();
                    Toast.makeText(AirtimeActivity.this, "Purchase success: " + (body != null ? body : ""), Toast.LENGTH_LONG).show();
                } else {
                    // Try to extract error message from the response body if present
                    String errMsg = response.message();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            if (!errorBody.isEmpty()) {
                                // Prefer backend 'message' field if JSON
                                try {
                                    JSONObject obj = new JSONObject(errorBody);
                                    if (obj.has("message")) {
                                        errMsg = obj.optString("message", errMsg);
                                    } else if (obj.has("error")) {
                                        errMsg = obj.optString("error", errMsg);
                                    } else if (obj.has("status")) {
                                        // sometimes message can be under another field
                                        String st = obj.optString("status", null);
                                        if (st != null && !st.isEmpty()) {
                                            errMsg = st;
                                        } else {
                                            errMsg = errorBody;
                                        }
                                    } else {
                                        errMsg = errorBody;
                                    }
                                } catch (JSONException je) {
                                    // Not JSON, use raw body
                                    errMsg = errorBody;
                                }
                            }
                        }
                    } catch (IOException e) {
                        // ignore and fall back to response.message()
                    }

                    // Fallback to friendly messages for common status codes when backend didn't provide one
                    if (errMsg == null || errMsg.trim().isEmpty()) {
                        switch (code) {
                            case 400:
                                errMsg = "Bad request. Please check your input and try again.";
                                break;
                            case 401:
                                errMsg = "Authentication failed. Please log in again.";
                                break;
                            case 403:
                                errMsg = "You don't have permission to perform this action.";
                                break;
                            case 404:
                                errMsg = "Requested resource not found.";
                                break;
                            case 500:
                                errMsg = "Server error. Please try again later.";
                                break;
                            default:
                                errMsg = "An error occurred (code: " + code + "). Please try again.";
                                break;
                        }
                    }

                    Toast.makeText(AirtimeActivity.this, "Purchase failed (" + code + "): " + errMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Re-enable the button
                textViewProceedPurchase.setEnabled(true);

                String msg;
                if (t instanceof IOException) {
                    // Network or conversion error
                    msg = "Network error. Please check your internet connection and try again.";
                } else {
                    // Non-IO exception (conversion issue, unexpected)
                    msg = "Unexpected error: " + t.getMessage();
                }
                Toast.makeText(AirtimeActivity.this, "Purchase error: " + msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
