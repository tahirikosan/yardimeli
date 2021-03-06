package com.tahirabuzetoglu.yardimeli.ui.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.tahirabuzetoglu.yardimeli.R;
import com.tahirabuzetoglu.yardimeli.ui.login.LoginActivity;
import com.tahirabuzetoglu.yardimeli.ui.main.FeedActivity;
import com.tahirabuzetoglu.yardimeli.utils.SharedPrefData;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    //public static final String USER = "USER";
    private SplashViewModel splashViewModel;
    private ImageView ivSplashIcon;
    private ImageView ivPoint;
    private TextView tvGoToMainMenu;
    private SharedPrefData sharedPrefData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ivSplashIcon = findViewById(R.id.iv_splash);
        ivPoint = findViewById(R.id.iv_point);
        tvGoToMainMenu = findViewById(R.id.tv_go_to_main_menu);
        sharedPrefData = new SharedPrefData(this);

        YoYo.with(Techniques.FadeOut).duration(1000).playOn(ivSplashIcon);
        YoYo.with(Techniques.BounceInRight).duration(1000).repeat(YoYo.INFINITE).playOn(ivPoint);

        initSplashViewModel();
        checkIfUserAuth();

        tvGoToMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfUserAuth();
            }
        });

    }

    //uygulama intro
    private void initViewPager() {
        ViewPager2 viewPager2 = findViewById(R.id.view_pager);
        List<SlideModel> list = new ArrayList<>();
        //drawable klasörünüze 3 adet resim yüklendiğini varsayıyoruz
        list.add(new SlideModel("⚡ Yardıma ihtiyacı olanlara hızlıca erişin ⚡", R.drawable.help_poor));
        list.add(new SlideModel("❤ İhtiyacınız olmayan eşyaları bağışlayın ❤ ", R.drawable.volunteer));
        list.add(new SlideModel("✨ Toplu yardımlara katılın ✨", R.drawable.truck_donation));
        list.add(new SlideModel("\uD83D\uDCB0 İhtiyaç sahiplerine destek olun \uD83D\uDCB0", R.drawable.charity));
        list.add(new SlideModel("\uD83D\uDC96 Kalplerde yer edinin \uD83D\uDC96", R.drawable.hearts));
        viewPager2.setAdapter(new SlidePagerAdapter(list));

        //Değişim sağlandığında çalışan listener
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (position == 4) {
                    tvGoToMainMenu.setVisibility(View.VISIBLE);
                    ivPoint.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    private void initSplashViewModel() {
        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
    }

    private void checkIfUserAuth() {
        splashViewModel.checkIfUserAuth();
        splashViewModel.userAuthLive.observe(this, user -> {
            if (!sharedPrefData.isSawIntro()) {
                sharedPrefData.userSawIntro();
                initViewPager();
            } else {
                if (!user.isAuthenticated()) {
                    // user not auth, then go to login to do auth
                    goToLoginActivity();
                } else {
                    goToMainActivity();
                }
            }
        });
    }


    private void goToLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, FeedActivity.class);
        startActivity(intent);
        finish();
    }
}