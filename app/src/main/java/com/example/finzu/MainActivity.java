package com.example.finzu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private TextView btnLogin, tvRegister;

    private SQLiteOpenHelper userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        userDB = UserSQLiteOpenHelper.getInstance(this);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = userDB.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT password FROM users WHERE email = ?", new String[]{email});

            if (cursor.getCount() == 0) {
                Toast.makeText(MainActivity.this, "Correo no registrado", Toast.LENGTH_SHORT).show();
                return;
            }

            String storedPassword = "";
            if (cursor.moveToFirst()) {
                storedPassword = cursor.getString(0);
            }

            cursor.close();
            db.close();

            if (!password.equals(storedPassword)) {
                Toast.makeText(MainActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                return;
            }

            // Successful login
            Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, HomeActivity.class); // or DashboardActivity
            intent.putExtra("userEmail", email);
            startActivity(intent);
            finish();
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}