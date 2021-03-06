package com.tahirabuzetoglu.yardimeli.ui.profile;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tahirabuzetoglu.yardimeli.data.entity.Post;
import com.tahirabuzetoglu.yardimeli.data.entity.User;

import java.util.List;

public class ProfileViewModel extends AndroidViewModel {
    public LiveData<User> userLive;
    public LiveData<List<Post>> userPostsLive;
    public LiveData<User> editedUserLive;
    private ProfileRepository repository;
    public LiveData<User> logOutUserLive;


    public ProfileViewModel(@NonNull Application application) {
        super(application);

        repository = new ProfileRepository(application);
    }

    public void getUser(){
        userLive = repository.getUser();
    }

    public void getUserPosts(String ownerID){
        userPostsLive = repository.getUserPosts(ownerID);
    }

    public void editUser(Uri imageUri, String userName, String userBiyo, String userCity){
        editedUserLive = repository.editUser(imageUri, userName, userBiyo, userCity);
    }

    public void logOut(){
        logOutUserLive = repository.logOut();
    }

}
