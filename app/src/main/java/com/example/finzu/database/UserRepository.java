package com.example.finzu.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.finzu.models.User;

public class UserRepository {
    private final SQLiteDatabase db;

    public UserRepository(Context context) {
        this.db = UserSQLiteOpenHelper.getInstance(context).getWritableDatabase();
    }

    public User authenticate(String email, String password) {
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            if (password.equals(storedPassword)) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                String profilePic = cursor.getString(cursor.getColumnIndexOrThrow("profile_pic_url"));
                String currency = cursor.getString(cursor.getColumnIndexOrThrow("currency"));
                String reminder = cursor.getString(cursor.getColumnIndexOrThrow("reminder_hour"));

                User user = new User(id, fullName, email, password, profilePic, currency, reminder);
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
        // Puedes dejar profile_pic_url, currency, reminder_hour como NULL si aún no se configuran

        long result = db.insert("users", null, values);
        return result != -1;
    }

    public void close() {
        if (db.isOpen()) {
            db.close();
        }
    }
}
