// File: app/src/main/java/com/example/madiotech/data/TransactionResponse.java
package com.example.madiotech.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TransactionResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("transactions")
    private List<Transactions> transactions;

    // Getters
    public int getCode() {
        return code;
    }

    public List<Transactions> getTransactions() {
        return transactions;
    }
}