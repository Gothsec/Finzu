package com.example.finzu.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "users.db";
    private static final int DB_VERSION = 1;

    private static UserSQLiteOpenHelper instance;

    public static synchronized UserSQLiteOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new UserSQLiteOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    public UserSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // User table
        String createUserTable = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "profile_pic_url TEXT DEFAULT '', " +
                "currency TEXT DEFAULT 'usd', " +
                "reminder_hour TEXT DEFAULT 'off')";
        db.execSQL(createUserTable);


        // Account table
        String createAccountTable = "CREATE TABLE accounts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_email TEXT, " +
                "name TEXT, " +
                "amount REAL DEFAULT 0, " +
                "type TEXT)";
        db.execSQL(createAccountTable);

        // Transaction table
        String createTransactionTable = "CREATE TABLE transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type TEXT, " +
                "amount REAL, " +
                "account_id INTEGER, " +
                "details TEXT, " +
                "date TEXT, " +
                "FOREIGN KEY(account_id) REFERENCES accounts(id) ON DELETE CASCADE)";
        db.execSQL(createTransactionTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
