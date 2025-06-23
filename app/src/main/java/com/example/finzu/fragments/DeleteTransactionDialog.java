package com.example.finzu.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.finzu.repositories.TransactionRepository;

public class DeleteTransactionDialog extends DialogFragment {

    private int transactionId;

    public static DeleteTransactionDialog newInstance(int id) {
        DeleteTransactionDialog dialog = new DeleteTransactionDialog();
        Bundle args = new Bundle();
        args.putInt("transactionId", id);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            transactionId = getArguments().getInt("transactionId");
        }

        return new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar transacción")
                .setMessage("¿Estás seguro de que deseas eliminar esta transacción?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    TransactionRepository repo = new TransactionRepository(requireContext());
                    repo.deleteTransaction(transactionId);
                    repo.close();
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create();
    }
}
