package com.example.madiotech.api;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("user/login.php")
    Call<LoginResponse> loginUser(@Body LoginRequest request);
    @POST("api/user/register.php")
    Call<String> registerUser(@Body RegisterRequest request);

}
