package com.example.finzu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HistoryFragment extends Fragment {

    private RadioGroup radioGroupTipo;
    private Spinner spinnerCuenta, spinnerMes;
    private Button btnExportar;
    private ImageView btnBack;

    public HistoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        setupSpinners();
        setupListeners();

        // Botón deshabilitado por defecto
        btnExportar.setEnabled(false);
    }

    private void initUI(View view) {
        radioGroupTipo = view.findViewById(R.id.rb_gasto).getParent() instanceof RadioGroup
                ? (RadioGroup) view.findViewById(R.id.rb_gasto).getParent()
                : null;

        spinnerCuenta = view.findViewById(R.id.spinner_cuenta);
        spinnerMes = view.findViewById(R.id.spinner_mes);
        btnExportar = view.findViewById(R.id.btn_exportar);
        btnBack = view.findViewById(R.id.btn_back);
    }

    private void setupSpinners() {
        String[] cuentas = {"Selecciona una cuenta", "Nequi", "Bancolombia", "Efectivo"};
        ArrayAdapter<String> adapterCuenta = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, cuentas);
        spinnerCuenta.setAdapter(adapterCuenta);

        String[] meses = {"Selecciona un mes", "Mayo - 2025", "Abril - 2025", "Marzo - 2025"};
        ArrayAdapter<String> adapterMes = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, meses);
        spinnerMes.setAdapter(adapterMes);
    }

    private void setupListeners() {
        // Exportar CSV
        btnExportar.setOnClickListener(v -> {
            String cuenta = spinnerCuenta.getSelectedItem().toString();
            String mes = spinnerMes.getSelectedItem().toString();

            Toast.makeText(getContext(),
                    "Exportando CSV...\nCuenta: " + cuenta + "\nMes: " + mes,
                    Toast.LENGTH_LONG).show();
        });

        // Botón Atrás
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Listeners para filtros
        if (radioGroupTipo != null) {
            radioGroupTipo.setOnCheckedChangeListener((group, checkedId) -> {
                validarFiltros();
                if (checkedId == R.id.rb_gasto) {
                    Toast.makeText(getContext(), "Filtro: Gasto", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.rb_ingreso) {
                    Toast.makeText(getContext(), "Filtro: Ingreso", Toast.LENGTH_SHORT).show();
                }
            });
        }

        spinnerCuenta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validarFiltros();
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validarFiltros();
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void validarFiltros() {
        boolean tipoSeleccionado = radioGroupTipo != null && radioGroupTipo.getCheckedRadioButtonId() != -1;
        boolean cuentaSeleccionada = spinnerCuenta.getSelectedItemPosition() > 0;
        boolean mesSeleccionado = spinnerMes.getSelectedItemPosition() > 0;

        btnExportar.setEnabled(tipoSeleccionado && cuentaSeleccionada && mesSeleccionado);
    }
}
