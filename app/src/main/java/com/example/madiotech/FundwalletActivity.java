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

        // Read stored values from centralized prefs and trim
        SharedPreferences prefs = getSharedPreferences(PrefsKeys.PREFS_NAME, MODE_PRIVATE);
        final String username = StringUtils.safeTrim(prefs.getString(PrefsKeys.KEY_USERNAME, ""));
        final String palmpay = StringUtils.safeTrim(prefs.getString(PrefsKeys.KEY_PALMPAY, ""));
        final String ninePsb = StringUtils.safeTrim(prefs.getString(PrefsKeys.KEY_9PSB, ""));
        final String bankNamePalm = StringUtils.safeTrim(prefs.getString(PrefsKeys.KEY_BANKNAME_PALMPAY, "Palmpay")); // fallback
        final String bankName9Psb = StringUtils.safeTrim(prefs.getString(PrefsKeys.KEY_BANKNAME_9PSB, "9PSB"));     // fallback

        final String accountOwner = (username == null || username.isEmpty()) ? "User/Madiotech" : "Payvessl/" +    username;

        // Update UI helper
        Runnable updatePalm = () -> {
            tvBankName1.setText(bankNamePalm);
            tvAccountName1.setText(accountOwner);
            tvAccountNumber1.setText(palmpay);
            cardBank1.setVisibility(palmpay.isEmpty() ? View.GONE : View.VISIBLE);
        };

        Runnable update9psb = () -> {
            tvBankName2.setText(bankName9Psb);
            tvAccountName2.setText(accountOwner);
            tvAccountNumber2.setText(ninePsb);
            cardBank2.setVisibility(ninePsb.isEmpty() ? View.GONE : View.VISIBLE);
        };

        // Initial UI update from prefs
        if (palmpay.isEmpty()) {
            cardBank1.setVisibility(View.GONE);
        } else {
            updatePalm.run();
        }

        if (ninePsb.isEmpty()) {
            cardBank2.setVisibility(View.GONE);
        } else {
            update9psb.run();
        }

        // If prefs were empty for either account, observe Room as fallback
        if (palmpay.isEmpty() || ninePsb.isEmpty()) {
            UserRepository repo = new UserRepository(getApplication());
            repo.getUser().observe(this, user -> {
                if (user == null) return;
                String dbPal = StringUtils.safeTrim(user.getPalmpay());
                String dbNine = StringUtils.safeTrim(user.getNinePsb());

                if (palmpay.isEmpty() && !dbPal.isEmpty()) {
                    tvBankName1.setText(bankNamePalm);
                    tvAccountName1.setText(accountOwner);
                    tvAccountNumber1.setText(dbPal);
                    cardBank1.setVisibility(View.VISIBLE);
                }

                if (ninePsb.isEmpty() && !dbNine.isEmpty()) {
                    tvBankName2.setText(bankName9Psb);
                    tvAccountName2.setText(accountOwner);
                    tvAccountNumber2.setText(dbNine);
                    cardBank2.setVisibility(View.VISIBLE);
                }

                // Additionally, sync these values back to SharedPreferences so future reads succeed quickly
                SharedPreferences.Editor editor = prefs.edit();
                boolean changed = false;
                if (palmpay.isEmpty() && !dbPal.isEmpty()) { editor.putString(PrefsKeys.KEY_PALMPAY, dbPal); changed = true; }
                if (ninePsb.isEmpty() && !dbNine.isEmpty()) { editor.putString(PrefsKeys.KEY_9PSB, dbNine); changed = true; }
                if (changed) editor.apply();
            });
        }

        // Copy listeners
        btnCopy1.setOnClickListener(v -> {
            String toCopy = StringUtils.safeTrim(tvAccountNumber1.getText().toString());
            if (!toCopy.isEmpty()) {
                copyToClipboard(bankNamePalm, toCopy);
                Toast.makeText(this, bankNamePalm + " copied", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No " + bankNamePalm + " account saved", Toast.LENGTH_SHORT).show();
            }
        });

        btnCopy2.setOnClickListener(v -> {
            String toCopy = StringUtils.safeTrim(tvAccountNumber2.getText().toString());
            if (!toCopy.isEmpty()) {
                copyToClipboard(bankName9Psb, toCopy);
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
