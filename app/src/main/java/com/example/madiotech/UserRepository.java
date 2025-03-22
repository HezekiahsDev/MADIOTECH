package com.example.madiotech;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;

import com.example.madiotech.api.ApiService;
import com.example.madiotech.api.LoginRequest;
import com.example.madiotech.api.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private ApiService apiService;
    private UserDao userDao;
    private LiveData<LoginResponse> user;

    public UserRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        user = userDao.getUser();
    }

    public LiveData<LoginResponse> getUser() {
        return user;
    }

    public void loginUser(String username, String password, LoginCallback callback) {
        LoginRequest request = new LoginRequest(username, password);
        apiService.loginUser(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new InsertUserTask(userDao).execute(response.body());
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Invalid Credentials");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    private static class InsertUserTask extends AsyncTask<LoginResponse, Void, Void> {
        private UserDao userDao;
        InsertUserTask(UserDao userDao) { this.userDao = userDao; }

        @Override
        protected Void doInBackground(LoginResponse... users) {
            userDao.insertUser(users[0]);
            return null;
        }
    }

    public interface LoginCallback {
        void onSuccess(LoginResponse user);
        void onFailure(String error);
    }
}
