package com.tahirabuzetoglu.yardimeli.ui.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.tahirabuzetoglu.yardimeli.R;
import com.tahirabuzetoglu.yardimeli.ui.login.LoginActivity;
import com.tahirabuzetoglu.yardimeli.ui.main.FeedActivity;

public class SplashActivity extends AppCompatActivity {

    //public static final String USER = "USER";
    private SplashViewModel splashViewModel;
    private ImageView ivSplashBee;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivSplashBee = findViewById(R.id.iv_splash);

        initSplashViewModel();
        checkIfUserAuth();

    }

    private void initSplashViewModel(){
        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
    }

    private void checkIfUserAuth(){
        splashViewModel.checkIfUserAuth();
        splashViewModel.userAuthLive.observe(this, user -> {
            if(!user.isAuthenticated()){
                // user not auth, then go to login to do auth
                goToLoginActivity();
            }else{
                goToMainActivity();
            }
        });
    }



    private void goToLoginActivity(){
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToMainActivity(){
        Intent intent = new Intent(SplashActivity.this, FeedActivity.class);
        startActivity(intent);
        finish();
    }
}