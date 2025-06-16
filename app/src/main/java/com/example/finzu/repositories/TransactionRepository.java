package com.example.finzu.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.finzu.database.FinzuDatabaseHelper;
import com.example.finzu.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {

    private final SQLiteDatabase db;

    public TransactionRepository(Context context) {
        db = FinzuDatabaseHelper.getInstance(context).getWritableDatabase();
    }

    public long insertTransaction(Transaction transaction) {
        ContentValues values = new ContentValues();
        values.put("type", transaction.getType());
        values.put("amount", transaction.getAmount());
        values.put("account_id", transaction.getAccountId());
        values.put("details", transaction.getDetails());
        values.put("date", transaction.getDate());

        long result = db.insert("transactions", null, values);

        // Ajustar el balance de la cuenta relacionada
        if (result != -1) {
            adjustAccountAmount(transaction.getAccountId(), transaction.getAmount(), transaction.getType());
        }

        return result;
    }

    private void adjustAccountAmount(int accountId, double amount, String type) {
        Cursor cursor = db.rawQuery("SELECT amount FROM accounts WHERE id = ?", new String[]{String.valueOf(accountId)});
        if (cursor.moveToFirst()) {
            double currentAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
            double newAmount = type.equals("income") ? currentAmount + amount : currentAmount - amount;

            ContentValues values = new ContentValues();
            values.put("amount", newAmount);
            db.update("accounts", values, "id = ?", new String[]{String.valueOf(accountId)});
        }
        cursor.close();
    }

    public List<Transaction> getTransactionsByAccountId(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM transactions WHERE account_id = ? ORDER BY date DESC",
                new String[]{String.valueOf(accountId)}
        );

        if (cursor.moveToFirst()) {
            do {
                Transaction t = new Transaction(
                                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                        cursor.getString(cursor.getColumnIndexOrThrow("type")),
                                        cursor.getInt(cursor.getColumnIndexOrThrow("account_id")),
                                        cursor.getString(cursor.getColumnIndexOrThrow("details")),
                                        cursor.getString(cursor.getColumnIndexOrThrow("date"))
                                );
                transactions.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transactions;
    }

    public List<Transaction> getTransactionsByFilters(int accountId, String monthYear, String type) {
        List<Transaction> transactions = new ArrayList<>();

        // Ya tienes db inicializado, úsalo directamente
        String sql = "SELECT * FROM transactions WHERE account_id = ? AND type = ? AND strftime('%m-%Y', date) = ?";
        String[] selectionArgs = {
                String.valueOf(accountId),
                type,
                monthYear  // Por ejemplo: "06-2025"
        };

        Cursor cursor = db.rawQuery(sql, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String tType = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                int aId = cursor.getInt(cursor.getColumnIndexOrThrow("account_id"));
                String details = cursor.getString(cursor.getColumnIndexOrThrow("details"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                Transaction transaction = new Transaction(id, amount, tType, aId, details, date);
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return transactions;
    }


    public void deleteTransaction(int id) {
        db.delete("transactions", "id = ?", new String[]{String.valueOf(id)});
    }

    public void deleteAllForAccount(int accountId) {
        db.delete("transactions", "account_id = ?", new String[]{String.valueOf(accountId)});
    }

    public void close() {
        if (db.isOpen()) {
            db.close();
        }
    }
}
