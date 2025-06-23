package com.example.finzu.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.finzu.R;
import com.example.finzu.models.Account;
import com.example.finzu.models.Transaction;
import com.example.finzu.repositories.AccountRepository;
import com.example.finzu.repositories.TransactionRepository;
import com.example.finzu.session.UserSession;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphsFragment extends Fragment {

    private BarChart barChart;
    private PieChart pieChart;
    private BarChart accountBalanceChart;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        barChart = view.findViewById(R.id.barChart);
        pieChart = view.findViewById(R.id.pieChart);
        accountBalanceChart = view.findViewById(R.id.accountChart);

        String email = UserSession.getUser().getEmail();
        TransactionRepository transactionRepo = new TransactionRepository(requireContext());
        AccountRepository accountRepo = new AccountRepository(requireContext());

        List<Transaction> transactions = transactionRepo.getTransactionsByUserEmail(email);
        List<Account> accounts = accountRepo.getAccountsByUserEmail(email);

        transactionRepo.close();
        accountRepo.close();

        setupBarChart(transactions);
        setupPieChart(transactions);
        setupAccountBalanceChart(accounts);

        return view;
    }

    private void setupBarChart(List<Transaction> transactions) {
        double ingresos = 0;
        double gastos = 0;

        for (Transaction t : transactions) {
            if (t.getType().equalsIgnoreCase("ingreso")) {
                ingresos += t.getAmount();
            } else {
                gastos += t.getAmount();
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, (float) ingresos));
        entries.add(new BarEntry(1f, (float) gastos));

        BarDataSet dataSet = new BarDataSet(entries, "Resumen Mensual");
        dataSet.setColors(Color.GREEN, Color.RED);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        String[] labels = new String[]{"Ingresos", "Gastos"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.length);

        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void setupPieChart(List<Transaction> transactions) {
        Map<String, Float> categoryTotals = new HashMap<>();

        for (Transaction t : transactions) {
            String category = t.getDetails();
            float amount = (float) t.getAmount();
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0f) + amount);
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Distribución por Categoría");
        dataSet.setColors(Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.LTGRAY, Color.BLUE);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void setupAccountBalanceChart(List<Account> accounts) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            entries.add(new BarEntry(i, (float) acc.getAmount()));
            labels.add(acc.getName());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Saldo por Cuenta");
        dataSet.setColors(Color.BLUE);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        accountBalanceChart.setData(data);

        XAxis xAxis = accountBalanceChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());

        accountBalanceChart.getDescription().setEnabled(false);
        accountBalanceChart.animateY(1000);
        accountBalanceChart.invalidate();
    }
}
