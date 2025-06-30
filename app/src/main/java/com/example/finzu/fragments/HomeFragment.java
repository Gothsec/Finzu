package com.example.finzu.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finzu.R;
import com.example.finzu.adapters.TransactionAdapter;
import com.example.finzu.models.Account;
import com.example.finzu.models.Transaction;
import com.example.finzu.repositories.AccountRepository;
import com.example.finzu.repositories.TransactionRepository;
import com.example.finzu.session.UserSession;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment implements TransactionAdapter.OnTransactionClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_CODE_IMPORT_CSV = 1002;
    private Uri photoUri;

    private TextView tvBalanceValue;
    private TextView tvIngresosMes;
    private TextView tvGastosMes;
    private RecyclerView recyclerTransactions;
    private TransactionAdapter transactionAdapter;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        CardView btnAccounts = view.findViewById(R.id.btn_accounts);
        btnAccounts.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container, new AccountsFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvBalanceValue = view.findViewById(R.id.balance_value);
        tvIngresosMes = view.findViewById(R.id.income_value);
        tvGastosMes = view.findViewById(R.id.expense_value);

        recyclerTransactions = view.findViewById(R.id.recycler_transactions);
        recyclerTransactions.setLayoutManager(new LinearLayoutManager(getContext()));

        updateBalance();
        updateMonthlySummary();
        loadTransactions();

        FloatingActionButton fab = requireActivity().findViewById(R.id.fab_add);
        if (fab != null) fab.show();

        LinearLayout fabMenu = view.findViewById(R.id.fab_menu);
        View btnImport = view.findViewById(R.id.btn_import);
        View btnScan = view.findViewById(R.id.btn_scan);
        View btnNew = view.findViewById(R.id.btn_new);

        final boolean[] isMenuOpen = {false};

        fab.setOnClickListener(v -> {
            if (isMenuOpen[0]) {
                fabMenu.setVisibility(View.GONE);
                fab.setImageResource(R.drawable.ic_add);
            } else {
                fabMenu.setVisibility(View.VISIBLE);
                fab.setImageResource(R.drawable.ic_close);
            }
            isMenuOpen[0] = !isMenuOpen[0];
        });

        btnImport.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("text/*");  // o "text/csv"
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQUEST_CODE_IMPORT_CSV);
        });


        btnScan.setOnClickListener(v -> {
            checkCameraPermissionAndOpenCamera();
        });

        btnNew.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container, new TransactionFormFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        SharedPreferences prefs = requireContext().getSharedPreferences("finzu_prefs", Context.MODE_PRIVATE);
        boolean tutorialShown = prefs.getBoolean("tutorial_home_shown", false);

        if (!tutorialShown) {
            new Handler().postDelayed(() -> {
                FloatingActionButton fab_tutorial = requireActivity().findViewById(R.id.fab_add);
                LinearLayout linear_menu_tutorial = view.findViewById(R.id.fab_menu);

                View title_tutorial = view.findViewById(R.id.title);
                View btn_import_tutorial = requireActivity().findViewById(R.id.btn_import);
                View btn_scan_tutorial = requireActivity().findViewById(R.id.btn_scan);
                View btn_new_tutorial = requireActivity().findViewById(R.id.btn_new);
                View btn_accounts_tutorial = view.findViewById(R.id.btn_accounts);

                linear_menu_tutorial.setVisibility(View.VISIBLE);

                TapTarget firstTarget = TapTarget.forView(
                                title_tutorial,
                                "¡Bienvenido!",
                                "Te daremos un tour por la app para ayudarte a navegar mejor y mejorar tu experiencia de uso."
                        )
                        .id(1)
                        .outerCircleColor(R.color.green)
                        .targetCircleColor(R.color.green)
                        .titleTextColor(android.R.color.white)
                        .descriptionTextColor(android.R.color.white)
                        .drawShadow(false)
                        .cancelable(true)
                        .tintTarget(false);

                TapTargetView.showFor(requireActivity(), firstTarget, new TapTargetView.Listener() {
                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                        TapTargetSequence sequence = new TapTargetSequence(requireActivity())
                                .targets(
                                        TapTarget.forView(fab_tutorial, "Botón agregar", "Toca aquí para desplegar múltiples opciones")
                                                .outerCircleColor(R.color.green)
                                                .targetCircleColor(android.R.color.transparent)
                                                .titleTextColor(android.R.color.white)
                                                .descriptionTextColor(android.R.color.white)
                                                .drawShadow(false)
                                                .cancelable(false)
                                                .tintTarget(false),

                                        TapTarget.forView(btn_import_tutorial, "Importar datos", "Aquí puedes importar datos desde un archivo CSV o Excel")
                                                .outerCircleColor(R.color.green)
                                                .targetCircleColor(R.color.green)
                                                .titleTextColor(android.R.color.white)
                                                .descriptionTextColor(android.R.color.white)
                                                .drawShadow(false)
                                                .cancelable(false)
                                                .tintTarget(false),

                                        TapTarget.forView(btn_scan_tutorial, "Escanear factura", "Desde aquí escaneas una factura para añadir un nuevo registro")
                                                .outerCircleColor(R.color.green)
                                                .targetCircleColor(R.color.green)
                                                .titleTextColor(android.R.color.white)
                                                .descriptionTextColor(android.R.color.white)
                                                .drawShadow(false)
                                                .cancelable(false)
                                                .tintTarget(false),

                                        TapTarget.forView(btn_new_tutorial, "Nuevo registro", "Aquí puedes añadir un nuevo ingreso o gasto")
                                                .outerCircleColor(R.color.green)
                                                .targetCircleColor(R.color.green)
                                                .titleTextColor(android.R.color.white)
                                                .descriptionTextColor(android.R.color.white)
                                                .drawShadow(false)
                                                .cancelable(false)
                                                .tintTarget(false),

                                        TapTarget.forView(btn_accounts_tutorial, "Cuentas", "Aquí puedes crear y administrar tus cuentas financieras. Echemos un vistazo")
                                                .outerCircleColor(R.color.green)
                                                .targetCircleColor(R.color.green)
                                                .titleTextColor(android.R.color.white)
                                                .descriptionTextColor(android.R.color.white)
                                                .drawShadow(false)
                                                .cancelable(false)
                                                .tintTarget(false)
                                )
                                .listener(new TapTargetSequence.Listener() {
                                    @Override
                                    public void onSequenceFinish() {
                                        prefs.edit().putBoolean("tutorial_home_shown", true).apply();
                                    }

                                    @Override
                                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {}

                                    @Override
                                    public void onSequenceCanceled(TapTarget lastTarget) {}
                                });

                        sequence.start();
                    }
                });

            }, 500);
        }
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            Log.d("HomeFragment", "Cámara disponible, creando archivo...");

            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d("HomeFragment", "Archivo creado: " + photoFile.getAbsolutePath());
            } catch (IOException ex) {
                Log.e("HomeFragment", "Error creando archivo", ex);
                Toast.makeText(requireContext(), "Error al crear archivo de imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            if (photoFile != null) {
                try {
                    photoUri = FileProvider.getUriForFile(
                            requireContext(),
                            requireContext().getPackageName() + ".provider",
                            photoFile
                    );
                    Log.d("HomeFragment", "URI creado: " + photoUri.toString());

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (Exception e) {
                    Log.e("HomeFragment", "Error con FileProvider", e);
                    Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(requireContext(), "No hay aplicación de cámara disponible", Toast.LENGTH_SHORT).show();
        }
    }

    private void importCsvFromUri(Uri uri) {
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                if (lineNumber == 0) {
                    // Saltar encabezado si es necesario
                    lineNumber++;
                    continue;
                }

                // Separar por coma
                String[] tokens = line.split(",");

                if (tokens.length >= 4) {
                    String type = tokens[0].trim(); // ingreso o gasto
                    double amount = Double.parseDouble(tokens[1].trim());
                    String date = tokens[2].trim(); // Formato: YYYY-MM-DD
                    String details = tokens[3].trim();

                    String email = UserSession.getInstance().getUser().getEmail();
                    AccountRepository accountRepo = new AccountRepository(requireContext());
                    List<Account> userAccounts = accountRepo.getAccountsByUserEmail(email);

                    if (!userAccounts.isEmpty()) {
                        int accountId = userAccounts.get(0).getId();

                        Transaction tx = new Transaction(0, amount, type, accountId, details, date, true);
                        TransactionRepository txRepo = new TransactionRepository(requireContext());
                        txRepo.insertTransaction(tx);
                    }
                }
                lineNumber++;
            }

            Toast.makeText(getContext(), "Importación exitosa", Toast.LENGTH_SHORT).show();
            loadTransactions(); // Recargar lista
            updateBalance();    // Actualizar resumen
            updateMonthlySummary();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al importar CSV", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), "Permiso de cámara requerido para escanear facturas",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);

        return imageFile;
    }

    private String extractAmount(String ocrText) {
        List<Double> amounts = new ArrayList<>();

        String[] patterns = {
                "\\$\\s*([0-9]{1,3}(?:[.,][0-9]{3})*(?:[.,][0-9]{2})?)",
                "([0-9]{1,3}(?:[.,][0-9]{3})*(?:[.,][0-9]{2})?)\\s*\\$",
                "total:?\\s*\\$?\\s*([0-9]{1,3}(?:[.,][0-9]{3})*(?:[.,][0-9]{2})?)",
                "monto:?\\s*\\$?\\s*([0-9]{1,3}(?:[.,][0-9]{3})*(?:[.,][0-9]{2})?)",
                "valor:?\\s*\\$?\\s*([0-9]{1,3}(?:[.,][0-9]{3})*(?:[.,][0-9]{2})?)",
                "([0-9]{1,3}(?:[.,][0-9]{3})*(?:[.,][0-9]{2})?)"
        };

        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(ocrText);

            while (matcher.find()) {
                String amountStr = matcher.group(1) != null ? matcher.group(1) : matcher.group(0);
                amountStr = amountStr.replace("$", "").trim();

                try {
                    String normalizedAmount = normalizeAmount(amountStr);
                    double amount = Double.parseDouble(normalizedAmount);

                    if (amount >= 0.01 && amount <= 1000000) {
                        amounts.add(amount);
                    }
                } catch (NumberFormatException e) {
                    // Continuar con el siguiente match
                }
            }
        }

        if (amounts.isEmpty()) {
            return "";
        }

        Double selectedAmount = findMostRelevantAmount(ocrText, amounts);

        return selectedAmount != null ? String.format("%.2f", selectedAmount) : "";
    }

    private String normalizeAmount(String amountStr) {

        if (amountStr.contains(",") && amountStr.contains(".")) {
            int lastCommaIndex = amountStr.lastIndexOf(",");
            int lastDotIndex = amountStr.lastIndexOf(".");

            if (lastDotIndex > lastCommaIndex) {
                return amountStr.replace(",", "");
            } else {
                return amountStr.replace(".", "").replace(",", ".");
            }
        } else if (amountStr.contains(",")) {
            String[] parts = amountStr.split(",");
            if (parts.length == 2 && parts[1].length() <= 2) {
                return amountStr.replace(",", ".");
            } else {
                return amountStr.replace(",", "");
            }
        }

        return amountStr;
    }

    private Double findMostRelevantAmount(String ocrText, List<Double> amounts) {
        String lowerText = ocrText.toLowerCase();

        String[] keywords = {"total", "monto", "valor", "importe", "pagar", "cobrar", "suma"};

        for (String keyword : keywords) {
            Pattern keywordPattern = Pattern.compile(keyword + "\\s*:?\\s*\\$?\\s*([0-9,\\.]+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = keywordPattern.matcher(ocrText);

            while (matcher.find()) {
                String amountStr = matcher.group(1);
                try {
                    String normalizedAmount = normalizeAmount(amountStr);
                    double amount = Double.parseDouble(normalizedAmount);

                    if (amounts.contains(amount)) {
                        return amount;
                    }
                } catch (NumberFormatException e) {
                    // Continuar buscando
                }
            }
        }

        return amounts.stream().max(Double::compareTo).orElse(null);
    }

    private String extractDetails(String ocrText) {
        String lower = ocrText.toLowerCase();
        String[] lines = ocrText.split("\n");

        // Patrones específicos para diferentes tipos de transacciones
        if (lower.contains("retiro") || lower.contains("withdrawal")) return "Retiro ATM";
        if (lower.contains("recarga") || lower.contains("top up") || lower.contains("carga")) return "Recarga";
        if (lower.contains("transferencia") || lower.contains("transfer")) return "Transferencia";
        if (lower.contains("pago") && (lower.contains("tarjeta") || lower.contains("card"))) return "Pago con tarjeta";
        if (lower.contains("deposito") || lower.contains("deposit")) return "Depósito";

        // Tipos de establecimientos
        if (lower.contains("supermarket") || lower.contains("supermercado") || lower.contains("super")) return "Supermercado";
        if (lower.contains("restaurant") || lower.contains("restaurante")) return "Restaurante";
        if (lower.contains("farmacia") || lower.contains("pharmacy")) return "Farmacia";
        if (lower.contains("gasolina") || lower.contains("gas") || lower.contains("combustible")) return "Gasolina";
        if (lower.contains("banco") || lower.contains("bank")) return "Banco";
        if (lower.contains("cajero") || lower.contains("atm")) return "Cajero ATM";

        // Buscar nombre del establecimiento en las primeras líneas
        for (int i = 0; i < Math.min(3, lines.length); i++) {
            String line = lines[i].trim();
            if (line.length() > 3 && line.length() < 50 && !line.matches(".*\\d.*")) {
                if (!line.toLowerCase().contains("factura") &&
                        !line.toLowerCase().contains("ticket") &&
                        !line.toLowerCase().contains("recibo")) {
                    return line;
                }
            }
        }

        return "Transacción";
    }

    private boolean detectIfExpense(String ocrText) {
        String lower = ocrText.toLowerCase();

        // Indicadores claros de GASTO
        String[] expenseKeywords = {
                "retiro", "withdrawal", "pago", "compra", "purchase", "gasto", "expense",
                "factura", "invoice", "ticket", "recibo", "debito", "debit",
                "supermercado", "restaurante", "gasolina", "farmacia",
                "total a pagar", "monto a pagar", "cobrar"
        };

        // Indicadores claros de INGRESO
        String[] incomeKeywords = {
                "recarga", "top up", "carga", "deposito", "deposit", "ingreso", "income",
                "abono", "credit", "credito", "transferencia recibida", "pago recibido"
        };

        int expenseCount = 0;
        int incomeCount = 0;

        for (String keyword : expenseKeywords) {
            if (lower.contains(keyword)) {
                expenseCount++;
            }
        }

        for (String keyword : incomeKeywords) {
            if (lower.contains(keyword)) {
                incomeCount++;
            }
        }

        if (incomeCount > expenseCount) {
            return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (photoUri != null) {
                Log.d("HomeFragment", "Imagen capturada exitosamente: " + photoUri.toString());
                Toast.makeText(requireContext(), "Procesando factura escaneada...", Toast.LENGTH_SHORT).show();

                try {
                    InputImage image = InputImage.fromFilePath(requireContext(), photoUri);
                    TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

                    recognizer.process(image)
                            .addOnSuccessListener(visionText -> {
                                String ocrText = visionText.getText();
                                Log.d("OCR", "Texto reconocido:\n" + ocrText);

                                String amount = extractAmount(ocrText);
                                String details = extractDetails(ocrText);
                                boolean isExpense = detectIfExpense(ocrText);

                                TransactionFormFragment formFragment = new TransactionFormFragment();

                                Bundle args = new Bundle();
                                args.putString("ocr_amount", amount);
                                args.putString("ocr_details", details);
                                args.putBoolean("ocr_is_expense", isExpense);
                                formFragment.setArguments(args);

                                FragmentTransaction transaction = requireActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction();
                                transaction.replace(R.id.fragment_container, formFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();

                            })
                            .addOnFailureListener(e -> {
                                Log.e("OCR", "Error procesando la imagen", e);
                                Toast.makeText(requireContext(), "Error procesando la imagen", Toast.LENGTH_SHORT).show();
                            });

                } catch (IOException e) {
                    Log.e("OCR", "Error cargando la imagen", e);
                    Toast.makeText(requireContext(), "No se pudo leer la imagen", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(requireContext(), "Error al capturar imagen", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Toast.makeText(requireContext(), "Escaneo cancelado", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_CODE_IMPORT_CSV && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                importCsvFromUri(uri);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBalance();
        updateMonthlySummary();
        loadTransactions();
    }

    private void updateBalance() {
        if (UserSession.getInstance().getUser() == null) return;

        String email = UserSession.getInstance().getUser().getEmail();
        AccountRepository accountRepository = new AccountRepository(requireContext());
        List<Account> accounts = accountRepository.getAccountsByUserEmail(email);

        double total = 0;
        for (Account account : accounts) {
            total += account.getAmount();
        }

        tvBalanceValue.setText(String.format("$ %.2f", total));
    }

    private void updateMonthlySummary() {
        if (UserSession.getInstance().getUser() == null) return;

        String email = UserSession.getInstance().getUser().getEmail();
        AccountRepository accountRepo = new AccountRepository(requireContext());
        TransactionRepository transactionRepo = new TransactionRepository(requireContext());

        List<Account> userAccounts = accountRepo.getAccountsByUserEmail(email);
        double totalIngresos = 0;
        double totalGastos = 0;

        Calendar calendar = Calendar.getInstance();
        int mesActual = calendar.get(Calendar.MONTH) + 1;
        int anioActual = calendar.get(Calendar.YEAR);

        for (Account account : userAccounts) {
            List<Transaction> transactions = transactionRepo.getTransactionsByAccountId(account.getId());

            for (Transaction tx : transactions) {
                String[] partesFecha = tx.getDate().split("-");
                if (partesFecha.length >= 2) {
                    int anio = Integer.parseInt(partesFecha[0]);
                    int mes = Integer.parseInt(partesFecha[1]);

                    if (anio == anioActual && mes == mesActual) {
                        String tipo = tx.getType().trim().toLowerCase();
                        if (tipo.equals("ingreso")) totalIngresos += tx.getAmount();
                        else if (tipo.equals("gasto")) totalGastos += tx.getAmount();
                    }
                } else {
                    Log.e("FormatoFecha", "Fecha malformada: " + tx.getDate());
                }
            }
        }

        tvIngresosMes.setText(String.format("$ %.2f", totalIngresos));
        tvGastosMes.setText(String.format("$ %.2f", totalGastos));
    }

    private void loadTransactions() {
        if (UserSession.getInstance().getUser() == null) return;

        String email = UserSession.getInstance().getUser().getEmail();
        AccountRepository accountRepo = new AccountRepository(requireContext());
        TransactionRepository transactionRepo = new TransactionRepository(requireContext());

        List<Account> userAccounts = accountRepo.getAccountsByUserEmail(email);
        List<Transaction> allTransactions = new ArrayList<>();

        for (Account acc : userAccounts) {
            allTransactions.addAll(transactionRepo.getTransactionsByAccountId(acc.getId()));
        }

        transactionAdapter = new TransactionAdapter(allTransactions, this);
        recyclerTransactions.setAdapter(transactionAdapter);
    }

    @Override
    public void onEdit(Transaction transaction) {
        EditTransactionFragment fragment = EditTransactionFragment.newInstance(transaction);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDelete(Transaction transaction) {
        DeleteTransactionDialog dialog = DeleteTransactionDialog.newInstance(transaction.getId());
        dialog.show(getParentFragmentManager(), "DeleteTransactionDialog");
    }
}