package com.example.finzu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finzu.R;
import com.example.finzu.repositories.UserRepository;
import com.example.finzu.utils.ToastUtils;
import com.example.finzu.utils.ValidationUtils;

public class Register extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private TextView btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

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

            boolean success = userRepo.registerUser(name, email, password);
            userRepo.close();

            if (success) {
                ToastUtils.showShort(this, "Cuenta creada para " + name);
                startActivity(new Intent(this, Login.class));
                finish();
            } else {
                ToastUtils.showShort(this, "Error al registrar");
            }
        });
    }
}
