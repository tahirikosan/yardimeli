package com.tahirabuzetoglu.yardimeli.ui.other_user;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tahirabuzetoglu.yardimeli.data.entity.Post;
import com.tahirabuzetoglu.yardimeli.data.entity.User;

import java.util.List;

public class OtherProfileViewModel extends AndroidViewModel {
    private OtherProfileRepository repository;

    public LiveData<User> otherUserLive;
    public LiveData<List<Post>> otherUserPostListLive;

    public OtherProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new OtherProfileRepository();
    }

    public void getOtherUser(String otherUserId){
        otherUserLive = repository.getOtherUser(otherUserId);
    }

    public void getOtherUserPostList(String otherUserId){
        otherUserPostListLive = repository.getOtherUserPosts(otherUserId);
    }


}
