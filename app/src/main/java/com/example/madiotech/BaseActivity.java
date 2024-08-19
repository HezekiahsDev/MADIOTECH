package com.example.madiotech;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private static final long NETWORK_CHECK_INTERVAL = 5 * 1000; // 10 seconds

    private Handler handler;
    private Runnable networkCheckRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
        startNetworkCheck();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopNetworkCheck();
    }

    private void startNetworkCheck() {
        if (networkCheckRunnable == null) {
            networkCheckRunnable = new Runnable() {
                @Override
                public void run() {
                    if (!isNetworkAvailable()) {
                        showNoInternetDialog();
                    }
                    handler.postDelayed(this, NETWORK_CHECK_INTERVAL);
                }
            };
        }
        handler.post(networkCheckRunnable);
    }

    private void stopNetworkCheck() {
        if (networkCheckRunnable != null) {
            handler.removeCallbacks(networkCheckRunnable);
            networkCheckRunnable = null;
        }
    }

    // Method to check for internet connectivity
    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    // Method to show the popup dialog for no internet connectivity
    protected void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please check your internet connection and try again.");
        builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Refresh the activity or retry internet connectivity check
                dialog.dismiss(); // Dismiss the dialog
                recreate(); // This restarts the activity
            }
        });
        builder.setCancelable(false); // Prevent dismissing dialog by tapping outside
        builder.show();
    }
}