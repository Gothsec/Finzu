package com.example.finzu.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.finzu.models.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountRepository {
    private final SQLiteDatabase db;

    public AccountRepository(Context context) {
        this.db = UserSQLiteOpenHelper.getInstance(context).getWritableDatabase();
    }

    public long insertAccount(String userEmail, Account account) {
        ContentValues values = new ContentValues();
        values.put("user_email", userEmail);
        values.put("name", account.getName());
        values.put("amount", account.getAmount());
        values.put("type", account.getType());

        return db.insert("accounts", null, values);
    }

    public List<Account> getAccountsByUserEmail(String userEmail) {
        List<Account> accounts = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM accounts WHERE user_email = ?", new String[]{userEmail});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));

                accounts.add(new Account(id, name, amount, type));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accounts;
    }

    public void close() {
        db.close();
    }
}
