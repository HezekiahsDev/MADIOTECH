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

    private long loginTimestamp;  // Add this field

    @SerializedName("palmpay")
    private String palmpay;

    @SerializedName("9psb")
    private String ninePsb;

    // Notification fields coming from login response
    @SerializedName("notice1")
    private String notice1;

    @SerializedName("notice2")
    private String notice2;

    @SerializedName("notice3")
    private String notice3;

    @SerializedName("notice4")
    private String notice4;

    @SerializedName("notice5")
    private String notice5;


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
    // Getters and Setters
    public long getLoginTimestamp() {
        return loginTimestamp;
    }

    public void setLoginTimestamp(long loginTimestamp) {
        this.loginTimestamp = loginTimestamp;
    }

    public String getPalmpay() {
        return palmpay;
    }

    public void setPalmpay(String palmpay) {
        this.palmpay = palmpay;
    }

    public String getNinePsb() {
        return ninePsb;
    }

    public void setNinePsb(String ninePsb) {
        this.ninePsb = ninePsb;
    }

    public String getNotice1() { return notice1; }
    public void setNotice1(String notice1) { this.notice1 = notice1; }

    public String getNotice2() { return notice2; }
    public void setNotice2(String notice2) { this.notice2 = notice2; }

    public String getNotice3() { return notice3; }
    public void setNotice3(String notice3) { this.notice3 = notice3; }

    public String getNotice4() { return notice4; }
    public void setNotice4(String notice4) { this.notice4 = notice4; }

    public String getNotice5() { return notice5; }
    public void setNotice5(String notice5) { this.notice5 = notice5; }

}
