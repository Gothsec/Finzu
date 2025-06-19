package com.example.finzu.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finzu.R;
import com.example.finzu.repositories.AccountRepository;
import com.example.finzu.repositories.TransactionRepository;
import com.example.finzu.models.Account;
import com.example.finzu.models.Transaction;
import com.example.finzu.session.UserSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionFormFragment extends Fragment {

    private Spinner spinnerAccount;
    private EditText editAmount, editDetails;
    private RadioGroup radioGroupType;
    private Button btnRegister;

    private List<Account> userAccounts;

    public TransactionFormFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        spinnerAccount = view.findViewById(R.id.spinner_account);
        editAmount = view.findViewById(R.id.edit_text_amount);
        editDetails = view.findViewById(R.id.edit_text_details);
        radioGroupType = view.findViewById(R.id.radio_group_type);
        btnRegister = view.findViewById(R.id.button_register_transaction);

        loadAccounts();

        btnRegister.setOnClickListener(v -> registerTransaction());
    }

    private void loadAccounts() {
        if (UserSession.getInstance().getUser() == null) return;

        String userEmail = UserSession.getInstance().getUser().getEmail();
        AccountRepository accountRepository = new AccountRepository(requireContext());
        userAccounts = accountRepository.getAccountsByUserEmail(userEmail);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getAccountNames()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccount.setAdapter(adapter);
    }

    private List<String> getAccountNames() {
        List<String> names = new ArrayList<>();
        for (Account account : userAccounts) {
            names.add(account.getName());
        }
        return names;
    }


    private void registerTransaction() {
        int selectedPosition = spinnerAccount.getSelectedItemPosition();
        if (selectedPosition == AdapterView.INVALID_POSITION || userAccounts == null || userAccounts.isEmpty()) {
            Toast.makeText(getContext(), "Selecciona una cuenta", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountStr = editAmount.getText().toString().trim();
        String details = editDetails.getText().toString().trim();

        int checkedRadioButtonId = radioGroupType.getCheckedRadioButtonId();
        String type = null;
        if (checkedRadioButtonId == R.id.radio_income) {
            type = "Ingreso";
        } else if (checkedRadioButtonId == R.id.radio_expense) {
            type = "Gasto";
        }

        if (TextUtils.isEmpty(amountStr) || type == null) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        int accountId = userAccounts.get(selectedPosition).getId();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Transaction transaction = new Transaction(-1, amount, type, accountId, details, date, true);

        TransactionRepository repo = new TransactionRepository(requireContext());
        long insertedId = repo.insertTransaction(transaction);
        repo.close();

        if (insertedId != -1) {
            Toast.makeText(getContext(), "Transacción registrada", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        } else {
            Toast.makeText(getContext(), "Error al registrar", Toast.LENGTH_SHORT).show();
        }
    }

}
