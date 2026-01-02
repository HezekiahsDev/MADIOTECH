package com.example.madiotech;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.madiotech.api.LoginResponse;

/**
 * NotificationFragment displays notices embedded in the stored LoginResponse.
 */
public class NotificationFragment extends Fragment {

    private TextView tvNotificationsEmpty;
    private TextView tvNotice1, tvNotice2, tvNotice3, tvNotice4, tvNotice5;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNotificationsEmpty = view.findViewById(R.id.tvNotificationsEmpty);
        tvNotice1 = view.findViewById(R.id.tvNotice1);
        tvNotice2 = view.findViewById(R.id.tvNotice2);
        tvNotice3 = view.findViewById(R.id.tvNotice3);
        tvNotice4 = view.findViewById(R.id.tvNotice4);
        tvNotice5 = view.findViewById(R.id.tvNotice5);

        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                populateNotices(user);
            } else {
                showEmptyMessage();
            }
        });
    }

    private void populateNotices(LoginResponse user) {
        boolean any = false;

        if (user.getNotice1() != null && !user.getNotice1().trim().isEmpty()) {
            tvNotice1.setText(user.getNotice1());
            tvNotice1.setVisibility(View.VISIBLE);
            any = true;
        } else {
            tvNotice1.setVisibility(View.GONE);
        }

        if (user.getNotice2() != null && !user.getNotice2().trim().isEmpty()) {
            tvNotice2.setText(user.getNotice2());
            tvNotice2.setVisibility(View.VISIBLE);
            any = true;
        } else {
            tvNotice2.setVisibility(View.GONE);
        }

        if (user.getNotice3() != null && !user.getNotice3().trim().isEmpty()) {
            tvNotice3.setText(user.getNotice3());
            tvNotice3.setVisibility(View.VISIBLE);
            any = true;
        } else {
            tvNotice3.setVisibility(View.GONE);
        }

        if (user.getNotice4() != null && !user.getNotice4().trim().isEmpty()) {
            tvNotice4.setText(user.getNotice4());
            tvNotice4.setVisibility(View.VISIBLE);
            any = true;
        } else {
            tvNotice4.setVisibility(View.GONE);
        }

        if (user.getNotice5() != null && !user.getNotice5().trim().isEmpty()) {
            tvNotice5.setText(user.getNotice5());
            tvNotice5.setVisibility(View.VISIBLE);
            any = true;
        } else {
            tvNotice5.setVisibility(View.GONE);
        }

        if (any) {
            tvNotificationsEmpty.setVisibility(View.GONE);
        } else {
            showEmptyMessage();
        }
    }

    private void showEmptyMessage() {
        tvNotificationsEmpty.setVisibility(View.VISIBLE);
        tvNotice1.setVisibility(View.GONE);
        tvNotice2.setVisibility(View.GONE);
        tvNotice3.setVisibility(View.GONE);
        tvNotice4.setVisibility(View.GONE);
        tvNotice5.setVisibility(View.GONE);
    }
}