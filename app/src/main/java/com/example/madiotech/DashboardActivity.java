package com.example.madiotech;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends BaseActivity {

  // Double back to exit variables
  private boolean doubleBackToExitPressedOnce = false;
  private static final int BACK_PRESS_INTERVAL = 2000; // 2 seconds

  private BottomNavigationView bottomNavigationView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dashboard);

    // Initialize bottom navigation
    bottomNavigationView = findViewById(R.id.bottomNavigationView);

    // Set default fragment (Home)
    if (savedInstanceState == null) {
      loadFragment(new HomeFragment());
    }

    // Setup bottom navigation listener
    bottomNavigationView.setOnItemSelectedListener(item -> {
      Fragment selectedFragment = null;

      int itemId = item.getItemId();
      if (itemId == R.id.bnav_home) {
        selectedFragment = new HomeFragment();
      } else if (itemId == R.id.bnav_profile) {
        selectedFragment = new ProfileFragment();
      } else if (itemId == R.id.bnav_notificatiion) {
        selectedFragment = new NotificationFragment();
      }

      if (selectedFragment != null) {
        loadFragment(selectedFragment);
        return true;
      }
      return false;
    });
  }

  private void loadFragment(Fragment fragment) {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.fragmentContainer, fragment);
    transaction.commit();
  }

  @Override
  public void onBackPressed() {
    if (doubleBackToExitPressedOnce) {
      super.onBackPressed();
      return;
    }

    this.doubleBackToExitPressedOnce = true;
    Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

    // Reset the flag after BACK_PRESS_INTERVAL milliseconds
    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, BACK_PRESS_INTERVAL);
  }
}