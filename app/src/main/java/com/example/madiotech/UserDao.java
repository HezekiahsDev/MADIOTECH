//UserDao.java
package com.example.madiotech;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.madiotech.api.LoginResponse;

@Dao
public interface UserDao {
    @Insert
    void insertUser(LoginResponse user);

    @Query("SELECT * FROM user LIMIT 1") // First available User
    LiveData<LoginResponse> getUser();

    @Query("DELETE FROM user") // Deletes all user records
    void deleteAllUsers();


}
