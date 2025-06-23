package com.example.finzu.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmDeleteAccountDialog extends DialogFragment {

    public interface OnDeleteConfirmedListener {
        void onDeleteConfirmed(int accountId);
    }

    private int accountId;
    private OnDeleteConfirmedListener listener;

    public ConfirmDeleteAccountDialog(int accountId, OnDeleteConfirmedListener listener) {
        this.accountId = accountId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setTitle("¿Eliminar cuenta?")
                .setMessage("¿Estás seguro de que quieres eliminar esta cuenta?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (listener != null) {
                        listener.onDeleteConfirmed(accountId);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
    }
}
