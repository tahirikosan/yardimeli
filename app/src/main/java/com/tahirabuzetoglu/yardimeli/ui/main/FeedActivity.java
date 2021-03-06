package com.tahirabuzetoglu.yardimeli.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.tahirabuzetoglu.yardimeli.R;
import com.tahirabuzetoglu.yardimeli.data.entity.Post;
import com.tahirabuzetoglu.yardimeli.data.entity.User;
import com.tahirabuzetoglu.yardimeli.ui.comment.CommentActivity;
import com.tahirabuzetoglu.yardimeli.ui.main.newpost.NewPostActivity;
import com.tahirabuzetoglu.yardimeli.ui.other_user.OtherUserProfileActivity;
import com.tahirabuzetoglu.yardimeli.ui.postdetail.PostDetailActivity;
import com.tahirabuzetoglu.yardimeli.ui.profile.ProfileActivity;
import com.tahirabuzetoglu.yardimeli.utils.SharedPrefData;

import java.util.ArrayList;
import java.util.List;

import static com.tahirabuzetoglu.yardimeli.ui.liked.LikedPostsActivity.OTHER_USER_ID;
import static com.tahirabuzetoglu.yardimeli.ui.other_user.OtherUserProfileActivity.TRANSFERED_POST;

public class FeedActivity extends AppCompatActivity  implements PostAdapter.OnItemClickListener{

    public static final String COMMENT_POST = "COMMENT_POST";

    private ImageView ivNewPost;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    private ProgressDialog progressDialog;
    private ImageView ivGoProfile;

    private SharedPrefData sharedPrefData;
    private User user;
    private PostViewModel postViewModel;
    private List<Post> postList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        ivNewPost = findViewById(R.id.btn_go_to_add_post);
        ivGoProfile = findViewById(R.id.iv_go_to_profile);
        recyclerView = findViewById(R.id.recycler_view_post);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        progressDialog = new ProgressDialog(this);

        sharedPrefData = new SharedPrefData(this);
        user = sharedPrefData.loadUser();

        setPostViewModel();
        getPosts();

        ivNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FeedActivity.this, NewPostActivity.class));
            }
        });
        ivGoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedActivity.this, ProfileActivity.class));
            }
        });
    }

    private void getPosts(){
        showProgressDialog();

        postViewModel.getPostList();
        postViewModel.postList.observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                if(posts.size() > 1){
                    if(posts.get(0).isSuccess()){
                        // set list
                        postList = posts;
                        setPostAdapter();
                        progressDialog.dismiss();
                    }else{
                        Toast.makeText(FeedActivity.this, "Postlar getirilemedi, lütfen tekrar deneyiniz", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }else{
                    postList = posts;
                    setPostAdapter();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Lütfen bekleyiniz");
        progressDialog.show();
    }

    private void setPostAdapter(){
        //set post adapter
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);
        postAdapter.setOnItemClickListener(FeedActivity.this);
    }

    private void setPostViewModel(){
        postViewModel = new ViewModelProvider(FeedActivity.this).get(PostViewModel.class);
    }

    @Override
    public void onLikeButtonClicked(int position, ImageButton imageButton, TextView textView) {
        YoYo.with(Techniques.BounceIn).duration(1000).playOn(imageButton);

        if(postList.get(position).isUserLiked()){
            //unsave the post
            postViewModel.dislikePost(postList.get(position));
            imageButton.setImageResource(R.drawable.ic_heart_black);
            textView.setText((postList.get(position).getLikeCount() - 1) + " beğenme");
        }else{
            postViewModel.likePost(postList.get(position));
            imageButton.setImageResource(R.drawable.ic_heart_red);
            textView.setText((postList.get(position).getLikeCount() + 1) + " beğenme");
        }

        postViewModel.handleLikeLive.observe(this, new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                if(post.isSuccess()){
                    /*if(post.isUserLiked()){
                        imageButton.setImageResource(R.drawable.ic_heart_red);
                    }else{
                        imageButton.setImageResource(R.drawable.ic_heart_black);
                    }
                    textView.setText(post.getLikeCount() + " beğenme");*/
                }else{
                    Toast.makeText(FeedActivity.this, "Bir hata oluştu lütfen bizimle iletişime geçiniz", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onCommentButtonClicked(int position) {
// Send selected post to see comments
        Post post = postList.get(position);
        Intent intent = new Intent(FeedActivity.this, CommentActivity.class);
        intent.putExtra(COMMENT_POST, post);
        startActivity(intent);
    }


    @Override
    public void onOwnerImageClicked(int position) {
        String ownerId = postList.get(position).getOwnerID();

        if(ownerId.equals(user.getId())){
            Intent startProfileFragment = new Intent(FeedActivity.this, ProfileActivity.class);
            startActivity(startProfileFragment);
        }else{
            Intent intent = new Intent(FeedActivity.this, OtherUserProfileActivity.class);
            intent.putExtra(OTHER_USER_ID , postList.get(position).getOwnerID());
            startActivity(intent);
        }
    }

    @Override
    public void onPostClicked(int position) {
        Post post = postList.get(position);
        Intent intent = new Intent(FeedActivity.this, PostDetailActivity.class);
        intent.putExtra(TRANSFERED_POST, post);
        startActivity(intent);
    }
}