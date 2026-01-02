//ApiService.java
package com.example.madiotech.api;


import com.example.madiotech.data.FetchDataResponse;
import com.example.madiotech.data.Transactions;
import com.example.madiotech.vtu.VtuNetwork;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

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

    // Buy airtime endpoint. This endpoint expects the api key and fields as query parameters.
    // Example: POST https://madiotech.com.ng/api/airtime/?apikey=...&amount=100&phone=0810...&network_code=MTN
    @GET("airtime/")
    Call<String> buyAirtime(@Query("apikey") String apiKey,
                            @Query("amount") String amount,
                            @Query("phone") String phone,
                            @Query("network_code") String networkCode);

    // Buy data endpoint. Example:
    // GET https://madiotech.com.ng/api/data/?apikey=...&product_code=SME01&phone=08022293496&network_code=MTN
    @GET("data/")
    okhttp3.ResponseBody buyDataRaw(@retrofit2.http.Query("apikey") String apiKey,
                                    @retrofit2.http.Query("product_code") String productCode,
                                    @retrofit2.http.Query("phone") String phone,
                                    @retrofit2.http.Query("network_code") String networkCode);

    @GET("data/")
    Call<ResponseBody> buyData(@Query("apikey") String apiKey,
                                       @Query("product_code") String productCode,
                                       @Query("phone") String phone,
                                       @Query("network_code") String networkCode);

}
