package com.tahirabuzetoglu.yardimeli.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;
import com.tahirabuzetoglu.yardimeli.R;
import com.tahirabuzetoglu.yardimeli.data.entity.Post;
import com.tahirabuzetoglu.yardimeli.data.entity.User;
import com.tahirabuzetoglu.yardimeli.ui.edit_profile.EditProfileActivity;
import com.tahirabuzetoglu.yardimeli.ui.liked.LikedPostsActivity;
import com.tahirabuzetoglu.yardimeli.ui.main.FeedActivity;
import com.tahirabuzetoglu.yardimeli.ui.main.PostViewModel;
import com.tahirabuzetoglu.yardimeli.ui.postdetail.PostDetailActivity;
import com.tahirabuzetoglu.yardimeli.ui.splash.SplashActivity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements UserPostAdapter.OnItemClickListener{


    private User mUser;
    private ImageView ivUser;
    private ImageView ivOptions;
    private ImageView ivCloseOptions;
    private ImageView ivEditProfile;
    private TextView tvLiked;
    private TextView tvBiyo;
    private TextView tvName;
    private TextView tvLogOut;
    private TextView tvWarningNoPublication;
    private ProgressDialog progressDialog;
    private BottomSheetBehavior optionsSheetBehaviour;
    private BottomSheetBehavior descriptionSheetBehaviour;
    private RelativeLayout rlOptionsSheet;
    private RelativeLayout rlLessonDescriptionSheet;
    // user posts
    private RecyclerView userPostRecyclerV;
    private UserPostAdapter userPostAdapter;
    LinearLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
    // LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
    private List<Post> mPostList;


    private ProfileViewModel profileViewModel;
    private PostViewModel postViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        setPostViewModel();
        setProfileViewModel();

        ivUser = findViewById(R.id.iv_user_photo);
        ivOptions = findViewById(R.id.iv_options);
        ivCloseOptions = findViewById(R.id.iv_close_options);
        ivEditProfile = findViewById(R.id.iv_edit_profile);
        tvBiyo = findViewById(R.id.tv_bio);
        tvName = findViewById(R.id.tv_name);
        tvLogOut = findViewById(R.id.tv_logout);
        tvLiked = findViewById(R.id.tv_liked_posts);
        tvWarningNoPublication = findViewById(R.id.tv_warning_no_publication);
        progressDialog = new ProgressDialog(this);

        userPostRecyclerV = findViewById(R.id.recycler_view_user_posts);
        userPostRecyclerV.setLayoutManager(gridLayoutManager);
        userPostRecyclerV.setHasFixedSize(true);

        rlOptionsSheet = findViewById(R.id.rl_profile_options_sheet);
        optionsSheetBehaviour = BottomSheetBehavior.from(rlOptionsSheet);
        optionsSheetBehaviour.setDraggable(false);

        getUser();

        ivEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        ivOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RotateOut).duration(1000).playOn(ivOptions);
                showOptionsPane();
            }
        });

        ivCloseOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.RotateIn).duration(1000).playOn(ivOptions);
                closeOptionsPane();
            }
        });

        tvLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        tvLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, LikedPostsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

       /* tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChangePassword();
            }
        });

        tvChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChangeEmail();
            }
        });*/

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(ProfileActivity.this, FeedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Get user info from shared prefs
    private void getUser(){
        showProgressDialog();

        profileViewModel.getUser();
        profileViewModel.userLive.observe(ProfileActivity.this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mUser = user;
                setUserUI(user);

                // after set user show posts
                getUserPosts();
                progressDialog.dismiss();
            }
        });
    }



    // get User Posts from firestore
    private void getUserPosts(){

        profileViewModel.getUserPosts(mUser.getId());
        profileViewModel.userPostsLive.observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> postList) {
                mPostList = postList;

                handlePublicationWarning(mPostList);

                setUserPostAdapter(mPostList);
            }
        });
    }

    // Initilize log out process
    private void logOut(){
        profileViewModel.logOut();
        profileViewModel.logOutUserLive.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user.isLogOut()){
                   Intent intent = new Intent(ProfileActivity.this, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finishAffinity();
                }else{
                    Toast.makeText(ProfileActivity.this, "Could not log out, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // set UI for user info
    private void setUserUI(User user){

        if(user.getImageUrl().equals("default")){
            ivUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_image_24));
        }else{
            Picasso.get().load(user.getImageUrl()).into(ivUser);
        }


        tvBiyo.setText(user.getDescription());
        tvName.setText(user.getName());
    }

    private void setUserPostAdapter(List<Post> postList){
        userPostAdapter = new UserPostAdapter(postList);
        userPostRecyclerV.setAdapter(userPostAdapter);
        userPostAdapter.setOnItemClickListener(this);
    }


    private void setPostViewModel(){
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
    }

    private void setProfileViewModel(){
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }



    // Handle short clickcs
    @Override
    public void onImageClick(int position) {
        Intent intent  = new Intent(ProfileActivity.this, PostDetailActivity.class);
        intent.putExtra("USER_POST", mPostList.get(position));
        startActivity(intent);
    }


    private void showProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
    }

    // Set active/deactive warning text view(tv_warning_no_publication)
    private void handlePublicationWarning(List<Post> userPost){
        if(userPost.size() < 1){
            tvWarningNoPublication.setVisibility(View.VISIBLE);
            userPostRecyclerV.setVisibility(View.GONE);
        }else{
            tvWarningNoPublication.setVisibility(View.GONE);
            userPostRecyclerV.setVisibility(View.VISIBLE);
        }
    }

    private void showOptionsPane(){
        optionsSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
        optionsSheetBehaviour.setHideable(false);
    }
    private void closeOptionsPane(){
        optionsSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

}