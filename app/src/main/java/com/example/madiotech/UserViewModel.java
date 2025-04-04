//UserViewModel.java

package com.example.madiotech;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.madiotech.api.LoginResponse;

public class UserViewModel extends AndroidViewModel {
    private UserDao userDao;
    private LiveData<LoginResponse> userLiveData;
    private String apiKey;

    public UserViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
        userLiveData = userDao.getUser(); // Assuming a method to get user exists in UserDao
    }

    public LiveData<LoginResponse> getUser() {
        return userLiveData;
    }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }
    public void logout() {
        new Thread(() -> userDao.deleteAllUsers()).start();
    }
}
