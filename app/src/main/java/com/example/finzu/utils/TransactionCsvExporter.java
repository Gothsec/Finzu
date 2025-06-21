package com.example.finzu.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.example.finzu.models.Transaction;
import com.example.finzu.repositories.TransactionRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TransactionCsvExporter {

    private final Context context;

    public TransactionCsvExporter(Context context) {
        this.context = context;
    }

    public boolean exportTransactionsToCsv(int accountId) {
        TransactionRepository repository = new TransactionRepository(context);
        List<Transaction> transactions = repository.getTransactionsByAccountId(accountId);

        if (transactions.isEmpty()) {
            Toast.makeText(context, "No hay transacciones para exportar", Toast.LENGTH_SHORT).show();
            return false;
        }

        String fileName = "transacciones_" + accountId + ".csv";

        File exportDir = new File(context.getExternalFilesDir(null), "exports");
        if (!exportDir.exists()) exportDir.mkdirs();

        File file = new File(exportDir, fileName);

        try {
            FileWriter writer = new FileWriter(file);

            // Headers
            writer.append("id,amount,type,account_id,details,date\n");

            for (Transaction t : transactions) {
                writer.append(t.getId() + ",")
                        .append(String.valueOf(t.getAmount())).append(",")
                        .append(t.getType()).append(",")
                        .append(String.valueOf(t.getAccountId())).append(",")
                        .append("\"").append(t.getDetails()).append("\"").append(",")
                        .append("\"").append(t.getDate()).append("\"")
                        .append("\n");
            }

            writer.flush();
            writer.close();

            Toast.makeText(context, "Exportado a: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al exportar CSV", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
