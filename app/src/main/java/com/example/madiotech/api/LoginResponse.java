package com.example.madiotech.api;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "user")
public class LoginResponse {
    @PrimaryKey
    @NonNull
    private String uid;

    private String status;
    private String message;
    private String email;
    private String apiKey;
    private String wallet;
    private String username;
    private String phone;

    @SerializedName("referral_credit") // Ensures Gson maps correctly
    private String referralCredit;

    @SerializedName("user_level")
    private String userLevel;

    // Default constructor (Required for Room and Gson)
    public LoginResponse() {
    }

    // Getters and Setters
    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReferralCredit() { // Correct getter method
        return referralCredit;
    }

    public void setReferralCredit(String referralCredit) {
        this.referralCredit = referralCredit;
    }

    public String getUserLevel() { // Correct getter method
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }
}
