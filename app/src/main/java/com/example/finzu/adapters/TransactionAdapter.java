package com.example.finzu.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finzu.R;
import com.example.finzu.models.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final List<Transaction> transactionList;
    private final OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onEdit(Transaction transaction);
        void onDelete(Transaction transaction);
    }

    public TransactionAdapter(List<Transaction> transactionList, OnTransactionClickListener listener) {
        this.transactionList = transactionList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvType, tvDate, tvDetails;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvType = itemView.findViewById(R.id.tv_type);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDetails = itemView.findViewById(R.id.tv_details);
            btnEdit = itemView.findViewById(R.id.btn_edit_transaction);
            btnDelete = itemView.findViewById(R.id.btn_delete_transaction);
        }
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.tvAmount.setText(String.format("$ %.2f", transaction.getAmount()));
        String tipo = transaction.getType().trim().toLowerCase();

        holder.tvType.setText(tipo.equals("ingreso") ? "Ingreso" :
                tipo.equals("gasto") ? "Gasto" : "Desconocido");

        holder.tvDate.setText(transaction.getDate());
        holder.tvDetails.setText(transaction.getDetails());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(transaction));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(transaction));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }
}
