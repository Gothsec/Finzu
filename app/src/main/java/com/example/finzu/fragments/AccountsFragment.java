package com.example.finzu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finzu.R;
import com.example.finzu.adapters.AccountAdapter;
import com.example.finzu.models.Account;
import com.example.finzu.repositories.AccountRepository;
import com.example.finzu.session.UserSession;
import com.example.finzu.utils.NavigationUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AccountsFragment extends Fragment implements AccountAdapter.OnAccountActionListener {

    NavigationUtils navigationUtils = new NavigationUtils();
    public AccountsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accounts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = requireActivity().findViewById(R.id.fab_add);
        if (fab != null) fab.hide();

        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        CardView btnHistory = view.findViewById(R.id.btn_history);
        btnHistory.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new HistoryFragment())
                .addToBackStack(null)
                .commit());

        CardView btnCreateAccounts = view.findViewById(R.id.btnCreateAccounts);
        btnCreateAccounts.setOnClickListener(v -> {
            NewAccountDialog dialog = new NewAccountDialog();
            dialog.show(getParentFragmentManager(), "NewAccountDialog");
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_accounts);
        TextView tvBalance = view.findViewById(R.id.tv_balance);

        String userEmail = UserSession.getInstance().getUser().getEmail();
        List<Account> cuentas;
        double totalBalance = 0.0;

        AccountRepository repo = new AccountRepository(requireContext());
        try {
            cuentas = repo.getAccountsByUserEmail(userEmail);
            for (Account cuenta : cuentas) {
                totalBalance += cuenta.getAmount();
            }
        } finally {
            repo.close();
        }

        tvBalance.setText(String.format("$ %.2f", totalBalance));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new AccountAdapter(cuentas, this));
    }

    @Override
    public void onEdit(Account account) {
        EditAccountFragment fragment = EditAccountFragment.newInstance(account);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onDelete(Account account) {
        ConfirmDeleteAccountDialog dialog = new ConfirmDeleteAccountDialog(account.getId(), accountId -> {
            AccountRepository repo = new AccountRepository(requireContext());
            repo.deleteAccount(accountId);
            repo.close();

            // Refresh fragment
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AccountsFragment())
                    .commit();
        });
        dialog.show(getParentFragmentManager(), "ConfirmDeleteAccountDialog");
    }
}
