package com.tahirabuzetoglu.yardimeli.ui.liked;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.tahirabuzetoglu.yardimeli.R;
import com.tahirabuzetoglu.yardimeli.data.entity.Post;
import com.tahirabuzetoglu.yardimeli.data.entity.User;
import com.tahirabuzetoglu.yardimeli.ui.comment.CommentActivity;
import com.tahirabuzetoglu.yardimeli.ui.main.FeedActivity;
import com.tahirabuzetoglu.yardimeli.ui.main.PostAdapter;
import com.tahirabuzetoglu.yardimeli.ui.main.PostViewModel;
import com.tahirabuzetoglu.yardimeli.ui.postdetail.PostDetailActivity;
import com.tahirabuzetoglu.yardimeli.utils.SharedPrefData;

import java.util.List;

import static com.tahirabuzetoglu.yardimeli.ui.main.FeedActivity.COMMENT_POST;
import static com.tahirabuzetoglu.yardimeli.ui.other_user.OtherUserProfileActivity.TRANSFERED_POST;

public class LikedPostsActivity extends AppCompatActivity implements PostAdapter.OnItemClickListener {

    public static final String OTHER_USER_ID = "OTHER_USER_ID";
    public static final String START_PROFILE_FRAGMENT = "START_PROFILE_FRAGMENT";
    public static final String LIKED_POSTS = "LIKED_POSTS";

    private TextView tvNoLikedPostsWarning;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    private PostAdapter postAdapter;

    private List<Post> mPostList;

    private PostViewModel postViewModel;

    private SharedPrefData sharedPrefData;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_posts);

        setPostViewModel();

        tvNoLikedPostsWarning = findViewById(R.id.tv_warning_no_liked_posts);

        recyclerView = findViewById(R.id.recycler_view_liked_post);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        sharedPrefData = new SharedPrefData(this);
        user = sharedPrefData.loadUser();

        getLikedPosts();

    }

    private void getLikedPosts() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Lütfen bekleyiniz...");
        progressDialog.show();

        postViewModel.getPostListLiked();
        postViewModel.likedPostList.observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> postList) {
                if(!postList.isEmpty()){
                    if (postList.get(0).isSuccess()) {
                        mPostList = postList;
                        setPostAdapter(postList);

                        tvNoLikedPostsWarning.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(LikedPostsActivity.this, "Lütfen tekrar deneyiniz", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LikedPostsActivity.this, "henüz post beğenmediniz", Toast.LENGTH_SHORT).show();
                    tvNoLikedPostsWarning.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();
            }
        });
    }


    private void setPostAdapter(List<Post> postList) {
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);
        postAdapter.setOnItemClickListener(this);
    }

    private void setPostViewModel() {
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
    }


    // Dislike the post
    @Override
    public void onLikeButtonClicked(int position, ImageButton imageButton, TextView textView) {
        YoYo.with(Techniques.BounceIn).duration(1000).playOn(imageButton);

        if(mPostList.get(position).isUserLiked()){
            //unsave the post
            postViewModel.dislikePost(mPostList.get(position));
            imageButton.setImageResource(R.drawable.ic_heart_black);
            textView.setText((mPostList.get(position).getLikeCount() - 1) + " beğenme");
        }else{
            postViewModel.likePost(mPostList.get(position));
            imageButton.setImageResource(R.drawable.ic_heart_red);
            textView.setText((mPostList.get(position).getLikeCount() + 1) + " beğenme");
        }

        postViewModel.handleLikeLive.observe(this, new Observer<Post>() {
            @Override
            public void onChanged(Post post) {
                if(post.isSuccess()){
                   /* if(post.isUserLiked()){
                        imageButton.setImageResource(R.drawable.ic_heart_red);
                    }else{
                        imageButton.setImageResource(R.drawable.ic_heart_black);
                    }
                    textView.setText(post.getLikeCount() + " beğenme");*/
                }else{
                    Toast.makeText(LikedPostsActivity.this, "Bir hata oluştu lütfen bizimle iletişime geçiniz", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onCommentButtonClicked(int position) {
        // Send selected post to see comments
        Post post = mPostList.get(position);
        Intent intent = new Intent(LikedPostsActivity.this, CommentActivity.class);
        intent.putExtra(COMMENT_POST, post);
        startActivity(intent);
    }


    @Override
    public void onOwnerImageClicked(int position) {
    /*   String ownerId = mPostList.get(position).getOwnerID();

        if(ownerId.equals(user.getId())){
            Intent startProfileFragment = new Intent(LikedPostsActivity.this, MainActivity.class);
            startProfileFragment.putExtra(START_PROFILE_FRAGMENT, true);
            startActivity(startProfileFragment);
        }else{
            Intent intent = new Intent(LikedPostsActivity.this, OtherUserProfileActivity.class);
            intent.putExtra(OTHER_USER_ID , mPostList.get(position).getOwnerID());
            startActivity(intent);
        }*/
    }

    @Override
    public void onPostClicked(int position) {
        Post post = mPostList.get(position);
        Intent intent = new Intent(LikedPostsActivity.this, PostDetailActivity.class);
        intent.putExtra(TRANSFERED_POST, post);
        startActivity(intent);
    }
}