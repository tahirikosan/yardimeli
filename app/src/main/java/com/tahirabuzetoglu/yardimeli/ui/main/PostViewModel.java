package com.tahirabuzetoglu.yardimeli.ui.main;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tahirabuzetoglu.yardimeli.data.entity.Post;

import java.util.List;

public class PostViewModel extends AndroidViewModel {


    private PostRepository repository;
    public LiveData<List<Post>> postList;
    public LiveData<List<Post>> likedPostList;
    public LiveData<List<Post>> savedPostList;
    public LiveData<Post> newPost;
    public LiveData<Post> deletedPostLive;

    // it also for dislike
    public LiveData<Post> likePost;
    public LiveData<Post> dislikedPostLive;
    public LiveData<Post> handleSaveLive;
    public LiveData<Post> handleLikeLive;

    public PostViewModel(@NonNull Application application) {
        super(application);

        repository = new PostRepository(application);
    }


    // get all posts in posts collection firestore db
    public void getPostList(){
        postList = repository.getPostList();
    }

    // insert a new post to posts Collection firestore db
    public void insertPost(Uri imageUri, String description, String phone, String location){
        newPost = repository.insertPost(imageUri, description, phone, location);
    }

   // delete post from firestore
    public void deletePost(Post post){
        deletedPostLive = repository.deletePost(post);
    }

    // get user liked posts posts collection firestore db
    public void getPostListLiked(){
        likedPostList = repository.getPostListLiked();
    }

    // dislike the post
    public void dislikePost(Post currentPost){
        handleLikeLive = repository.dislikePost(currentPost);
    }

    public void likePost(Post currentPost){
        handleLikeLive = repository.likePost(currentPost);
    }


}
