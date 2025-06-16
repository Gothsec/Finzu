package com.example.finzu.session;

import com.example.finzu.models.User;

public class UserSession {
    private static UserSession instance;
    private User user;

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

    public User getUser() {
        return user;
    }

    public void clearSession() {
        user = null;
    }
}
