package com.example.madiotech;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.madiotech.api.LoginResponse;

public class LoginViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LoginViewModel(Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<LoginResponse> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }

    public void login(String username, String password) {
        // Set loading state to true when login starts
        isLoading.setValue(true);

        userRepository.loginUser(username, password, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(LoginResponse user) {
                // Save the login timestamp
                user.setLoginTimestamp(System.currentTimeMillis());

                // Store the updated user data in the database (Room)
                new Thread(() -> AppDatabase.getInstance(getApplication()).userDao().insertUser(user)).start();

                loginResult.postValue(user);
                // Note: We don't set isLoading to false here because
                // that will be handled in the Activity after observing the result
            }

            @Override
            public void onFailure(String error) {
                LoginResponse errorResponse = new LoginResponse();
                errorResponse.setStatus("failed");
                errorResponse.setMessage(error);
                loginResult.postValue(errorResponse);
                // Note: We don't set isLoading to false here because
                // that will be handled in the Activity after observing the result
            }
        });
    }
}