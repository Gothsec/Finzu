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
import java.util.Locale;

public class TransactionCsvImporter {

    private final Context context;

    public TransactionCsvImporter(Context context) {
        this.context = context;
    }

    public boolean importFromCsv(Uri fileUri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            boolean isFirstLine = true;

            TransactionRepository repository = new TransactionRepository(context);

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // saltar encabezado
                }

                try {
                    String[] columns = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                    if (columns.length < 5) {
                        Log.w("TransactionCsvImporter", "Línea inválida, columnas insuficientes: " + line);
                        continue;
                    }

                    String type = clean(columns[0]);
                    double amount = Double.parseDouble(columns[1].trim());
                    int accountId = Integer.parseInt(columns[2].trim());
                    String details = clean(columns[3]);
                    String rawDate = clean(columns[4]);
                    String formattedDate = normalizeDate(rawDate);

                    if (!accountExists(accountId)) {
                        Log.w("TransactionCsvImporter", "Cuenta no encontrada: ID " + accountId + ". Ignorada.");
                        continue;
                    }

                    Transaction transaction = new Transaction(
                            0, amount, type, accountId, details, formattedDate, true
                    );
                    repository.insertTransaction(transaction);

                } catch (Exception ex) {
                    Log.e("TransactionCsvImporter", "Error al procesar línea: " + line, ex);
                    // continuar con el siguiente registro
                }
            }

            return true;

        } catch (Exception e) {
            Log.e("TransactionCsvImporter", "Error general al importar CSV", e);
            return false;
        }
    }

    private String clean(String value) {
        return value.trim().replaceAll("^\"|\"$", "");
    }

    private boolean accountExists(int accountId) {
        Cursor cursor = null;
        try {
            cursor = FinzuDatabaseHelper
                    .getInstance(context)
                    .getReadableDatabase()
                    .rawQuery("SELECT id FROM accounts WHERE id = ?", new String[]{String.valueOf(accountId)});

            return (cursor != null && cursor.moveToFirst());

        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private String normalizeDate(String inputDate) {
        String[] formats = {
                "yyyy-MM-dd",
                "dd/MM/yyyy",
                "MM/dd/yyyy",
                "dd-MM-yyyy",
                "yyyy/MM/dd"
        };

        for (String format : formats) {
            try {
                SimpleDateFormat parser = new SimpleDateFormat(format, Locale.getDefault());
                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                return output.format(parser.parse(inputDate));
            } catch (ParseException ignored) {}
        }

        return inputDate; // fallback si no se pudo formatear
    }
}
