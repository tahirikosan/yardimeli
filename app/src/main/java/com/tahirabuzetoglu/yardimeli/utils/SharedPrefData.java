package com.tahirabuzetoglu.yardimeli.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.tahirabuzetoglu.yardimeli.data.entity.User;


public class SharedPrefData {
    public static final String DATA = "DATA";
    public static final String USER = "USER";

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public SharedPrefData(Context context) {
        this.context = context;

        sharedPreferences = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Saves user in db
    public void saveUser(User user){

        // convert user to json string
        String userJson = userToJson(user);

        editor.putString(USER, userJson);
        editor.commit();
    }


    // Loads user
    public User loadUser(){
        String userJson = sharedPreferences.getString(USER, "");

        //convert json to user object
        User user = jsonToUser(userJson);
        return user;
    }

    // Convert user Object to json string
    public String userToJson(User user){
        Gson gson = new Gson();
        String json = gson.toJson(user);
        return json;
    }

    // Convert json string to User object;
    public User jsonToUser(String json){
        Gson gson = new Gson();
        User user = gson.fromJson(json, User.class);

        return user;
    }
}
