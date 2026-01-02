package com.example.madiotech;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.example.madiotech.api.ApiService;
import com.example.madiotech.api.RetrofitClient;
import com.example.madiotech.api.LoginResponse;
import com.example.madiotech.data.DataPlan;
import com.example.madiotech.data.FetchDataResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyDataActivity extends BaseActivity {

    private Spinner spinnerNetworkProviders, spinnerBundleType, spinnerBundleAmount;
    private EditText editTextDataPhonenumber;
    // keep button local to onCreate to reduce field-level warning
    // private Button textViewProceedPurchase;

    private UserViewModel userViewModel;

    private Map<String, Map<String, List<DataPlan>>> fullDataMap = new HashMap<>();
    private List<DataPlan> currentPlans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buydata);

        // Init Views
        spinnerNetworkProviders = findViewById(R.id.spinnerNetworkProviders);
        spinnerBundleType = findViewById(R.id.spinnerBundleType);
        spinnerBundleAmount = findViewById(R.id.spinnerBundleAmount);
        editTextDataPhonenumber = findViewById(R.id.editTextDataPhonenumber);
        Button textViewProceedPurchase = findViewById(R.id.textViewProceedPurchase);

        // ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getUser().observe(this, loginResponse -> {
            if (loginResponse != null && loginResponse.getApiKey() != null) {
                fetchBundleData("Bearer " + loginResponse.getApiKey());
            }
        });

        spinnerNetworkProviders.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                updateBundleTypes(spinnerNetworkProviders.getSelectedItem().toString());
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerBundleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                updateBundleAmounts(
                    spinnerNetworkProviders.getSelectedItem().toString(),
                    spinnerBundleType.getSelectedItem().toString()
                );
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        textViewProceedPurchase.setOnClickListener(v -> proceedWithPurchase());
    }

    private void fetchBundleData(String bearerToken) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.fetchDataBundles(bearerToken).enqueue(new Callback<FetchDataResponse>() {
            @Override
            public void onResponse(Call<FetchDataResponse> call, Response<FetchDataResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Adjust this depending on your FetchDataResponse structure
                    fullDataMap = response.body(); // Ensure getData() returns Map<String, Map<String, List<DataPlan>>>

                    List<String> networks = new ArrayList<>(fullDataMap.keySet());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(BuyDataActivity.this, android.R.layout.simple_spinner_item, networks);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerNetworkProviders.setAdapter(adapter);
                } else {
                    Toast.makeText(BuyDataActivity.this, "Failed to parse data plans", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FetchDataResponse> call, Throwable t) {
                Toast.makeText(BuyDataActivity.this, "Failed to fetch data plans", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBundleTypes(String selectedNetwork) {
        if (fullDataMap != null) {
            Map<String, List<DataPlan>> bundleTypes = fullDataMap.get(selectedNetwork);
            if (bundleTypes != null) {
                List<String> types = new ArrayList<>(bundleTypes.keySet());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerBundleType.setAdapter(adapter);
            }
        }
    }

    private void updateBundleAmounts(String network, String type) {
        if (fullDataMap != null) {
            Map<String, List<DataPlan>> networkMap = fullDataMap.get(network);
            if (networkMap != null) {
                List<DataPlan> plans = networkMap.get(type);
                if (plans != null) {
                    currentPlans = plans;
                    List<String> amountList = new ArrayList<>();
                    for (DataPlan plan : currentPlans) {
                        amountList.add(plan.plan + " " + plan.size + " - ₦" + plan.amount);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, amountList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBundleAmount.setAdapter(adapter);
                }
            }
        }
    }

    private void proceedWithPurchase() {
        if (spinnerNetworkProviders.getSelectedItem() == null ||
            spinnerBundleType.getSelectedItem() == null ||
            spinnerBundleAmount.getSelectedItem() == null) {
            Toast.makeText(this, "Please select all options", Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumber = editTextDataPhonenumber.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPlanIndex = spinnerBundleAmount.getSelectedItemPosition();
        if (currentPlans == null || selectedPlanIndex < 0 || selectedPlanIndex >= currentPlans.size()) {
            Toast.makeText(this, "Invalid plan selected", Toast.LENGTH_SHORT).show();
            return;
        }
        DataPlan selectedPlan = currentPlans.get(selectedPlanIndex);

        // Use API key from session stored in ViewModel's observed user
        LoginResponse sessionUser = null;
        if (userViewModel.getUser() != null && userViewModel.getUser().getValue() != null) {
            sessionUser = userViewModel.getUser().getValue();
        }

        if (sessionUser == null || sessionUser.getApiKey() == null || sessionUser.getApiKey().isEmpty()) {
            Toast.makeText(this, "No API key found. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String apiKey = sessionUser.getApiKey();
        String productCode = selectedPlan.plan_code; // plan_code used as product_code
        if (productCode == null || productCode.isEmpty()) {
            Toast.makeText(this, "Selected plan has no product code", Toast.LENGTH_SHORT).show();
            return;
        }
        String networkCode = spinnerNetworkProviders.getSelectedItem().toString();

        Toast.makeText(this, "Purchasing: " + selectedPlan.plan + " for " + phoneNumber, Toast.LENGTH_SHORT).show();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        // Call buyData endpoint
        apiService.buyData(apiKey, productCode, phoneNumber, networkCode).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody responseBody = response.body();
                    if (response.isSuccessful() && responseBody != null) {
                        try (ResponseBody body = responseBody) {
                            String bodyStr = body.string();
                            // Try to parse JSON that looks like: {"code":409,"status":"failed","message":"..."}
                            try {
                                ApiResult apiResult = new Gson().fromJson(bodyStr, ApiResult.class);
                                if (apiResult != null) {
                                    if ("success".equalsIgnoreCase(apiResult.status) || apiResult.code == 200) {
                                        showServerMessageDialog("Purchase successful", apiResult.message != null ? apiResult.message : "Purchase successful");
                                    } else {
                                        // Server returned an application-level failure (e.g., code 409)
                                        showServerMessageDialog("Purchase failed", apiResult.message != null ? apiResult.message : "Purchase failed");
                                    }
                                } else {
                                    // Not parsable into ApiResult; show raw
                                    showServerMessageDialog("Purchase", bodyStr);
                                }
                            } catch (JsonSyntaxException jse) {
                                // Response not JSON — show raw body
                                showServerMessageDialog("Purchase", bodyStr);
                            }
                        }
                    } else {
                        String err = "Purchase failed";
                        ResponseBody rb = response.errorBody();
                        if (rb != null) {
                            try (ResponseBody resBody = rb) {
                                err = resBody.string();
                                // try parsing error body for message
                                try {
                                    ApiResult apiErr = new Gson().fromJson(err, ApiResult.class);
                                    if (apiErr != null && apiErr.message != null) {
                                        err = apiErr.message;
                                    }
                                } catch (Exception ignore) {}
                            } catch (IOException ioe) {
                                // error reading error body
                            }
                        }
                        showServerMessageDialog("Purchase failed", err);
                    }
                } catch (IOException ioe) {
                    showServerMessageDialog("Purchase error", "I/O error reading response: " + ioe.getMessage());
                } catch (Exception e) {
                    showServerMessageDialog("Purchase error", "Unexpected response handling error");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showServerMessageDialog("Network error", "Purchase request failed: " + t.getMessage());
            }
        });
    }

    // Show server message in a dialog (used for purchase responses/errors)
    private void showServerMessageDialog(String title, String message) {
        if (message == null || message.isEmpty()) message = "No message returned from server.";
        new AlertDialog.Builder(BuyDataActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Simple model for common API responses from madiotech endpoints
    private static class ApiResult {
        public int code;
        public String status;
        public String message;
    }
 }
