package com.example.finzu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

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
            // Acción futura: importar CSV
        });

        btnScan.setOnClickListener(v -> {
            // Acción futura: escanear factura
        });

        btnNew.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container, new TransactionFormFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });
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
                int anio = Integer.parseInt(partesFecha[0]);
                int mes = Integer.parseInt(partesFecha[1]);

                if (anio == anioActual && mes == mesActual) {
                    String tipo = tx.getType().trim().toLowerCase();

                    if (tipo.equals("ingreso")) {
                        totalIngresos += tx.getAmount();
                    } else if (tipo.equals("gasto")) {
                        totalGastos += tx.getAmount();
                    }

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

        transactionAdapter = new TransactionAdapter(allTransactions);
        recyclerTransactions.setAdapter(transactionAdapter);
    }
}
