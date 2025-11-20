package com.example.madiotech; // Adjust the package name if needed

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madiotech.data.Transactions;

import java.text.SimpleDateFormat;
import java.util.ArrayList; // Import ArrayList
import java.util.List;
import java.util.Locale;

public class RecentTransactionAdapter extends RecyclerView.Adapter<RecentTransactionAdapter.ViewHolder> {

    // FIX 1: Initialize the list to an empty list here. This prevents it from ever being null.
    private List<Transactions> transactionList = new ArrayList<>();
    private final Context context;

    public RecentTransactionAdapter(Context context, List<Transactions> transactionList) {
        this.context = context;
        // Also check here to avoid passing a null list
        if (transactionList != null) {
            this.transactionList = transactionList;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction_small, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transactions transaction = transactionList.get(position);
        holder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        // FIX 2: Add a safety check. If the list is somehow null, return 0.
        return transactionList != null ? transactionList.size() : 0;
    }

    // Method to update the data in the adapter
    public void updateData(List<Transactions> newTransactions) {
        if (newTransactions != null) {
            this.transactionList = newTransactions;
        } else {
            this.transactionList.clear(); // Clear the list instead of setting it to null
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTxIcon;
        TextView tvTxTitle, tvTxSubtitle, tvTxAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTxIcon = itemView.findViewById(R.id.ivTxIcon);
            tvTxTitle = itemView.findViewById(R.id.tvTxTitle);
            tvTxSubtitle = itemView.findViewById(R.id.tvTxSubtitle);
            tvTxAmount = itemView.findViewById(R.id.tvTxAmount);
        }

        void bind(Transactions transaction) {
            if (transaction == null) return; // Safety check

            tvTxTitle.setText(transaction.getDescription());

            String formattedDate = formatDate(transaction.getDate());
            String subtitle = formattedDate + " · Ref# " + transaction.getTransactionId();
            tvTxSubtitle.setText(subtitle);

            String description = transaction.getDescription() != null ? transaction.getDescription().toLowerCase() : "";
            if (description.contains("wallet funding") || description.contains("credit")) {
                tvTxAmount.setText(String.format("+₦%s", transaction.getAmount()));
                tvTxAmount.setTextColor(ContextCompat.getColor(context, R.color.green_success));
            } else {
                tvTxAmount.setText(String.format("-₦%s", transaction.getAmount()));
                tvTxAmount.setTextColor(ContextCompat.getColor(context, R.color.red_error));
            }

            String service = transaction.getService() != null ? transaction.getService().toLowerCase() : "";
            if (service.contains("airtime")) {
                ivTxIcon.setImageResource(R.drawable.btn_airtime_topup);
            } else if (service.contains("data")) {
                ivTxIcon.setImageResource(R.drawable.data1);
            } else if (service.contains("electricity")) {
                ivTxIcon.setImageResource(R.drawable.btn_electricity_bills);
            } else if (service.contains("wallet")) {
                ivTxIcon.setImageResource(R.drawable.btn_wallet);
            } else {
                ivTxIcon.setImageResource(R.drawable.btn_transaction_history);
            }
        }

        private String formatDate(String dateString) {
            if (dateString == null) return "N/A";
            try {
                SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM", Locale.getDefault());
                return formatter.format(parser.parse(dateString));
            } catch (Exception e) {
                return dateString.split(" ")[0];
            }
        }
    }
}
