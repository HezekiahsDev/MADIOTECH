package com.example.madiotech;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.madiotech.utils.UserLevelMapper;

import org.json.JSONObject;

import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Added fields
    private TextView profileName;
    private TextView profileEmail;
    private TextView tvBalance;
    private TextView tvAccountLevel;
    private Button buttonLogout;
    private UserViewModel userViewModel;
    private final Handler handler = new Handler();
    private final Runnable walletBalanceRunnable = new Runnable() {
        @Override
        public void run() {
            fetchWalletBalance();
            handler.postDelayed(this, 60000); // Refresh every 1 minute
        }
    };

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        profileName = view.findViewById(R.id.profileName);
        profileEmail = view.findViewById(R.id.profileEmail);
        tvBalance = view.findViewById(R.id.tvBalance);
        tvAccountLevel = view.findViewById(R.id.tvAccountLevel);
        buttonLogout = view.findViewById(R.id.buttonLogout);

        // Initialize ViewModel (shared with activity so HomeFragment updates too)
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Observe user data
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                profileName.setText(user.getUsername() != null ? user.getUsername() : "Guest");
                profileEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                // store api key in ViewModel for other fragments/activities
                userViewModel.setApiKey(user.getApiKey());
                // Fetch wallet balance immediately after user is available
                fetchWalletBalance();
                // Start periodic wallet balance updates
                handler.post(walletBalanceRunnable);
                // Map and display account level using shared mapper
                String mappedLevel = UserLevelMapper.mapUserLevel(user.getUserLevel());
                tvAccountLevel.setText(mappedLevel);
            }
        });

        // Logout flow: reuse confirm_logout layout like HomeFragment
        buttonLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.confirm_logout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button btnGoBack = dialogView.findViewById(R.id.buttonConfirmLogoutGoBack);
        Button btnLogout = dialogView.findViewById(R.id.buttonConfirmLogout);

        btnGoBack.setOnClickListener(v -> dialog.dismiss());

        btnLogout.setOnClickListener(v -> {
            dialog.dismiss();
            handleLogout();
        });
    }

    private void handleLogout() {
        // Use ViewModel's logout helper and navigate to MainActivity
        userViewModel.logout();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void fetchWalletBalance() {
        String apiKey = userViewModel.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            return;
        }

        String url = "https://madiotech.com.ng/api/balance/?apikey=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    int code = jsonObject.optInt("code", -1);
                    if (code == 200) {
                        String walletBalance = jsonObject.optString("wallet_balance", "₦0.00");
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> tvBalance.setText("₦" + walletBalance));
                        }
                    } else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> tvBalance.setText("--"));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> tvBalance.setText("--"));
                }
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(walletBalanceRunnable);
    }
}