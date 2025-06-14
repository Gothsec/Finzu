package com.example.finzu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Referencia al botón
        CardView btnAccounts = view.findViewById(R.id.btn_accounts);
        btnAccounts.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();

            transaction.replace(R.id.fragment_container, new AccountsFragment());
            transaction.addToBackStack(null); // Para que el botón "atrás" funcione
            transaction.commit();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fab = requireActivity().findViewById(R.id.fab_add);
        if (fab != null) fab.show();

        // Referencias al submenú
        LinearLayout fabMenu = view.findViewById(R.id.fab_menu);
        View btnImport = view.findViewById(R.id.btn_import);
        View btnScan = view.findViewById(R.id.btn_scan);
        View btnNew = view.findViewById(R.id.btn_new);

        final boolean[] isMenuOpen = {false}; // Necesitamos esto como array para usarlo dentro del listener

        fab.setOnClickListener(v -> {
            if (isMenuOpen[0]) {
                fabMenu.setVisibility(View.GONE);
                fab.setImageResource(R.drawable.ic_add); // Cambiar ícono a "+"
            } else {
                fabMenu.setVisibility(View.VISIBLE);
                fab.setImageResource(R.drawable.ic_close); // Cambiar ícono a "X"
            }
            isMenuOpen[0] = !isMenuOpen[0];
        });

        // Acciones de cada botón del submenú
        btnImport.setOnClickListener(v -> {
            // Acción para "Importar datos"
        });

        btnScan.setOnClickListener(v -> {
            // Acción para "Escanear factura"
        });

        btnNew.setOnClickListener(v -> {
            // Acción para "Nuevo registro"
        });
    }
}
