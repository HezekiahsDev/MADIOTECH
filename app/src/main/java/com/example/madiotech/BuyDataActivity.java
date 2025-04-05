package com.example.madiotech;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.madiotech.api.ApiService;
import com.example.madiotech.api.RetrofitClient;
import com.example.madiotech.data.DataPlan;
import com.example.madiotech.data.FetchDataResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyDataActivity extends BaseActivity {

    private Spinner spinnerNetworkProviders, spinnerBundleType, spinnerBundleAmount;
    private EditText editTextDataPhonenumber;
    private Button textViewProceedPurchase;

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
        textViewProceedPurchase = findViewById(R.id.textViewProceedPurchase);

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
        if (fullDataMap != null && fullDataMap.containsKey(selectedNetwork)) {
            Map<String, List<DataPlan>> bundleTypes = fullDataMap.get(selectedNetwork);
            List<String> types = new ArrayList<>(bundleTypes.keySet());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBundleType.setAdapter(adapter);
        }
    }

    private void updateBundleAmounts(String network, String type) {
        if (fullDataMap != null
            && fullDataMap.containsKey(network)
            && fullDataMap.get(network).containsKey(type)) {

            currentPlans = fullDataMap.get(network).get(type);
            List<String> amountList = new ArrayList<>();
            for (DataPlan plan : currentPlans) {
                amountList.add(plan.plan + " " + plan.size + " - ₦" + plan.amount);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, amountList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBundleAmount.setAdapter(adapter);
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
        DataPlan selectedPlan = currentPlans.get(selectedPlanIndex);

        // Proceed with API call to purchase using selectedPlan.plan_code or whatever identifier you use
        Toast.makeText(this, "Selected plan: " + selectedPlan.plan + "\nPhone: " + phoneNumber, Toast.LENGTH_LONG).show();

        // TODO: Make purchase API call here with selectedPlan and phoneNumber
    }
}
