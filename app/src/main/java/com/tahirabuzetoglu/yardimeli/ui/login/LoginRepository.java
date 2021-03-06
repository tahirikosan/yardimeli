package com.tahirabuzetoglu.yardimeli.ui.login;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tahirabuzetoglu.yardimeli.data.entity.User;
import com.tahirabuzetoglu.yardimeli.utils.SharedPrefData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginRepository {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPrefData sharedPrefData;

    public LoginRepository(Context context) {
        sharedPrefData = new SharedPrefData(context);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    // Create a new user in cloud db if not exist there GoogleSignIn
    public MutableLiveData<User> createUserInDb(String phoneNumber) {
        MutableLiveData<User> newUser = new MutableLiveData<>();

        FirebaseUser mCurrentUser = mAuth.getCurrentUser();

        // create user
        Map<String, Object> cloudUser = new HashMap<>();
        cloudUser.put("id", mCurrentUser.getUid());
        cloudUser.put("name","Adınızı değiştirin");
        cloudUser.put("email", "Email adresinizi değiştirin");
        cloudUser.put("city", "konumuzu değiştirin");
        cloudUser.put("imageUrl", "default");
        cloudUser.put("description", "açıklamayı değiştirin");
        cloudUser.put("phoneNumber", phoneNumber);


        // auth user
        if (mCurrentUser != null) {

            db.collection("users")
                    .whereEqualTo("phoneNumber", phoneNumber)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                if(!task.getResult().getDocuments().isEmpty()){

                                    User user = task.getResult().getDocuments().get(0).toObject(User.class);
                                    sharedPrefData.saveUser(user);

                                    user.setLogedIn(true);
                                    newUser.setValue(user);
                                }else{

                                    //there is no phone number, so make a new user
                                    db.collection("users")
                                            .document(mCurrentUser.getUid())
                                            .set(cloudUser)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    User user = new User();
                                                    user.setId(mCurrentUser.getUid());
                                                    user.setName("Adınızı değiştirin");
                                                    user.setEmail("Email adresinizi değiştirin");
                                                    user.setCity("konumuzu değiştirin");
                                                    user.setImageUrl("default");
                                                    user.setDescription("açıklamayı değiştirin");
                                                    user.setPhoneNumber(phoneNumber);
                                                    sharedPrefData.saveUser(user);

                                                    user.setCreated(true);
                                                    newUser.setValue(user);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.v("Registration Error : ", e.getMessage());
                                            User user = new User();
                                            user.setCreated(false);
                                            newUser.setValue(user);
                                        }
                                    });
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

        return newUser;
    }
}
