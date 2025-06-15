package com.example.finzu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    }

}
