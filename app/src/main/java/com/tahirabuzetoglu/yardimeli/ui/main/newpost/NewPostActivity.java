package com.tahirabuzetoglu.yardimeli.ui.main.newpost;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.tahirabuzetoglu.yardimeli.R;
import com.tahirabuzetoglu.yardimeli.data.entity.Post;
import com.tahirabuzetoglu.yardimeli.ui.main.FeedActivity;
import com.tahirabuzetoglu.yardimeli.ui.main.PostViewModel;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class NewPostActivity extends AppCompatActivity {


    public static final int GALLERY_REQUEST = 25;

    private ImageButton imageButton;
    private EditText etDesc;
    private EditText etLocation;
    private EditText etPhone;
    private Button btnPost;
    private ProgressDialog progressDialog;

    private Uri imageUri;
    private String description;
    private String phone;
    private String location;

    private PostViewModel postViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        setUIComponents();
        setPostViewModel();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTexts();
                startPosting();
            }
        });
    }


    private void setUIComponents(){
        imageButton = findViewById(R.id.ib_new_post);
        etDesc = findViewById(R.id.et_desc);
        etLocation = findViewById(R.id.et_location);
        etPhone = findViewById(R.id.et_phone);
        btnPost = findViewById(R.id.btn_post);

        progressDialog = new ProgressDialog(this);
    }

    private void setTexts(){
        description = etDesc.getText().toString().trim();
        phone = etLocation.getText().toString().trim();
        location = etPhone.getText().toString().trim();
    }

    private void setPostViewModel(){
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            if(requestCode == GALLERY_REQUEST){
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

                imageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageButton.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void startPosting(){

        if(imageUri == null || description.isEmpty()){
            Toast.makeText(this, "Please fill all inputs.", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

        postViewModel.insertPost(imageUri, description, phone, location);
        postViewModel.newPost.observe(this, new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                if(post.isSuccess()){
                    Toast.makeText(NewPostActivity.this, "Postunuz paylaşıldı", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    startActivity(new Intent(NewPostActivity.this, FeedActivity.class));
                    finish();
                }else{
                    Toast.makeText(NewPostActivity.this, "Post paylaşılırken bir hata oluştu", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        });
    }


    private void showProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
    }

}