package com.tahirabuzetoglu.yardimeli.ui.main;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tahirabuzetoglu.yardimeli.data.entity.Post;
import com.tahirabuzetoglu.yardimeli.data.entity.User;
import com.tahirabuzetoglu.yardimeli.utils.SharedPrefData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostRepository {

    private FirebaseAuth mAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference mStoragePostImages;
    private FirebaseFirestore db;
    private SharedPrefData sharedPrefData;
    private User currentUser;

    public PostRepository(Context context) {
        sharedPrefData = new SharedPrefData(context);
        currentUser = sharedPrefData.loadUser();
        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        mStoragePostImages = firebaseStorage.getReference().child("image").child(mAuth.getCurrentUser().getUid());
        db = FirebaseFirestore.getInstance();
    }


    // get all post from firebase
    public MutableLiveData<List<Post>> getPostList(){
        //keep data synched
        MutableLiveData<List<Post>> postListLiveData = new MutableLiveData<>();
        List<Post> postList = new ArrayList<>();

        //Get all posts
        db.collection("posts")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            String userId = currentUser.getId().trim();

                            for(DocumentSnapshot childSnapshot: task.getResult().getDocuments()){

                                Post post = childSnapshot.toObject(Post.class);
                                post.setSuccess(true);
                                post.setLikeCount(post.getLikes().size());

                                if(post.getLikes().contains(userId)){
                                    post.setUserLiked(true);
                                }else{
                                    post.setUserLiked(false);
                                }

                                //Log.v("Repodayım : ", post.getLikes().toString());
                                postList.add(post);
                            }

                            postListLiveData.setValue(postList);
                        }else{
                            Post post = new Post();
                            post.setSuccess(false);
                            postList.add(0, post);
                            postListLiveData.setValue(postList);

                            Log.v("Getting posts error: ", task.getException().getMessage());
                        }
                    }
                });

        return postListLiveData;
    }

    // insert a new post to firebase
    public MutableLiveData<Post> insertPost(Uri imageUri, String description, String phone, String location){
        MutableLiveData<Post> newPostLive = new MutableLiveData<>();

        // prepare post reference for inserting to db
        DocumentReference newPostRef =  db.collection("posts").document();

        // add image to storage
        StorageReference filePath = mStoragePostImages.child(newPostRef.getId()+".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //user who liked the post list
                        List<String> likes = new ArrayList<>();
                        Double createdAt = Double.valueOf(System.currentTimeMillis() / 1000l);

                        Map<String, Object> cloudPost = new HashMap<>();
                        cloudPost.put("id", newPostRef.getId());
                        cloudPost.put("ownerImageUrl", currentUser.getImageUrl());
                        cloudPost.put("description", description);
                        cloudPost.put("imageUrl", uri.toString());
                        cloudPost.put("ownerID", currentUser.getId().trim());
                        cloudPost.put("ownerName", currentUser.getName());
                        cloudPost.put("likes", likes);
                        cloudPost.put("date", createdAt);
                        cloudPost.put("location",location);
                        cloudPost.put("phoneNumber", phone);

                        // insert post to db
                        newPostRef.set(cloudPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                //make a new post for live data
                                Post newPost = new Post(
                                        newPostRef.getId(),
                                        currentUser.getId().trim(),
                                        currentUser.getName(),
                                        uri.toString(),
                                        currentUser.getImageUrl(),
                                        description,
                                        likes,
                                        createdAt,
                                        location,
                                        phone
                                );
                                newPost.setSuccess(true);
                                newPostLive.setValue(newPost);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.v("Exception New Posting", e.getMessage());
                                Post newPost = new Post();
                                newPost.setSuccess(false);
                                newPostLive.setValue(newPost);
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("Exception Upload Image", e.getMessage());
                Post newPost = new Post();
                newPost.setSuccess(false);
                newPostLive.setValue(newPost);
            }
        });

        return newPostLive;
    }


    // delete post from firestore
    public MutableLiveData<Post> deletePost(Post post){
        MutableLiveData<Post> deletedPostLive = new MutableLiveData<>();

        StorageReference imageRef = mStoragePostImages.child(post.getId()+ ".jpg");

        imageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                db.collection("posts")
                        .document(post.getId())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                db.collection("likedPosts")
                                        .document(currentUser.getId())
                                        .update("postIDs", FieldValue.arrayRemove(post.getId()))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                db.collection("comments")
                                                        .document(post.getId())
                                                        .delete()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    post.setSuccess(true);
                                                                    deletedPostLive.setValue(post);
                                                                }
                                                            }
                                                        });

                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v("Deleting post error : ", e.getMessage());
                        Post post = new Post();
                        post.setSuccess(false);
                        deletedPostLive.setValue(post);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("Deleting image error : ", e.getMessage());
                Post post = new Post();
                post.setSuccess(false);
                deletedPostLive.setValue(post);
            }
        });


        return deletedPostLive;
    }

    // like a new post in firebase
    public MutableLiveData<Post> likePost(Post post){
        MutableLiveData<Post> savedPostLive = new MutableLiveData<>();

        // prepare post reference for inserting to db
        DocumentReference savedPostRef =  db.collection("likedPosts").document(currentUser.getId());

        savedPostRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.getResult().get("postIDs") != null){
                            //if doc exist just update postIDs
                            savedPostRef.update("postIDs", FieldValue.arrayUnion(post.getId()))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                                //add user id to post's saved list in firestore db
                                                db.collection("posts")
                                                        .document(post.getId())
                                                        .update("likes", FieldValue.arrayUnion(currentUser.getId().trim()))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                post.setSuccess(true);
                                                                post.setUserLiked(true);
                                                                post.setLikeCount(post.getLikeCount() + 1);
                                                                savedPostLive.setValue(post);
                                                            }
                                                        });
                                            }else{
                                                Log.v("Task Error Like Post ID", task.getException().getMessage());
                                                post.setSuccess(false);
                                                savedPostLive.setValue(post);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.v("Exception Like Post ID", e.getMessage());
                                    post.setSuccess(false);
                                    savedPostLive.setValue(post);
                                }
                            });
                        }else{
                            List<String> postIDs = new ArrayList<>();
                            postIDs.add(0, post.getId());

                            Map<String, Object> savedPost = new HashMap<>();
                            savedPost.put("postIDs", postIDs);

                            savedPostRef.set(savedPost, SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.v("SUCCES : ", "New likedPosts created");
                                            post.setSuccess(true);
                                            savedPostLive.setValue(post);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.v("Failure : ", "New likedPosts did not created");
                                    post.setSuccess(false);
                                    savedPostLive.setValue(post);
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("Failure : ", "New likedPosts did not created");
                post.setSuccess(false);
                savedPostLive.setValue(post);
            }
        });




        return savedPostLive;


        /*MutableLiveData<Post> savedPostLive = new MutableLiveData<>();

        //add user id to post's saved list in firestore db
        db.collection("posts")
                .document(post.getId())
                .update("likes", FieldValue.arrayUnion(currentUser.getId().trim()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        post.setSuccess(true);
                        post.setUserLiked(true);
                        post.setLikeCount(post.getLikeCount() + 1);
                        savedPostLive.setValue(post);
                    }
                });


        return savedPostLive;*/
    }

    // dislike post from firestore
    public MutableLiveData<Post> dislikePost(Post post){

        MutableLiveData<Post> postLive = new MutableLiveData<>();

        db.collection("likedPosts")
                .document(currentUser.getId())
                .update("postIDs", FieldValue.arrayRemove(post.getId()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            //add user id to post's saved list in firestore db
                            db.collection("posts")
                                    .document(post.getId())
                                    .update("likes", FieldValue.arrayRemove(currentUser.getId()))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            post.setSuccess(true);
                                            post.setUserLiked(false);
                                            post.setLikeCount(post.getLikeCount() - 1);
                                            postLive.setValue(post);
                                        }
                                    });
                        }else{
                            Log.v("dislike post error:", task.getException().getMessage());
                            post.setSuccess(false);
                            postLive.setValue(post);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("dislike post error:", e.getMessage());
                post.setSuccess(false);
                postLive.setValue(post);
            }
        });

        return postLive;
    }

    // get user's liked posts
    public MutableLiveData<List<Post>> getPostListLiked(){

        MutableLiveData<List<Post>> postListLiveData = new MutableLiveData<>();
        List<Post> postList = new ArrayList<>();

        db.collection("likedPosts")
                .document(currentUser.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Map<String, Object> tmpMap = task.getResult().getData();
                            if(tmpMap != null){
                                List<String> postIDs = (List) tmpMap.get("postIDs");
                                if(postIDs.size() >= 1){
                                    for(String postId : postIDs){

                                        db.collection("posts")
                                                .document(postId)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            DocumentSnapshot documentSnapshot = task.getResult();

                                                            Post post = documentSnapshot.toObject(Post.class);
                                                            post.setLikeCount(post.getLikes().size());
                                                            post.setSuccess(true);
                                                            post.setUserLiked(true);
                                                            postList.add(post);
                                                            postListLiveData.setValue(postList);
                                                        }else{
                                                            Post post = new Post();
                                                            post.setSuccess(false);
                                                            post.setUserLiked(false);
                                                            postList.add(0, post);
                                                            postListLiveData.setValue(postList);

                                                            Log.v("Failure : ","Gettin liked posts task error => " + task.getException().getMessage());
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Post post = new Post();
                                                post.setSuccess(false);
                                                post.setUserLiked(false);
                                                postList.add(0, post);
                                                postListLiveData.setValue(postList);

                                                Log.v("Error : ","Gettin liked posts error => " + e.getMessage());
                                            }
                                        });
                                    }
                                }else{
                                    postListLiveData.setValue(postList);

                                    Log.v("Failure : ","Could not get any post from db");
                                }
                            }else{
                                postListLiveData.setValue(postList);

                                Log.v("Failure : ","Could not get any post from db");
                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Post post = new Post();
                post.setSuccess(false);
                post.setUserLiked(false);
                postList.add(0, post);
                postListLiveData.setValue(postList);

                Log.v("Error : ","Gettin liked posts error => " + e.getMessage());
            }
        });

        return postListLiveData;
    }


}
