package com.example.finzu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finzu.R;
import com.example.finzu.repositories.UserRepository;
import com.example.finzu.models.User;
import com.example.finzu.session.UserSession;
import com.example.finzu.utils.ToastUtils;
import com.example.finzu.utils.ValidationUtils;

public class Login extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private TextView btnLogin, tvRegister;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);


        // Login button event

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!ValidationUtils.areLoginFieldsValid(email, password)) {
                ToastUtils.showLong(this, "Correo o contraseña incorrectos");
                return;
            }

            UserRepository userRepo = new UserRepository(this);

            // Makes user with login data
            User user = userRepo.authenticate(email, password);
            userRepo.close();

            if (user == null) {
                ToastUtils.showLong(this, "Correo o contraseña incorrectos");
            } else {
                UserSession.getInstance().setUser(user);
                startActivity(new Intent(this, Home.class));
                finish();
            }
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, Register.class));
        });
    }
}