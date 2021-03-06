package com.tahirabuzetoglu.yardimeli.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tahirabuzetoglu.yardimeli.data.entity.User;

public class LoginViewModel  extends AndroidViewModel {
    private LoginRepository repository;
    public LiveData<User> createdUserLive;


    public LoginViewModel(@NonNull Application application) {
        super(application);

        repository = new LoginRepository(application);
    }


    public void createUserInDB(String phoneNumber){
        createdUserLive = repository.createUserInDb(phoneNumber);
    }



}
