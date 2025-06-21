package com.example.finzu.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.finzu.database.FinzuDatabaseHelper;
import com.example.finzu.models.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountRepository {
    private final SQLiteDatabase db;

    public AccountRepository(Context context) {
        this.db = FinzuDatabaseHelper.getInstance(context).getWritableDatabase();
    }

    public long insertAccount(String userEmail, Account account) {
        ContentValues values = new ContentValues();
        values.put("user_email", userEmail);
        values.put("name", account.getName());
        values.put("amount", account.getAmount());

        return db.insert("accounts", null, values);
    }

    public List<Account> getAccountsByUserEmail(String userEmail) {
        List<Account> accounts = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM accounts WHERE user_email = ? AND active = 1", new String[]{userEmail});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("user_email"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));

                accounts.add(new Account(id, email, name, amount));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accounts;
    }

    public void updateAccountAmount(int accountId, double newAmount) {
        ContentValues values = new ContentValues();
        values.put("amount", newAmount);
        db.update("accounts", values, "id = ?", new String[]{String.valueOf(accountId)});
    }

    public Account getAccountById(int accountId) {
        Cursor cursor = db.rawQuery("SELECT * FROM accounts WHERE id = ? AND active = 1", new String[]{String.valueOf(accountId)});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("user_email"));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
            cursor.close();
            return new Account(id, email, name, amount);
        }
        cursor.close();
        return null;
    }

    public void updateAccountName(int accountId, String newName) {
        ContentValues values = new ContentValues();
        values.put("name", newName);
        db.update("accounts", values, "id = ?", new String[]{String.valueOf(accountId)});
    }

    public void deleteAccount(int accountId) {
        ContentValues values = new ContentValues();
        values.put("active", 0);
        db.update("accounts", values, "id = ?", new String[]{String.valueOf(accountId)});
    }

    public void close() {
        if (db.isOpen()) {
            db.close();
        }
    }
}
