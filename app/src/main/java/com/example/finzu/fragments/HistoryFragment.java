package com.example.finzu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finzu.R;
import com.example.finzu.adapters.TransactionAdapter;
import com.example.finzu.models.Account;
import com.example.finzu.models.Transaction;
import com.example.finzu.repositories.AccountRepository;
import com.example.finzu.repositories.TransactionRepository;
import com.example.finzu.session.UserSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment implements TransactionAdapter.OnTransactionClickListener {

    private RadioGroup radioGroupTipo;
    private Spinner spinnerCuenta, spinnerMes;
    private Button btnBuscar;
    private ImageView btnBack;
    private RecyclerView recyclerView;

    private Map<String, Integer> cuentaNombreToId = new HashMap<>();
    private TransactionRepository transactionRepository;

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

        btnBuscar.setEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionRepository = new TransactionRepository(requireContext());
    }

    private void initUI(View view) {
        radioGroupTipo = view.findViewById(R.id.rg_tipo);
        spinnerCuenta = view.findViewById(R.id.spinner_cuenta);
        spinnerMes = view.findViewById(R.id.spinner_mes);
        btnBuscar = view.findViewById(R.id.btn_buscar);
        btnBack = view.findViewById(R.id.btn_back);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    private void setupSpinners() {
        String email = UserSession.getInstance().getUser().getEmail();
        AccountRepository repo = new AccountRepository(requireContext());
        List<Account> cuentasUsuario = repo.getAccountsByUserEmail(email);

        List<String> nombresCuentas = new ArrayList<>();
        nombresCuentas.add("Selecciona una cuenta");
        for (Account acc : cuentasUsuario) {
            nombresCuentas.add(acc.getName());
            cuentaNombreToId.put(acc.getName(), acc.getId());
        }

        ArrayAdapter<String> adapterCuenta = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, nombresCuentas);
        spinnerCuenta.setAdapter(adapterCuenta);

        String[] meses = {
                "Selecciona un mes",
                "Enero - 2025", "Febrero - 2025", "Marzo - 2025", "Abril - 2025", "Mayo - 2025", "Junio - 2025",
                "Julio - 2025", "Agosto - 2025", "Septiembre - 2025", "Octubre - 2025", "Noviembre - 2025", "Diciembre - 2025",
                "Enero - 2024", "Febrero - 2024", "Marzo - 2024", "Abril - 2024", "Mayo - 2024", "Junio - 2024",
                "Julio - 2024", "Agosto - 2024", "Septiembre - 2024", "Octubre - 2024", "Noviembre - 2024", "Diciembre - 2024"
        };
        ArrayAdapter<String> adapterMes = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, meses);
        spinnerMes.setAdapter(adapterMes);
    }

    private void setupListeners() {
        btnBuscar.setOnClickListener(v -> mostrarHistorial());

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        if (radioGroupTipo != null) {
            radioGroupTipo.setOnCheckedChangeListener((group, checkedId) -> validarFiltros());
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

        btnBuscar.setEnabled(tipoSeleccionado && cuentaSeleccionada && mesSeleccionado);
    }

    private void mostrarHistorial() {
        String cuentaNombre = spinnerCuenta.getSelectedItem().toString();
        String mesSeleccionado = spinnerMes.getSelectedItem().toString();
        int tipoId = radioGroupTipo.getCheckedRadioButtonId();
        String tipo = (tipoId == R.id.rb_gasto) ? "Gasto" : "Ingreso";

        if (!cuentaNombreToId.containsKey(cuentaNombre)) {
            Toast.makeText(getContext(), "Cuenta no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        int cuentaId = cuentaNombreToId.get(cuentaNombre);
        String mesAnio = convertirMesAMMYYYY(mesSeleccionado);

        List<Transaction> resultados = transactionRepository.getTransactionsByFilters(cuentaId, mesAnio, tipo);

        if (resultados.isEmpty()) {
            Toast.makeText(getContext(), "No hay transacciones para los filtros seleccionados.", Toast.LENGTH_SHORT).show();
        }

        TransactionAdapter adapter = new TransactionAdapter(resultados, this);
        recyclerView.setAdapter(adapter);
    }

    private String convertirMesAMMYYYY(String entrada) {
        String[] partes = entrada.split(" - ");
        if (partes.length != 2) return "";

        String mesNombre = partes[0];
        String anio = partes[1];

        String mes;
        switch (mesNombre.toLowerCase()) {
            case "enero": mes = "01"; break;
            case "febrero": mes = "02"; break;
            case "marzo": mes = "03"; break;
            case "abril": mes = "04"; break;
            case "mayo": mes = "05"; break;
            case "junio": mes = "06"; break;
            case "julio": mes = "07"; break;
            case "agosto": mes = "08"; break;
            case "septiembre": mes = "09"; break;
            case "octubre": mes = "10"; break;
            case "noviembre": mes = "11"; break;
            case "diciembre": mes = "12"; break;
            default: mes = "00"; break;
        }

        return mes + "-" + anio;
    }

    @Override
    public void onEdit(Transaction transaction) {
        EditTransactionFragment fragment = EditTransactionFragment.newInstance(transaction);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDelete(Transaction transaction) {
        DeleteTransactionDialog dialog = DeleteTransactionDialog.newInstance(transaction.getId());
        dialog.show(getParentFragmentManager(), "DeleteTransactionDialog");
    }
}
