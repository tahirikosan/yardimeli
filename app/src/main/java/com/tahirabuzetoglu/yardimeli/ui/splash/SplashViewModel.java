package com.tahirabuzetoglu.yardimeli.ui.splash;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tahirabuzetoglu.yardimeli.data.entity.User;

public class SplashViewModel extends AndroidViewModel {
    private SplashRepository repository;
    public LiveData<User> userAuthLive;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        repository = new SplashRepository();
    }

    // check if user authenticated
    public void checkIfUserAuth(){
        userAuthLive = repository.checkUserAuth();
    }
}
