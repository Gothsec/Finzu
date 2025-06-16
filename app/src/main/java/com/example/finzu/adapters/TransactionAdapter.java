package com.example.finzu.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.finzu.R;
import com.example.finzu.models.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvType, tvDate, tvDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvType = itemView.findViewById(R.id.tv_type);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDetails = itemView.findViewById(R.id.tv_details);
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

        if (tipo.equals("ingreso")) {
            holder.tvType.setText("Ingreso");
        } else if (tipo.equals("gasto")) {
            holder.tvType.setText("Gasto");
        } else {
            holder.tvType.setText("Desconocido"); // fallback por si acaso
        }

        holder.tvDate.setText(transaction.getDate());
        holder.tvDetails.setText(transaction.getDetails());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }
}
