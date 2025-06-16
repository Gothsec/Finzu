package com.example.finzu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.finzu.R;
import com.example.finzu.repositories.AccountRepository;
import com.example.finzu.models.Account;
import com.example.finzu.session.UserSession;
import com.example.finzu.utils.ToastUtils;

public class NewAccountDialog extends DialogFragment {

    private EditText editNombre, editCantidad;
    private Button btnGuardar;
    private TextView btnCerrar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Quitar fondo gris predeterminado
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        View view = inflater.inflate(R.layout.dialog_new_account, container, false);

        editNombre = view.findViewById(R.id.edit_nombre);
        editCantidad = view.findViewById(R.id.edit_cantidad);
        btnGuardar = view.findViewById(R.id.btn_guardar);
        btnCerrar = view.findViewById(R.id.btn_cerrar);

        btnCerrar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombre = editNombre.getText().toString().trim();
            String cantidadStr = editCantidad.getText().toString().trim();

            if (nombre.isEmpty() || cantidadStr.isEmpty()) {
                ToastUtils.showShort(requireContext(), "Completa todos los campos");
                return;
            }

            double cantidad;
            try {
                cantidad = Double.parseDouble(cantidadStr);
            } catch (NumberFormatException e) {
                ToastUtils.showLong(requireContext(), "Cantidad inválida");
                return;
            }

            String userEmail = UserSession.getInstance().getUser().getEmail();
            Account nuevaCuenta = new Account(0, userEmail, nombre, cantidad);

            AccountRepository repo = new AccountRepository(requireContext());
            long result = repo.insertAccount(userEmail, nuevaCuenta);
            repo.close();

            if (result != -1) {
                ToastUtils.showShort(requireContext(), "Cuenta guardada: " + nombre);
                dismiss();
            } else {
                ToastUtils.showLong(requireContext(), "Error al guardar cuenta");
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}
