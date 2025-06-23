package com.example.finzu.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finzu.R;
import com.example.finzu.models.Account;
import com.example.finzu.models.Transaction;
import com.example.finzu.repositories.AccountRepository;
import com.example.finzu.repositories.TransactionRepository;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditTransactionFragment extends Fragment {

    private static final String ARG_TRANSACTION = "transaction";

    private Transaction transaction;
    private EditText etAmount, etDetails, etDate;
    private Spinner spnAccount, spnType;
    private List<Account> accounts;
    private int selectedAccountId;

    public static EditTransactionFragment newInstance(Transaction transaction) {
        EditTransactionFragment fragment = new EditTransactionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRANSACTION, (Serializable) transaction);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transaction = (Transaction) getArguments().getSerializable(ARG_TRANSACTION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        // Inicializa vistas
        etAmount = view.findViewById(R.id.et_amount);
        etDetails = view.findViewById(R.id.et_details);
        etDate = view.findViewById(R.id.et_date);
        spnAccount = view.findViewById(R.id.spn_account);
        spnType = view.findViewById(R.id.spn_type);
        Button btnSave = view.findViewById(R.id.btn_save);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        // Rellena datos de la transacción
        etAmount.setText(String.valueOf(transaction.getAmount()));
        etDetails.setText(transaction.getDetails());
        etDate.setText(transaction.getDate());

        // Date picker
        etDate.setOnClickListener(v -> showDatePicker());

        // Obtenemos el email del usuario asociado a esta transacción
        TransactionRepository tempRepo = new TransactionRepository(requireContext());
        String userEmail = tempRepo.getUserEmailByTransactionId(transaction.getId());
        tempRepo.close();

        // Obtenemos las cuentas del usuario
        AccountRepository accountRepo = new AccountRepository(requireContext());
        accounts = accountRepo.getAccountsByUserEmail(userEmail);
        accountRepo.close();

        // Adaptador para spinner de cuentas
        ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item
        );
        for (Account acc : accounts) {
            accountAdapter.add(acc.getName());
        }
        spnAccount.setAdapter(accountAdapter);

        // Selecciona la cuenta actual de la transacción
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getId() == transaction.getAccountId()) {
                spnAccount.setSelection(i);
                selectedAccountId = accounts.get(i).getId();
                break;
            }
        }

        // Spinner tipo de transacción
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.transaction_types, android.R.layout.simple_spinner_item
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(typeAdapter);
        spnType.setSelection(transaction.getType().equalsIgnoreCase("ingreso") ? 0 : 1);

        // Guardar cambios
        btnSave.setOnClickListener(v -> {
            double amount = Double.parseDouble(etAmount.getText().toString());
            String details = etDetails.getText().toString();
            String date = etDate.getText().toString();
            String type = spnType.getSelectedItem().toString();
            Account selectedAccount = accounts.get(spnAccount.getSelectedItemPosition());

            transaction.setAmount(amount);
            transaction.setDetails(details);
            transaction.setDate(date);
            transaction.setType(type);
            transaction.setAccountId(selectedAccount.getId());

            // Nueva instancia del repositorio SOLO para actualizar
            TransactionRepository updateRepo = new TransactionRepository(requireContext());
            updateRepo.updateTransaction(transaction);
            updateRepo.close();

            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Cancelar
        btnCancel.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    etDate.setText(format.format(selected.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }
}
