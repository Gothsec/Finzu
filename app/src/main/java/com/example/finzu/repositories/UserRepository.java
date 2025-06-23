package com.example.finzu.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.finzu.database.FinzuDatabaseHelper;
import com.example.finzu.models.User;

public class UserRepository {
    private final SQLiteDatabase db;

    public UserRepository(Context context) {
        this.db = FinzuDatabaseHelper.getInstance(context).getWritableDatabase();
    }

    public User authenticate(String email, String password) {
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            if (password.equals(storedPassword)) {
                User user = createUserFromCursor(cursor);
                cursor.close();
                return user;
            }
        }
        cursor.close();
        return null;
    }

    public boolean isEmailRegistered(String email) {
        Cursor cursor = db.rawQuery("SELECT email FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean registerUser(String name, String email, String password) {
        ContentValues values = new ContentValues();
        values.put("full_name", name);
        values.put("email", email);
        values.put("password", password);
        values.put("profile_pic_url", "");
        values.put("currency", "usd");
        values.put("reminder_hour", "off");
        values.put("reminder", "off");
        values.put("balance", 0);

        long result = db.insert("users", null, values);
        return result != -1;
    }

    public User getUserByEmail(String email) {
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND active = 1", new String[]{email});
        if (cursor.moveToFirst()) {
            User user = createUserFromCursor(cursor);
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    public void updateBalance(String email, double newBalance) {
        ContentValues values = new ContentValues();
        values.put("balance", newBalance);
        db.update("users", values, "email = ?", new String[]{email});
    }

    private User createUserFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        String fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
        String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
        String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
        String profilePic = cursor.getString(cursor.getColumnIndexOrThrow("profile_pic_url"));
        String currency = cursor.getString(cursor.getColumnIndexOrThrow("currency"));
        String reminderHour = cursor.getString(cursor.getColumnIndexOrThrow("reminder_hour"));
        boolean reminder = "on".equals(cursor.getString(cursor.getColumnIndexOrThrow("reminder")));
        double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"));

        return new User(id, fullName, email, password, profilePic, currency, reminderHour, reminder, balance, true);
    }

    public void updateUserName(String email, String newName) {
        ContentValues values = new ContentValues();
        values.put("full_name", newName);
        db.update("users", values, "email = ?", new String[]{email});
    }

    public void updateUserPassword(String email, String newPassword) {
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        db.update("users", values, "email = ?", new String[]{email});
    }

    public void updateUserCurrency(String email, String newCurrency) {
        ContentValues values = new ContentValues();
        values.put("currency", newCurrency);
        db.update("users", values, "email = ?", new String[]{email});
    }

    public void updateNotificationSettings(String email, boolean enabled, String time) {
        ContentValues values = new ContentValues();
        values.put("reminder", enabled ? "on" : "off");
        values.put("reminder_hour", time);
        db.update("users", values, "email = ?", new String[]{email});
    }
    public void softDeleteUser(String email) {
        ContentValues values = new ContentValues();
        values.put("active", 0);
        db.update("users", values, "email = ?", new String[]{email});
    }

    public void close() {
        if (db.isOpen()) {
            db.close();
        }
    }
}