package com.example.madiotech;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.madiotech.api.LoginResponse;

public class LoginViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();

    public LoginViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<LoginResponse> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        userRepository.loginUser(username, password, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(LoginResponse user) {
                loginResult.postValue(user);
            }

            @Override
            public void onFailure(String error) {
                loginResult.postValue(null);
            }
        });
    }
}
