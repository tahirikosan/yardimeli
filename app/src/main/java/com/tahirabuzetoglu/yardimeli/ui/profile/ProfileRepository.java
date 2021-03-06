package com.tahirabuzetoglu.yardimeli.ui.profile;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tahirabuzetoglu.yardimeli.data.entity.Post;
import com.tahirabuzetoglu.yardimeli.data.entity.User;
import com.tahirabuzetoglu.yardimeli.utils.SharedPrefData;

import java.util.ArrayList;
import java.util.List;

public class ProfileRepository {

    private Context context;
    private SharedPrefData sharedPrefData;

    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;

    private StorageReference mStorageProfileImage;

    public ProfileRepository(Context context) {
        this.context = context;

        sharedPrefData = new SharedPrefData(context);

        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        auth = FirebaseAuth.getInstance();

        mStorageProfileImage = firebaseStorage.getReference().child("profileImage").child(auth.getCurrentUser().getUid() + ".jpg");

    }

    // Get user info from firestore user doc
    public MutableLiveData<User> getUser() {
        MutableLiveData<User> userLive = new MutableLiveData<>();
        User user = sharedPrefData.loadUser();
        userLive.setValue(user);
        return userLive;
    }


    // Get user post from firestore
    public MutableLiveData<List<Post>> getUserPosts(String ownerID) {
        MutableLiveData<List<Post>> postListLive = new MutableLiveData<>();


        db.collection("posts")
                .whereEqualTo("ownerID", ownerID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<Post> postList = new ArrayList<>();

                        for (DocumentSnapshot childSnapShot : task.getResult().getDocuments()) {
                            Post post = childSnapShot.toObject(Post.class);

                            if(post.getLikes().contains(ownerID)){
                                post.setUserLiked(true);
                            }else{
                                post.setUserLiked(false);
                            }

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


    // Edit user info in firestore
    public MutableLiveData<User> editUser(Uri imageUri, String userName, String userBiyo, String userCity) {
        MutableLiveData<User> userLive = new MutableLiveData<>();

        User user = sharedPrefData.loadUser();

        // prepare post reference for inserting to db
        DocumentReference userRef = db.collection("users").document(auth.getCurrentUser().getUid());

        if (!imageUri.toString().equals(user.getImageUrl())) {
            // add image to storage
            StorageReference filePath = mStorageProfileImage;
            final UploadTask uploadTask = filePath.putFile(imageUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //If user inserting a new image to, use here
                            userRef.update("imageUrl", uri.toString(),
                                    "name", userName,
                                    "description", userBiyo,
                                    "city", userCity)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            user.setName(userName);
                                            user.setDescription(userBiyo);
                                            user.setCity(userCity);
                                            user.setImageUrl(uri.toString());
                                            user.setSuccess(true);

                                            userLive.setValue(user);
                                            sharedPrefData.saveUser(user);

                                            editUserPosts(user);
                                            //editUserLikes(user, userOldName);
                                            //editUserComments(user, userOldName);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.v("Error Editing : ", e.getMessage());
                                    user.setSuccess(false);
                                    userLive.setValue(user);
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v("Error Profile Image: ", "Error while inserting image to storage, " + e.getMessage());
                }
            });

        } else {

            //If user is not inserting a new image to, use here
            userRef.update("name", userName,
                    "description", userBiyo,
                    "city", userCity)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.setName(userName);
                            user.setDescription(userBiyo);
                            user.setCity(userCity);
                            user.setSuccess(true);
                            userLive.setValue(user);
                            sharedPrefData.saveUser(user);

                            editUserPosts(user);
                            // editUserLikes(user, userOldName);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v("Error Editing : ", e.getMessage());
                    user.setSuccess(false);
                    userLive.setValue(user);
                }
            });
        }

        return userLive;
    }

    // Edit ownerName and ownerImageUrl in the posts
    private void editUserPosts(User user) {
        //Update user posts
        db.collection("posts")
                .whereEqualTo("ownerID", user.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot childSnapShot : task.getResult().getDocuments()) {
                            Post post = childSnapShot.toObject(Post.class);

                            db.collection("posts")
                                    .document(post.getId())
                                    .update("ownerName", user.getName(),
                                            "ownerImageUrl", user.getImageUrl()
                                    )
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.v("Success", "User likes updated");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.v("Error", "User posts did not update");
                                }
                            });
                        }
                    }
                });
    }


    // log out user from both auth and db
    public MutableLiveData<User> logOut() {
        MutableLiveData<User> userLive = new MutableLiveData<>();

        User user = new User();
        try {
            db.collection("users")
                    .document(auth.getCurrentUser().getUid())
                    .update("online", false)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            auth.signOut();
                            user.setLogOut(true);
                            userLive.setValue(user);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v("Error log out: ", e.getMessage());
                    user.setLogOut(false);
                    userLive.setValue(user);
                }
            });
        } catch (Exception e) {
            Log.v("Error log out: ", e.getMessage());
            user.setLogOut(false);
            userLive.setValue(user);
        }
        return userLive;
    }

}
