package com.example.madiotech;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class BuyDataActivity extends BaseActivity {

    private Spinner spinnerNetworkProviders;
    private Spinner spinnerBundleType;
    private Spinner spinnerBundleAmount;
    private EditText editTextDataPhonenumber;
    private Button textViewProceedPurchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buydata);

        // Initialize the views
        spinnerNetworkProviders = findViewById(R.id.spinnerNetworkProviders);
        spinnerBundleType = findViewById(R.id.spinnerBundleType);
        spinnerBundleAmount = findViewById(R.id.spinnerBundleAmount);
        editTextDataPhonenumber = findViewById(R.id.editTextDataPhonenumber);
        textViewProceedPurchase = findViewById(R.id.textViewProceedPurchase);

        // Set up the spinners
        ArrayAdapter<CharSequence> networkProvidersAdapter = ArrayAdapter.createFromResource(this,
                R.array.network_providers, android.R.layout.simple_spinner_item);
        networkProvidersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNetworkProviders.setAdapter(networkProvidersAdapter);

        ArrayAdapter<CharSequence> bundleTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.bundle_types, android.R.layout.simple_spinner_item);
        bundleTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBundleType.setAdapter(bundleTypeAdapter);

        ArrayAdapter<CharSequence> bundleAmountAdapter = ArrayAdapter.createFromResource(this,
                R.array.bundle_amounts, android.R.layout.simple_spinner_item);
        bundleAmountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBundleAmount.setAdapter(bundleAmountAdapter);

        // Set up the proceed button
        textViewProceedPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedWithPurchase();
            }
        });
    }

    private void proceedWithPurchase() {
        // Implement the logic to proceed with the data bundle purchase
        // You can retrieve the selected items from the spinners and the entered values from the EditTexts
        String selectedNetworkProvider = spinnerNetworkProviders.getSelectedItem().toString();
        String selectedBundleType = spinnerBundleType.getSelectedItem().toString();
        String selectedBundleAmount = spinnerBundleAmount.getSelectedItem().toString();
        String phoneNumber = editTextDataPhonenumber.getText().toString();

        // Implement your purchase logic here
    }
}