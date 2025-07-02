package com.example.finzu.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.finzu.database.FinzuDatabaseHelper;
import com.example.finzu.models.Account;
import com.example.finzu.models.Transaction;
import com.example.finzu.repositories.AccountRepository;
import com.example.finzu.repositories.TransactionRepository;
import com.example.finzu.session.UserSession;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
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

            TransactionRepository transactionRepo = new TransactionRepository(context);
            AccountRepository accountRepo = new AccountRepository(context);

            String userEmail = UserSession.getInstance().getUser().getEmail();
            List<Account> userAccounts = accountRepo.getAccountsByUserEmail(userEmail);

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Saltar encabezado
                }

                try {
                    String[] columns = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                    if (columns.length < 5) {
                        Log.w("TransactionCsvImporter", "Línea inválida, columnas insuficientes: " + line);
                        continue;
                    }

                    String rawType = clean(columns[0]);
                    String type = normalizeType(rawType);
                    if (type == null) {
                        Log.w("TransactionCsvImporter", "Tipo inválido: " + rawType);
                        continue;
                    }

                    double amount = Double.parseDouble(columns[1].trim());
                    int csvAccountId = Integer.parseInt(columns[2].trim());
                    String details = clean(columns[3]);
                    String rawDate = clean(columns[4]);
                    String formattedDate = normalizeDate(rawDate);

                    // Verifica si la cuenta pertenece al usuario actual
                    boolean belongsToUser = false;
                    for (Account account : userAccounts) {
                        if (account.getId() == csvAccountId) {
                            belongsToUser = true;
                            break;
                        }
                    }

                    if (!belongsToUser) {
                        Log.w("TransactionCsvImporter", "Cuenta no pertenece al usuario: ID " + csvAccountId);
                        continue;
                    }

                    Transaction transaction = new Transaction(
                            0, amount, type, csvAccountId, details, formattedDate, true
                    );
                    transactionRepo.insertTransaction(transaction);

                } catch (Exception ex) {
                    Log.e("TransactionCsvImporter", "Error al procesar línea: " + line, ex);
                    // Continuar con el siguiente registro
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

    private String normalizeType(String raw) {
        String normalized = raw.trim().toLowerCase();
        if (normalized.contains("ingreso") || normalized.contains("entrada")) return "Ingreso";
        if (normalized.contains("gasto") || normalized.contains("salida")) return "Gasto";
        return null; // tipo desconocido
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
            } catch (ParseException ignored) {
            }
        }

        return inputDate; // fallback si no se pudo formatear
    }
}
