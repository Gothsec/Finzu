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
import com.example.finzu.database.AccountRepository;
import com.example.finzu.models.Account;
import com.example.finzu.session.UserSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AccountsFragment extends Fragment {

    public AccountsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        // Boton volver atras
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Boton historial
        CardView btnHistory = view.findViewById(R.id.btn_history);
        btnHistory.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HistoryFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Boton crear cuenta
        CardView btnCreateAccounts = view.findViewById(R.id.btnCreateAccounts);
        btnCreateAccounts.setOnClickListener(v -> {
            NewAccountDialog dialog = new NewAccountDialog();
            dialog.show(getParentFragmentManager(), "NewAccountDialog");
        });

        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = requireActivity().findViewById(R.id.fab_add);
        if (fab != null) fab.hide();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_accounts);
        TextView tvBalance = view.findViewById(R.id.tv_balance); // Asegúrate de tener este ID en tu XML

        String userEmail = UserSession.getInstance().getUser().getEmail();
        AccountRepository repo = new AccountRepository(requireContext());
        List<Account> cuentas = repo.getAccountsByUserEmail(userEmail);
        repo.close();

        double balanceTotal = 0;
        for (Account cuenta : cuentas) {
            balanceTotal += cuenta.getAmount();
        }

        tvBalance.setText(String.format("Balance total: $ %.2f", balanceTotal));

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new AccountAdapter(cuentas));
    }
}
