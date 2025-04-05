package com.example.madiotech;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.madiotech.api.ApiService;
import com.example.madiotech.api.RegisterRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    private EditText firstNameEditText, lastNameEditText, usernameEditText, referEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private Retrofit retrofit;
    private ApiService apiService;
    private View loginSwitchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firstNameEditText = findViewById(R.id.editTextFirstName);
        lastNameEditText = findViewById(R.id.editTextLastName);
        usernameEditText = findViewById(R.id.editTextUsername);
        referEditText = findViewById(R.id.editTextReferalCode);
        emailEditText = findViewById(R.id.editTextEmailAddress);
        phoneEditText = findViewById(R.id.editTextPhoneNumber);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        registerButton = findViewById(R.id.signupPageSignUp);
        loginSwitchButton =
            findViewById(R.id.textClickableViewLogin);

        // Initialize Retrofit
        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://madiotech.com.ng/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegistration();
            }
        });

        loginSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect the user to the LoginActivity
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();  // Close the current SignupActivity
            }
        });
    }

    private void handleRegistration() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String fulname = firstName + " " + lastName;
        String username = usernameEditText.getText().toString().trim();
        String refer = referEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String pass1 = passwordEditText.getText().toString().trim();
        String pass2 = confirmPasswordEditText.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || phone.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass1.equals(pass2)) {
            Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest request = new RegisterRequest(fulname, username, refer, email, phone, pass1, pass2);
        Call<String> call = apiService.registerUser(request);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String serverResponse = response.body();
                    Log.d("API_RESPONSE", "Response: " + serverResponse);

                    if ("success".equalsIgnoreCase(serverResponse.trim())) {
                        Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showErrorDialog("Registration failed: " + serverResponse);
                    }
                } else {
                    showErrorDialog("Invalid response from server.");
                    Log.e("API_RESPONSE", "Invalid Response Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showErrorDialog("Registration failed: " + t.getMessage());
                Log.e("API_ERROR", "Error: ", t);
            }
        });
    }


    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Registration Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }



}
