package com.tahirabuzetoglu.yardimeli.ui.edit_profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import com.tahirabuzetoglu.yardimeli.R;
import com.tahirabuzetoglu.yardimeli.data.entity.User;
import com.tahirabuzetoglu.yardimeli.ui.main.FeedActivity;
import com.tahirabuzetoglu.yardimeli.ui.profile.ProfileActivity;
import com.tahirabuzetoglu.yardimeli.ui.profile.ProfileViewModel;
import com.theartofdev.edmodo.cropper.CropImage;


public class EditProfileActivity extends AppCompatActivity {

    public static final int GALLERY_REQUEST = 25;
    public static final String FROM_EDIT_PROFILE_ACTIVIY = "FROM_EDIT_PROFILE_ACTIVIY";

    private ImageView ivUserPhoto;
    private EditText etName;
    private EditText etBiyo;
    private EditText etCity;
    private Button btnEditDone;

    private User mUser;
    private ProfileViewModel profileViewModel;

    private Uri imageUri;
    private String imageUrl = "default";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setUI();
        setProfileViewModel();
        getUser();

        ivUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhotoFromGallery();
            }
        });

        btnEditDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = etName.getText().toString().trim();
                String userBiyo = etBiyo.getText().toString().trim();
                String userCity = etCity.getText().toString().trim();

                if(imageUri == null){
                    imageUri =  Uri.parse("default");
                }

                if(validateInputs(userName, userBiyo, userCity)){
                    editProfile(imageUri, userName, userBiyo, userCity);
                }else{
                    Toast.makeText(EditProfileActivity.this, "Please fill all inputs", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void editProfile(Uri imageUri, String userName, String userBiyo, String userCity){

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        profileViewModel.editUser(imageUri, userName, userBiyo, userCity);
        profileViewModel.editedUserLive.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user.isSuccess()) {
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(FROM_EDIT_PROFILE_ACTIVIY, true);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(EditProfileActivity.this, "Could not edit user, please try again", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private boolean validateInputs(String userName, String userBiyo, String userCity){
        if(!userName.isEmpty() && !userBiyo.isEmpty() && !userCity.isEmpty()){
            return true;
        }else{
            return false;
        }
    }


    private void selectPhotoFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    private void getUser(){
        profileViewModel.getUser();
        profileViewModel.userLive.observe(this ,new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mUser = user;
                setUserUI(user);

                imageUri = Uri.parse(mUser.getImageUrl());
            }
        });
    }

    // Put info into views
    private void setUserUI(User user){

        if(user.getImageUrl().equals("default")){
            ivUserPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_image_24));
        }else{
            Picasso.get().load(user.getImageUrl()).into(ivUserPhoto);
            ivUserPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        etName.setText(user.getName());
        etBiyo.setText(user.getDescription());
        etCity.setText(user.getCity());
    }

    // prepare views
    private void setUI(){
        ivUserPhoto = findViewById(R.id.iv_edit_user_photo);
        etName = findViewById(R.id.et_edit_name);
        etBiyo = findViewById(R.id.et_edit_biyo);
        etCity = findViewById(R.id.et_edit_city);
        btnEditDone = findViewById(R.id.btn_edit_done);
    }

    private void setProfileViewModel(){
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Select ımage from gallerry
        if(resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                imageUri = data.getData();

                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(this);
            }
        }

        // Crop image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();

                ivUserPhoto.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}