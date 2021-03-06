package com.tahirabuzetoglu.yardimeli.ui.other_user;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tahirabuzetoglu.yardimeli.data.entity.Post;
import com.tahirabuzetoglu.yardimeli.data.entity.User;

import java.util.ArrayList;
import java.util.List;


public class OtherProfileRepository {

    private FirebaseFirestore db;

    public OtherProfileRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<User> getOtherUser(String otherUserId){
        MutableLiveData<User> otherUserLive = new MutableLiveData<>();

        db.collection("users")
                .document(otherUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            User user = documentSnapshot.toObject(User.class);
                            user.setSuccess(true);
                            otherUserLive.setValue(user);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("Error :", "Getting other user error => " + e.getMessage());
                User user = new User();
                user.setSuccess(false);
                otherUserLive.setValue(user);
            }
        });

        return otherUserLive;
    }

    // Get user post from firestore
    public MutableLiveData<List<Post>> getOtherUserPosts(String otherUserId) {
        MutableLiveData<List<Post>> postListLive = new MutableLiveData<>();


        db.collection("posts")
                .whereEqualTo("ownerID", otherUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<Post> postList = new ArrayList<>();

                        for (DocumentSnapshot childSnapShot : task.getResult().getDocuments()) {
                            Post post = childSnapShot.toObject(Post.class);

                            postList.add(post);
                        }

                        postListLive.setValue(postList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("Error user posts: ", e.getMessage());
            }
        });

        return postListLive;
    }



}
