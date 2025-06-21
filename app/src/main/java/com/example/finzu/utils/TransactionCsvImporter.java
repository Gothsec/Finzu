package com.example.finzu.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.finzu.models.Transaction;
import com.example.finzu.repositories.TransactionRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
                // jump header
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] columns = line.split(",");

                if (columns.length < 6) continue; // invalid line

                double amount = Double.parseDouble(columns[1].trim());
                String type = columns[2].trim();
                int accountId = Integer.parseInt(columns[3].trim());
                String details = columns[4].trim().replace("\"", "");
                String date = columns[5].trim().replace("\"", "");

                Transaction transaction = new Transaction(
                        0, // autogenerate id
                        amount,
                        type,
                        accountId,
                        details,
                        date,
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
}
