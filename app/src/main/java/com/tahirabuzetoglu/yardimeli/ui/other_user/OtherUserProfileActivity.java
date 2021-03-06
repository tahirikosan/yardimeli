package com.tahirabuzetoglu.yardimeli.ui.other_user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;
import com.tahirabuzetoglu.yardimeli.R;
import com.tahirabuzetoglu.yardimeli.data.entity.Post;
import com.tahirabuzetoglu.yardimeli.data.entity.User;
import com.tahirabuzetoglu.yardimeli.ui.main.PostAdapter;
import com.tahirabuzetoglu.yardimeli.ui.postdetail.PostDetailActivity;
import com.tahirabuzetoglu.yardimeli.ui.profile.ProfileActivity;
import com.tahirabuzetoglu.yardimeli.ui.profile.UserPostAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.tahirabuzetoglu.yardimeli.ui.liked.LikedPostsActivity.OTHER_USER_ID;

public class OtherUserProfileActivity extends AppCompatActivity implements UserPostAdapter.OnItemClickListener{


    public static final String TRANSFERED_POST = "TRANSFERED_POST";

    private User otherUser;
    private ImageView ivUser;
    private ImageButton ibLocation;
    private ImageButton ibPhone;
    private TextView tvBiyo;
    private TextView tvName;
    private TextView tvSheetTitle;
    private TextView tvSheetSubTitle;
    private TextView tvWarningNoPublication;
    private Button btnCloseDescPane;
    private BottomSheetBehavior sheetBehavior;
    private RelativeLayout rlBottomSheet;

    private ProgressDialog progressDialog;

    // user posts
    private RecyclerView userPostRecyclerV;
    private UserPostAdapter userPostAdapter;
    LinearLayoutManager gridLayoutManager = new GridLayoutManager(this , 2);
    // LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
    private List<Post> mPostList = new ArrayList<>();

    private OtherProfileViewModel otherProfileViewModel;
    private String userId;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        getUserId();

        setProfileViewModel();

        ivUser = findViewById(R.id.iv_user_photo);
        ibLocation = findViewById(R.id.ib_show_location);
        ibPhone = findViewById(R.id.ib_show_phone);
        tvBiyo = findViewById(R.id.tv_bio);
        tvName = findViewById(R.id.tv_name);
        tvSheetTitle = findViewById(R.id.tv_sheet_title);
        tvSheetSubTitle = findViewById(R.id.tv_sheet_subtitle);
        tvWarningNoPublication = findViewById(R.id.tv_warning_no_publication);
        btnCloseDescPane = findViewById(R.id.btn_close_decs_pane);
        tvName = findViewById(R.id.tv_name);
        rlBottomSheet = findViewById(R.id.rl_botom_sheet);
        sheetBehavior = BottomSheetBehavior.from(rlBottomSheet);
        progressDialog = new ProgressDialog(this);

        userPostRecyclerV = findViewById(R.id.recycler_view_user_posts);
        userPostRecyclerV.setLayoutManager(gridLayoutManager);
        userPostRecyclerV.setHasFixedSize(true);

        getUser();

        btnCloseDescPane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBottomSheet();
            }
        });

        ibLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetLocation();
            }
        });

        ibPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetPhone();
            }
        });

    }


    private void getUserId(){
        Intent intent = getIntent();
        userId = intent.getStringExtra(OTHER_USER_ID);
    }



    // Get user info from shared prefs
    private void getUser(){
        showProgressDialog();

        otherProfileViewModel.getOtherUser(userId);
        otherProfileViewModel.otherUserLive.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user.isSuccess()) {
                    otherUser = user;
                    setUserUI(user);

                    // after set user show posts
                    getUserPosts();

                    progressDialog.dismiss();
                }else{
                    Toast.makeText(OtherUserProfileActivity.this, "Could not get user, please try again later", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }




    // get User Posts from firestore
    private void getUserPosts(){

        otherProfileViewModel.getOtherUserPostList(otherUser.getId());
        otherProfileViewModel.otherUserPostListLive.observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> postList) {
                mPostList = postList;

                handlePublicationWarning(mPostList);

                setUserPostAdapter(mPostList);
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



    private void setProfileViewModel(){
        otherProfileViewModel = new ViewModelProvider(this).get(OtherProfileViewModel.class);
    }


    private void showBottomSheetLocation(){
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        tvSheetTitle.setText("Adres");
        tvSheetSubTitle.setText(otherUser.getCity());
        sheetBehavior.setHideable(false);
    }

    private void showBottomSheetPhone(){
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        tvSheetTitle.setText("Telefon No");
        tvSheetSubTitle.setText(otherUser.getPhoneNumber());
        sheetBehavior.setHideable(false);
    }

    private void closeBottomSheet(){
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }



    // Handle short clickcs
    @Override
    public void onImageClick(int position) {
        Intent intent  = new Intent(OtherUserProfileActivity.this, PostDetailActivity.class);
        intent.putExtra(TRANSFERED_POST, mPostList.get(position));
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

}
