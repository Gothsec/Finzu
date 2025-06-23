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
            String cleanType = type.trim().toLowerCase();
            double newAmount = cleanType.equals("ingreso") ? currentAmount + amount : currentAmount - amount;

            ContentValues values = new ContentValues();
            values.put("amount", newAmount);
            db.update("accounts", values, "id = ?", new String[]{String.valueOf(accountId)});
        }
        cursor.close();
    }

    public List<Transaction> getTransactionsByAccountId(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM transactions WHERE account_id = ? AND active = 1 ORDER BY date DESC",
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
                                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        true
                                );
                transactions.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transactions;
    }

    public List<Transaction> getTransactionsByFilters(int accountId, String monthYear, String type) {
        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT * FROM transactions WHERE account_id = ? AND type = ? AND strftime('%m-%Y', date) = ? AND active = 1";
        String[] selectionArgs = {
                String.valueOf(accountId),
                type,
                monthYear
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

                Transaction transaction = new Transaction(id, amount, tType, aId, details, date, true);
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return transactions;
    }

    public List<Transaction> getTransactionsByUserEmail(String email) {
        List<Transaction> transactions = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT t.id, t.amount, t.type, t.account_id, t.details, t.date, t.active " +
                        "FROM transactions t " +
                        "JOIN accounts a ON t.account_id = a.id " +
                        "WHERE a.user_email = ?",
                new String[]{email}
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                double amount = cursor.getDouble(1);
                String type = cursor.getString(2);
                int accountId = cursor.getInt(3);
                String details = cursor.getString(4);
                String date = cursor.getString(5);
                boolean active = cursor.getInt(6) == 1;

                transactions.add(new Transaction(id, amount, type, accountId, details, date, active));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return transactions;
    }


    public String getUserEmailByTransactionId(int transactionId) {
        String email = null;

        String sql = "SELECT a.user_email " +
                "FROM transactions t " +
                "JOIN accounts a ON t.account_id = a.id " +
                "WHERE t.id = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(transactionId)});
        if (cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndexOrThrow("user_email"));
        }
        cursor.close();
        return email;
    }

    private void revertAccountAmount(int accountId, double amount, String type) {
        Cursor cursor = db.rawQuery("SELECT amount FROM accounts WHERE id = ?", new String[]{String.valueOf(accountId)});
        if (cursor.moveToFirst()) {
            double currentAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
            double newAmount = type.equals("ingreso") ? currentAmount - amount : currentAmount + amount;

            ContentValues values = new ContentValues();
            values.put("amount", newAmount);
            db.update("accounts", values, "id = ?", new String[]{String.valueOf(accountId)});
        }
        cursor.close();
    }

    private void revertUserAmount(String userEmail, double amount, String type) {
        Cursor cursor = db.rawQuery("SELECT balance FROM users WHERE email = ?", new String[]{userEmail});
        if (cursor.moveToFirst()) {
            double currentTotal = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"));
            double newTotal = type.equals("ingreso") ? currentTotal - amount : currentTotal + amount;

            ContentValues values = new ContentValues();
            values.put("balance", newTotal);
            db.update("users", values, "email = ?", new String[]{userEmail});
        }
        cursor.close();
    }

    private void adjustUserBalance(String userEmail, double amount, String type) {
        Cursor cursor = db.rawQuery("SELECT balance FROM users WHERE email = ?", new String[]{userEmail});
        if (cursor.moveToFirst()) {
            double currentAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"));
            double newAmount = type.trim().equalsIgnoreCase("ingreso") ? currentAmount + amount : currentAmount - amount;

            ContentValues values = new ContentValues();
            values.put("balance", newAmount);
            db.update("users", values, "email = ?", new String[]{userEmail});
        }
        cursor.close();
    }

    private void revertUserBalance(String userEmail, double amount, String type) {
        adjustUserBalance(userEmail, amount, type.equalsIgnoreCase("ingreso") ? "gasto" : "ingreso");
    }


    public void updateTransaction(Transaction transaction) {
        // 1. get old transaction
        Cursor oldCursor = db.rawQuery("SELECT amount, type, account_id FROM transactions WHERE id = ?", new String[]{String.valueOf(transaction.getId())});
        if (oldCursor.moveToFirst()) {
            double oldAmount = oldCursor.getDouble(oldCursor.getColumnIndexOrThrow("amount"));
            String oldType = oldCursor.getString(oldCursor.getColumnIndexOrThrow("type")).trim().toLowerCase();
            int oldAccountId = oldCursor.getInt(oldCursor.getColumnIndexOrThrow("account_id"));

            // 2. get user eamil
            String userEmail = null;
            Cursor accCursor = db.rawQuery("SELECT user_email FROM accounts WHERE id = ?", new String[]{String.valueOf(oldAccountId)});
            if (accCursor.moveToFirst()) {
                userEmail = accCursor.getString(accCursor.getColumnIndexOrThrow("user_email"));
            }
            accCursor.close();

            if (userEmail != null) {
                revertAccountAmount(oldAccountId, oldAmount, oldType);
                revertUserAmount(userEmail, oldAmount, oldType);

                // 3. apply to new transaction
                adjustAccountAmount(transaction.getAccountId(), transaction.getAmount(), transaction.getType());
                adjustUserBalance(userEmail, transaction.getAmount(), transaction.getType());
            }
        }
        oldCursor.close();

        // 5. update transaction on database
        ContentValues values = new ContentValues();
        values.put("amount", transaction.getAmount());
        values.put("type", transaction.getType());
        values.put("account_id", transaction.getAccountId());
        values.put("details", transaction.getDetails());
        values.put("date", transaction.getDate());

        db.update("transactions", values, "id = ?", new String[]{String.valueOf(transaction.getId())});
    }

    public void deleteTransaction(int id) {
        // 1. get the transaction
        Cursor cursor = db.rawQuery("SELECT amount, type, account_id FROM transactions WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type")).trim().toLowerCase();
            int accountId = cursor.getInt(cursor.getColumnIndexOrThrow("account_id"));

            // 2. delete the money from the account
            revertAccountAmount(accountId, amount, type);

            // 3. delete the transaction from users balance
            Cursor accountCursor = db.rawQuery("SELECT user_email FROM accounts WHERE id = ?", new String[]{String.valueOf(accountId)});
            if (accountCursor.moveToFirst()) {
                String userEmail = accountCursor.getString(accountCursor.getColumnIndexOrThrow("user_email"));
                revertUserAmount(userEmail, amount, type);
            }
            accountCursor.close();
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("active", 0);
        db.update("transactions", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public void close() {
        if (db.isOpen()) {
            db.close();
        }
    }
}
