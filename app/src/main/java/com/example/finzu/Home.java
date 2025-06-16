package com.example.finzu;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class Home extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // debe contener un contenedor de fragmentos

        // Carga el fragmento Home al iniciar
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();

            // Espera un corto tiempo para que el popBackStack se complete y luego actualiza la UI
            getSupportFragmentManager().executePendingTransactions();

            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (current instanceof HomeFragment) {
                findViewById(R.id.fab_add).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.fab_add).setVisibility(View.GONE);
            }
        } else {
            super.onBackPressed();
        }
    }
}
