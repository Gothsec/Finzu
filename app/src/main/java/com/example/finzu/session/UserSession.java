package com.example.finzu.session;

import android.content.Context;

import com.example.finzu.models.User;

public class UserSession {
    private static UserSession instance;
    private static User user;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static User getUser() {
        return user;
    }

    public static void clearSession(Context context) {
        user = null;
    }
}
