package com.example.finzu.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.finzu.fragments.HomeFragment;
import com.example.finzu.R;

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
            getSupportFragmentManager().executePendingTransactions();

            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            View fab = findViewById(R.id.fab_add);
            if (fab != null) {
                if (current instanceof HomeFragment) {
                    fab.setVisibility(View.VISIBLE);
                } else {
                    fab.setVisibility(View.GONE);
                }
            }
        } else {
            super.onBackPressed();
        }
    }

}
