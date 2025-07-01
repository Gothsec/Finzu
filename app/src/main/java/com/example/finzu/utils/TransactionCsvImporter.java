package com.example.finzu.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.finzu.database.FinzuDatabaseHelper;
import com.example.finzu.models.Transaction;
import com.example.finzu.repositories.TransactionRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransactionCsvImporter {

    private final Context context;

    public TransactionCsvImporter(Context context) {
        this.context = context;
    }

    public boolean importFromCsv(Uri fileUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            if (inputStream == null) return false;

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            boolean isFirstLine = true;

            TransactionRepository repository = new TransactionRepository(context);

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Manejar comas dentro de comillas
                String[] columns = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                if (columns.length < 5) continue;

                String type = columns[0].trim().replace("\"", "");
                double amount = Double.parseDouble(columns[1].trim());
                int accountId = Integer.parseInt(columns[2].trim());
                String details = columns[3].trim().replace("\"", "");
                String rawDate = columns[4].trim().replace("\"", "");
                String formattedDate = normalizeDate(rawDate);

                if (!accountExists(accountId)) {
                    Log.w("TransactionCsvImporter", "Cuenta no encontrada (ID " + accountId + "). Transacción ignorada.");
                    continue;
                }

                Transaction transaction = new Transaction(
                        0,
                        amount,
                        type,
                        accountId,
                        details,
                        formattedDate,
                        true
                );

                repository.insertTransaction(transaction);
            }

            reader.close();
            return true;

        } catch (Exception e) {
            Log.e("TransactionCsvImporter", "Error al importar CSV: ", e);
            return false;
        }
    }

    private boolean accountExists(int accountId) {
        Cursor cursor = FinzuDatabaseHelper
                .getInstance(context)
                .getReadableDatabase()
                .rawQuery("SELECT id FROM accounts WHERE id = ?", new String[]{String.valueOf(accountId)});

        boolean exists = (cursor != null && cursor.moveToFirst());
        if (cursor != null) cursor.close();
        return exists;
    }

    private String normalizeDate(String inputDate) {
        String[] formats = {
                "yyyy-MM-dd",
                "dd/MM/yyyy",
                "MM/dd/yyyy",
                "dd-MM-yyyy",
                "yyyy/MM/dd",
                "yyyy-MM-dd HH:mm:ss", // ya válida
                "dd/MM/yyyy HH:mm:ss",
                "MM/dd/yyyy HH:mm:ss"
        };

        for (String format : formats) {
            try {
                SimpleDateFormat parser = new SimpleDateFormat(format, Locale.getDefault());
                Date parsedDate = parser.parse(inputDate);
                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                return output.format(parsedDate);
            } catch (ParseException ignored) {}
        }

        Log.w("TransactionCsvImporter", "Formato de fecha no reconocido: " + inputDate);
        return inputDate;
    }
}
