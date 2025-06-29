package com.example.finzu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finzu.R;
import com.example.finzu.repositories.UserRepository;
import com.example.finzu.utils.NavigationUtils;
import com.example.finzu.utils.ToastUtils;
import com.example.finzu.utils.ValidationUtils;

public class Register extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private TextView btnRegister;
    private NavigationUtils navUtils = new NavigationUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> {
            // Get data
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validate data
            if (name.isEmpty() || !ValidationUtils.areLoginFieldsValid(email, password)) {
                ToastUtils.showShort(this, "Completa correctamente todos los campos");
                return;
            }

            UserRepository userRepo = new UserRepository(this);

            if (userRepo.isEmailRegistered(email)) {
                ToastUtils.showShort(this, "Este correo ya está registrado");
                userRepo.close();
                return;
            }

            // Register user (create user in database)
            boolean success = userRepo.registerUser(name, email, password);
            userRepo.close();

            if (success) {
                ToastUtils.showLong(this, "Cuenta creada para " + name);
                navUtils.changeActivityAndFinish(this, Login.class);
            } else {
                ToastUtils.showShort(this, "Error al registrar");
            }
        });
    }
}
