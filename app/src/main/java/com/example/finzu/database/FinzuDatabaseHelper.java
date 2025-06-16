package com.example.finzu.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FinzuDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "finzu.db";
    private static final int DB_VERSION = 1;

    private static FinzuDatabaseHelper instance;

    public static synchronized FinzuDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new FinzuDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public FinzuDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabla de usuarios
        String createUserTable = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "profile_pic_url TEXT DEFAULT '', " +
                "currency TEXT DEFAULT 'usd', " +
                "reminder_hour TEXT DEFAULT '', " +
                "reminder TEXT DEFAULT 'off', " +
                "balance REAL DEFAULT 0" +
                ")";
        db.execSQL(createUserTable);

        // Tabla de cuentas
        String createAccountTable = "CREATE TABLE accounts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_email TEXT, " +
                "name TEXT, " +
                "amount REAL DEFAULT 0, " +
                "FOREIGN KEY(user_email) REFERENCES users(email) ON DELETE CASCADE" +
                ")";
        db.execSQL(createAccountTable);

        // Tabla de transacciones
        String createTransactionTable = "CREATE TABLE transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type TEXT, " +               // income / outcome
                "amount REAL, " +
                "account_id INTEGER, " +
                "details TEXT, " +
                "date TEXT, " +
                "FOREIGN KEY(account_id) REFERENCES accounts(id) ON DELETE CASCADE" +
                ")";
        db.execSQL(createTransactionTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Por ahora sin implementación de migraciones
    }
}
