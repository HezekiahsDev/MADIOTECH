// java
package com.example.madiotech;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class FundwalletActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_USERNAME = "KEY_USERNAME";
    private static final String KEY_PALMPAY = "KEY_PALMPAY";
    private static final String KEY_9PSB = "KEY_9PSB";
    // Optional keys (use if you stored bank display names)
    private static final String KEY_BANKNAME_PALMPAY = "KEY_BANKNAME_PALMPAY";
    private static final String KEY_BANKNAME_9PSB = "KEY_BANKNAME_9PSB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fundwallet);

        // Views (ensure these IDs exist in your layout)
        TextView tvBankName1 = findViewById(R.id.tvBankName1);         // bank label for first card
        TextView tvBankName2 = findViewById(R.id.tvBankName2);         // bank label for second card
        TextView tvAccountName1 = findViewById(R.id.tvAccountName1);   // account owner for first card
        TextView tvAccountName2 = findViewById(R.id.tvAccountName2);   // account owner for second card
        TextView tvAccountNumber1 = findViewById(R.id.tvAccountNumber1);
        TextView tvAccountNumber2 = findViewById(R.id.tvAccountNumber2);
        ImageButton btnCopy1 = findViewById(R.id.btnCopy1);
        ImageButton btnCopy2 = findViewById(R.id.btnCopy2);
        MaterialCardView cardBank1 = findViewById(R.id.cardBank1);
        MaterialCardView cardBank2 = findViewById(R.id.cardBank2);

        // Read stored values
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final String username = prefs.getString(KEY_USERNAME, "");
        final String palmpay = prefs.getString(KEY_PALMPAY, "");
        final String ninePsb = prefs.getString(KEY_9PSB, "");
        final String bankNamePalm = prefs.getString(KEY_BANKNAME_PALMPAY, "Palmpay"); // fallback
        final String bankName9Psb = prefs.getString(KEY_BANKNAME_9PSB, "9PSB");     // fallback

        final String accountOwner = (username == null || username.isEmpty()) ? "User/Madiotech" : username + "/Madiotech";

        // Update UI
        if (palmpay == null || palmpay.isEmpty()) {
            cardBank1.setVisibility(View.GONE);
        } else {
            tvBankName1.setText(bankNamePalm);
            tvAccountName1.setText(accountOwner);
            tvAccountNumber1.setText(palmpay);
            cardBank1.setVisibility(View.VISIBLE);
        }

        if (ninePsb == null || ninePsb.isEmpty()) {
            cardBank2.setVisibility(View.GONE);
        } else {
            tvBankName2.setText(bankName9Psb);
            tvAccountName2.setText(accountOwner);
            tvAccountNumber2.setText(ninePsb);
            cardBank2.setVisibility(View.VISIBLE);
        }

        // Copy listeners
        btnCopy1.setOnClickListener(v -> {
            if (palmpay != null && !palmpay.isEmpty()) {
                copyToClipboard(bankNamePalm, palmpay);
                Toast.makeText(this, bankNamePalm + " copied", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No " + bankNamePalm + " account saved", Toast.LENGTH_SHORT).show();
            }
        });

        btnCopy2.setOnClickListener(v -> {
            if (ninePsb != null && !ninePsb.isEmpty()) {
                copyToClipboard(bankName9Psb, ninePsb);
                Toast.makeText(this, bankName9Psb + " copied", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No " + bankName9Psb + " account saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
        }
    }
}
