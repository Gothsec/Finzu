package com.example.finzu.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finzu.R;
import com.example.finzu.models.Account;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {
    public interface OnAccountActionListener {
        void onEdit(Account account);
        void onDelete(Account account);
    }

    private final List<Account> accountList;
    private final OnAccountActionListener listener;

    public AccountAdapter(List<Account> accountList, OnAccountActionListener listener) {
        this.accountList = accountList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account account = accountList.get(position);
        holder.tvName.setText(account.getName());
        holder.tvAmount.setText(String.format("$ %.2f", account.getAmount()));

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(account));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(account));
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount;
        ImageButton btnEdit, btnDelete;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            btnEdit = itemView.findViewById(R.id.btn_edit_account);
            btnDelete = itemView.findViewById(R.id.btn_delete_account);
        }
    }
}
