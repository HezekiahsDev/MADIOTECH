package com.example.madiotech;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/user/login.php")
    Call<LoginResponse> loginUser(@Body LoginRequest request);
}
