package com.example.madiotech;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AirtimeActivity extends BaseActivity {

    private Spinner spinnerNetworkProviders;
    private Spinner spinnerAirtimeType;
    private EditText editTextAirtimeAmount;
    private EditText editTextAirtimePhonenumber;
    private Button textViewProceedPurchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airtime);

        // Initialize the views
        spinnerNetworkProviders = findViewById(R.id.spinnerNetworkProviders);
        spinnerAirtimeType = findViewById(R.id.spinnerAirtimeType);
        editTextAirtimeAmount = findViewById(R.id.editTextAirtimeAmount);
        editTextAirtimePhonenumber = findViewById(R.id.editTextAirtimePhonenumber);
        textViewProceedPurchase = findViewById(R.id.textViewProceedPurchase);

        // Set up the spinners
        ArrayAdapter<CharSequence> networkProvidersAdapter = ArrayAdapter.createFromResource(this,
                R.array.network_providers, android.R.layout.simple_spinner_item);
        networkProvidersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNetworkProviders.setAdapter(networkProvidersAdapter);

        ArrayAdapter<CharSequence> airtimeTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.airtime_types, android.R.layout.simple_spinner_item);
        airtimeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAirtimeType.setAdapter(airtimeTypeAdapter);

        // Set up the proceed button
        textViewProceedPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedWithPurchase();
            }
        });
    }

    private void proceedWithPurchase() {
        // Implement the logic to proceed with the airtime purchase
        // You can retrieve the selected items from the spinners and the entered values from the EditTexts
        String selectedNetworkProvider = spinnerNetworkProviders.getSelectedItem().toString();
        String selectedAirtimeType = spinnerAirtimeType.getSelectedItem().toString();
        String airtimeAmount = editTextAirtimeAmount.getText().toString();
        String phoneNumber = editTextAirtimePhonenumber.getText().toString();

        // Implement your purchase logic here
    }
}