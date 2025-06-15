package com.example.finzu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class NewAccountDialog extends DialogFragment {

    private EditText editNombre, editCantidad, editTipo;
    private Button btnGuardar;
    private TextView btnCerrar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // 🔥 Quitar el fondo gris predeterminado del diálogo
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        View view = inflater.inflate(R.layout.dialog_new_account, container, false);

        editNombre = view.findViewById(R.id.edit_nombre);
        editCantidad = view.findViewById(R.id.edit_cantidad);
        editTipo = view.findViewById(R.id.edit_tipo);
        btnGuardar = view.findViewById(R.id.btn_guardar);
        btnCerrar = view.findViewById(R.id.btn_cerrar);

        btnCerrar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombre = editNombre.getText().toString().trim();
            String cantidad = editCantidad.getText().toString().trim();
            String tipo = editTipo.getText().toString().trim();

            if (nombre.isEmpty() || cantidad.isEmpty() || tipo.isEmpty()) {
                Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Aquí puedes guardar en base de datos
            Toast.makeText(getContext(), "Cuenta guardada: " + nombre, Toast.LENGTH_SHORT).show();
            dismiss();
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
