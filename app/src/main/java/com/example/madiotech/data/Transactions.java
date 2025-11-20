// File: app/src/main/java/com/example/madiotech/data/Transactions.java
package com.example.madiotech.data;

import com.google.gson.annotations.SerializedName;

public class Transactions {

    @SerializedName("transaction_id")
    private String transactionId;

    @SerializedName("balance_before")
    private String balanceBefore;

    @SerializedName("balance_after")
    private String balanceAfter;

    @SerializedName("service")
    private String service;

    @SerializedName("amount")
    private String amount;

    @SerializedName("network")
    private String network;

    @SerializedName("date")
    private String date;

    @SerializedName("status")
    private String status;

    @SerializedName("true_response")
    private String trueResponse;

    @SerializedName("description")
    private String description;

    // --- Getters ---

    public String getTransactionId() {
        return transactionId;
    }

    public String getBalanceBefore() {
        return balanceBefore;
    }

    public String getBalanceAfter() {
        return balanceAfter;
    }

    public String getService() {
        return service;
    }

    public String getAmount() {
        return amount;
    }

    public String getNetwork() {
        return network;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getTrueResponse() {
        return trueResponse;
    }

    public String getDescription() {
        return description;
    }

    // --- Overridden toString() for Readable Logging ---

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", amount='" + amount + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}