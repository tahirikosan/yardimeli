package com.tahirabuzetoglu.yardimeli.ui.postdetail;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.squareup.picasso.Picasso;
import com.tahirabuzetoglu.yardimeli.R;
import com.tahirabuzetoglu.yardimeli.data.entity.Post;
import com.tahirabuzetoglu.yardimeli.ui.comment.CommentActivity;
import com.tahirabuzetoglu.yardimeli.ui.main.FeedActivity;
import com.tahirabuzetoglu.yardimeli.ui.main.PostViewModel;
import com.tahirabuzetoglu.yardimeli.ui.profile.ProfileActivity;

import static com.tahirabuzetoglu.yardimeli.ui.main.FeedActivity.COMMENT_POST;


public class PostDetailActivity extends AppCompatActivity {

    private static final String USER_POST = "USER_POST";

    private Post post;

    private ImageView ivUserPhoto;
    private ImageView ivPostImage;
    private TextView tvUserName;
    private TextView tvDescription;
    private TextView tvCancelDeleting;
    private TextView tvDeletePost;
    private ImageButton ibCommentPost;
    private ImageButton ibPostDelete;
    private EditText etComment;
    private Button btnInsertComment;
    private CardView cvDeletePostAlert;
    private ProgressDialog progressDialog;

    private PostViewModel postViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        setUIComponents();
        setPostViewModel();

        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra(USER_POST);
        setPostUI(post);

        ibPostDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cvDeletePostAlert.setVisibility(View.VISIBLE);
            }
        });

        tvCancelDeleting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvDeletePostAlert.setVisibility(View.GONE);
            }
        });

        tvDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost(post);
            }
        });

        ibCommentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this, CommentActivity.class);
                intent.putExtra(COMMENT_POST, post);
                startActivity(intent);
            }
        });


    }


    // Delete post from firestore db
    private void deletePost(Post currentPost){
        showProgressDialog();

        postViewModel.deletePost(currentPost);
        postViewModel.deletedPostLive.observe(this, new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                if(post.isSuccess()){
                    Toast.makeText(PostDetailActivity.this, "Post silindi", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    Intent intent = new Intent(PostDetailActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                };

            }
        });
    }


    // set all views
    private void setUIComponents(){
        ivUserPhoto = findViewById(R.id.iv_owner_image);
        ivPostImage = findViewById(R.id.iv_post_image);
        tvUserName = findViewById(R.id.tv_post_owner);
        tvDescription = findViewById(R.id.tv_post_desc);
        tvCancelDeleting = findViewById(R.id.tv_cancel_delete);
        tvDeletePost = findViewById(R.id.tv_delete);
        ibCommentPost = findViewById(R.id.ib_post_comment);
        ibPostDelete = findViewById(R.id.ib_post_delete);
        etComment = findViewById(R.id.et_comment);
        btnInsertComment = findViewById(R.id.btn_insert_comment);
        cvDeletePostAlert = findViewById(R.id.cv_delete_post_alert);
    }

    // put data into views
    private void setPostUI(Post post){

        Picasso.get().load(post.getOwnerImageUrl()).into(ivUserPhoto);
        Picasso.get().load(post.getImageUrl()).into(ivPostImage);

        tvUserName.setText(post.getOwnerName());
        tvDescription.setText(post.getDescription());
    }

    private void setPostViewModel(){
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
    }



    private void showProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
    }
}