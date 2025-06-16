package com.example.finzu.utils;

public class ValidationUtils {

    public static boolean isEmailValid(String email) {
        return email != null && !email.trim().isEmpty() && email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        return password != null && !password.trim().isEmpty() && password.length() >= 6;
    }

    public static boolean areLoginFieldsValid(String email, String password) {
        return isEmailValid(email) && isPasswordValid(password);
    }
}
