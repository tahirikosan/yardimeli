package com.tahirabuzetoglu.yardimeli.ui.comment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tahirabuzetoglu.yardimeli.data.entity.Comment;
import com.tahirabuzetoglu.yardimeli.data.entity.User;

import java.util.List;

public class CommentViewModel extends AndroidViewModel {

    private CommentRepository repository;
    public LiveData<Comment> commentLiveData;
    public LiveData<List<Comment>> commenListLiveData;

    public CommentViewModel(@NonNull Application application) {
        super(application);

        repository = new CommentRepository(application);
    }

    public void getComments(String postId){
        commenListLiveData = repository.getComments(postId);
    }

    public void insertComment(String postId, String ownerComment, User user){
        commentLiveData = repository.insertComment(postId, ownerComment, user);
    }

    public void deleteComment(String postId, Comment currentComment, User user, List<Comment> commentList){
        commenListLiveData = repository.deleteComment(postId, currentComment, user, commentList);
    }
}
