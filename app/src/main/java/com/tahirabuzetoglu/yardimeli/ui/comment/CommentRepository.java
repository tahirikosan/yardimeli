package com.tahirabuzetoglu.yardimeli.ui.comment;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tahirabuzetoglu.yardimeli.data.entity.Comment;
import com.tahirabuzetoglu.yardimeli.data.entity.User;
import com.tahirabuzetoglu.yardimeli.utils.SharedPrefData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentRepository {

    private Context mContext;
    private FirebaseFirestore db;
    private SharedPrefData sharedPrefData;

    public CommentRepository(Context context) {
        mContext = context;
        db = FirebaseFirestore.getInstance();
        sharedPrefData = new SharedPrefData(mContext);
    }

    public MutableLiveData<List<Comment>> getComments(String postId){
        MutableLiveData<List<Comment>> commentListLive = new MutableLiveData<>();

        List<Comment> commentList = new ArrayList<>();

        db.collection("comments")
                .document(postId)
                .collection("comment")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot childSnapShot: queryDocumentSnapshots.getDocuments()){
                            Comment comment = childSnapShot.toObject(Comment.class);
                            comment.setSuccess(true);
                            commentList.add(comment);
                        }

                        commentListLive.setValue(commentList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Comment comment = new Comment();
                comment.setSuccess(false);
                commentList.add(comment);
                commentListLive.setValue(commentList);
            }
        });

        return commentListLive;
    }

    public MutableLiveData<Comment> insertComment(String postId, String ownerComment, User user){
        MutableLiveData<Comment> commentLive = new MutableLiveData<>();
        Comment comment = new Comment();

        DocumentReference commentRef = db.collection("comments").document(postId).collection("comment").document();

        Double createdAt = Double.valueOf(System.currentTimeMillis() / 1000l);

        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put("username", user.getName());
        commentMap.put("userID", user.getId());
        commentMap.put("comment", ownerComment);
        commentMap.put("date", createdAt);
        commentMap.put("documentID", commentRef.getId());

        commentRef.set(commentMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        comment.setUsername(user.getName());
                        comment.setUserID(user.getId());
                        comment.setComment(ownerComment);
                        comment.setDocumentID(commentRef.getId());
                        comment.setSuccess(true);
                        commentLive.setValue(comment);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("Comment Insert Error", e.getMessage());
                comment.setSuccess(false);
                commentLive.setValue(comment);
            }
        });

        return commentLive;
    }

    public MutableLiveData<List<Comment>> deleteComment(String postId, Comment currentComment, User user, List<Comment> commentList){

        MutableLiveData<List<Comment>> commentListLive = new MutableLiveData<>();

        db.collection("comments")
                .document(postId)
                .collection("comment")
                .document(currentComment.getDocumentID())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // delete comment from default comment list for recycler view
                        int position = commentList.indexOf(currentComment);
                        commentList.remove(position);

                        if (commentList.size() >= 1){
                            commentList.get(0).setSuccess(true);
                        }
                        commentListLive.setValue(commentList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("Comment delete error: ", e.getMessage());
                commentList.get(0).setSuccess(false);
                commentListLive.setValue(commentList);
            }
        });

        return commentListLive;
    }
}
