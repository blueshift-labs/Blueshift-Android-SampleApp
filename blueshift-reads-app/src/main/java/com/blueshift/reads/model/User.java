package com.blueshift.reads.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

/**
 * @author Rahul Raveendran V P
 *         Created on 6/10/16 @ 6:09 PM
 *         https://github.com/rahulrvp
 */


public class User {
    private static final String PREF_FILE = "UserFile";
    private static final String PREF_KEY = "UserKey";
    private static final String LOG_TAG = "User";

    private static User sInstance = null;

    private String email;

    private User() {
    }

    public static User getInstance(Context context) {
        if (sInstance == null) {
            sInstance = loadFromPrefStore(context);
        }

        if (sInstance == null) {
            sInstance = new User();
        }

        return sInstance;
    }

    private static User loadFromPrefStore(Context context) {
        User user = null;

        if (context != null) {
            String json = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).getString(PREF_KEY, null);
            if (!TextUtils.isEmpty(json)) {
                try {
                    user = new Gson().fromJson(json, User.class);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        }

        return user;
    }

    public void save(Context context) {
        if (context != null) {
            String json = new Gson().toJson(this);

            context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
                    .edit()
                    .putString(PREF_KEY, json)
                    .apply();
        }
    }

    public void logOut(Context context) {
        if (context != null) {
            // delete email
            this.email = null;

            // delete cached user
            context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
                    .edit()
                    .remove(PREF_KEY)
                    .apply();

            // make current instance null. to load new instance next time.
            sInstance = null;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSignedIn() {
        return !TextUtils.isEmpty(email);
    }
}
