package com.example.sw0b_001.Models.User;

import android.content.Context;
import android.content.SharedPreferences;

public class UserHandler {

    private String userId;
    private static String sharedPreferenceKey = "com.example.swob.USER_INFORMATION";
    private Context context;

    public UserHandler(){ }

    public UserHandler(Context context){
        this.context = context;
    }

    public UserHandler(Context context, String userId){
        this.context = context;
        this.userId = userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void commitUser() {
        /*
        https://developer.android.com/training/data-storage/shared-preferences
        https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences
         */

        SharedPreferences sharedPreferences = this.context.getSharedPreferences(this.sharedPreferenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString("user_id", this.userId);
        sharedPreferencesEditor.apply();
    }

    public User getUser() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(this.sharedPreferenceKey, Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "");

        return new User(userId);
    }
}
