package com.example.madiotech;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.madiotech.data.Transactions;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> implements Filterable {

    private final Context context;
    private final List<Transactions> transactionList;
    private final List<Transactions> transactionListFull; // A copy for filtering

    public TransactionAdapter(Context context, List<Transactions> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
        this.transactionListFull = new ArrayList<>(transactionList);
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your specific item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transactions transaction = transactionList.get(position);

        // 1. Set Description - Use Html.fromHtml for tags like <span> or <br>
        if (transaction.getDescription() != null) {
            holder.tvDescription.setText(Html.fromHtml(transaction.getDescription(), Html.FROM_HTML_MODE_LEGACY));
        }

        // 2. Set Date
        holder.tvDate.setText(transaction.getDate());

        // 3. Set Amount with proper formatting
        try {
            double amount = Double.parseDouble(transaction.getAmount());
            holder.tvAmount.setText(String.format(Locale.getDefault(), "₦%,.2f", amount));
        } catch (Exception e) {
            holder.tvAmount.setText("₦--.--"); // Fallback for invalid numbers
        }

        // 4. Set Status and color-code it
        String status = transaction.getStatus().toUpperCase();
        holder.tvStatus.setText(status);

        int statusColor;
        switch (status) {
            case "SUCCESSFUL":
                statusColor = Color.parseColor("#2E7D32"); // Dark Green
                break;
            case "PENDING":
                statusColor = Color.parseColor("#FF8F00"); // Amber/Orange
                break;
            case "REVERSED":
            case "FAILED":
                statusColor = Color.parseColor("#C62828"); // Dark Red
                break;
            default:
                statusColor = Color.GRAY;
                break;
        }
        holder.tvStatus.getBackground().setTint(statusColor);
    }

    @Override
    public int getItemCount() {
        // CHECK THIS LOG
     Log.d("AdapterDebug", "getItemCount() called. Size is: " + (transactionList != null ? transactionList.size() : 0));
        return transactionList != null ? transactionList.size() : 0;
    }


    // Method to update the list from the Activity and refresh the view
    public void updateList(List<Transactions> newList) {

        // CHECK THIS LOG
        Log.d("AdapterDebug", "updateList() called with " + (newList != null ? newList.size() : 0) + " items.");

        if (newList == null) {
            transactionList.clear();
            transactionListFull.clear();
            notifyDataSetChanged();
            return;
        }

        transactionList.clear();
        transactionList.addAll(newList);
        transactionListFull.clear();
        transactionListFull.addAll(newList);
        notifyDataSetChanged(); // This tells the RecyclerView to redraw itself
    }

    // ViewHolder class that holds the views for a single item
    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvDate, tvAmount, tvStatus;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvTransactionDescription);
            tvDate = itemView.findViewById(R.id.tvTransactionDate);
            tvAmount = itemView.findViewById(R.id.tvTransactionAmount);
            tvStatus = itemView.findViewById(R.id.tvTransactionStatus);
        }
    }

    // --- Filter logic for SearchView ---
    @Override
    public Filter getFilter() {
        return transactionFilter;
    }

    private final Filter transactionFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Transactions> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(transactionListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Transactions item : transactionListFull) {
                    // Search in description, amount, status, or transactionId
                    if (item.getDescription().toLowerCase().contains(filterPattern) ||
                            item.getAmount().toLowerCase().contains(filterPattern) ||
                            item.getStatus().toLowerCase().contains(filterPattern) ||
                            item.getTransactionId().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            transactionList.clear();
            Object values = results.values;
            if (values instanceof List<?>) {
                for (Object o : (List<?>) values) {
                    if (o instanceof Transactions) {
                        transactionList.add((Transactions) o);
                    }
                }
            }
            notifyDataSetChanged();
        }
    };
}