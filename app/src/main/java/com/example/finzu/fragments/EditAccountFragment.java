package com.example.finzu.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.finzu.R;
import com.example.finzu.models.Account;
import com.example.finzu.repositories.AccountRepository;

public class EditAccountFragment extends DialogFragment {

    private static final String ARG_ACCOUNT_ID = "account_id";
    private static final String ARG_ACCOUNT_NAME = "account_name";

    private int accountId;
    private String currentName;

    public static EditAccountFragment newInstance(Account account) {
        EditAccountFragment fragment = new EditAccountFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ACCOUNT_ID, account.getId());
        args.putString(ARG_ACCOUNT_NAME, account.getName());
        fragment.setArguments(args);
        return fragment;
    }

    public EditAccountFragment() {
        //empty
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            accountId = getArguments().getInt(ARG_ACCOUNT_ID);
            currentName = getArguments().getString(ARG_ACCOUNT_NAME);
        }

        EditText etName = view.findViewById(R.id.et_account_name);
        Button btnSave = view.findViewById(R.id.btn_save);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        etName.setText(currentName);

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            if (!TextUtils.isEmpty(newName)) {
                AccountRepository repo = new AccountRepository(requireContext());
                repo.updateAccountName(accountId, newName);
                repo.close();
                dismiss();
            } else {
                etName.setError("El nombre no puede estar vacío");
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }
}
