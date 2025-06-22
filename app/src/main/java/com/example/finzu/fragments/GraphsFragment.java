package com.example.finzu.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.finzu.R;
import com.example.finzu.models.Transaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class GraphsFragment extends Fragment {

    private BarChart barChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        barChart = view.findViewById(R.id.barChart);

        // Temporal data
        List<Transaction> transaccionesDummy = new ArrayList<>();
        transaccionesDummy.add(new Transaction(1, 1500, "ingreso", 1, "Sueldo", "2025-06-01", true));
        transaccionesDummy.add(new Transaction(2, 500, "gasto", 1, "Comida", "2025-06-03", true));

        setupBarChart(transaccionesDummy);

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

        // custom labels
        String[] labels = new String[]{"Ingresos", "Gastos"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.length);

        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate(); // refresh
    }
}
