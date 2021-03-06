package com.tahirabuzetoglu.yardimeli.ui.splash;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.tahirabuzetoglu.yardimeli.data.entity.User;

public class SplashRepository {

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    public SplashRepository() {
        this.mAuth= FirebaseAuth.getInstance();
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }

    // check if user signed in or not
    public MutableLiveData<User> checkUserAuth(){
        MutableLiveData<User> isUserAuth = new MutableLiveData<>();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        User user = new User();

        if(firebaseUser != null){
            user.setAuthenticated(true);
            isUserAuth.setValue(user);
        }else{
            user.setAuthenticated(false);
            isUserAuth.setValue(user);
        }
        return isUserAuth;
    }
}
