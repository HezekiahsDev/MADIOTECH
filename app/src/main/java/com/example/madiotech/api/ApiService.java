//ApiService.java
package com.example.madiotech.api;


import com.example.madiotech.data.FetchDataResponse;
import com.example.madiotech.data.Transactions;
import com.example.madiotech.vtu.VtuNetwork;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("user/login.php")
    Call<LoginResponse> loginUser(@Body LoginRequest request);
    @POST("api/user/register.php")
    Call<String> registerUser(@Body RegisterRequest request);
    @GET("user/fetch_vtu.php")
    Call<List<VtuNetwork>> fetchVtuNetworks(@Header("Authorization") String bearerToken);
    @GET("user/fetch_data.php")
    Call<FetchDataResponse> fetchDataBundles(@Header("Authorization") String bearerToken);

    @GET("user/transactions.php")
    // The endpoint returns a JSON array of transactions; map it to List<Transactions>
    Call<List<Transactions>> getTransactions(@Header("Authorization") String apiKey);

}
