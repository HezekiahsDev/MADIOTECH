package com.example.madiotech.api;


public class RegisterRequest {
    private String fulname;
    private String username;
    private String refer;
    private String email;
    private String phone;
    private String pass1;
    private String pass2;

    public RegisterRequest(String fulname, String username, String refer, String email, String phone, String pass1, String pass2) {
        this.fulname = fulname;
        this.username = username;
        this.refer = refer;
        this.email = email;
        this.phone = phone;
        this.pass1 = pass1;
        this.pass2 = pass2;
    }
}