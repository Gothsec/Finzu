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
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = userDB.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT password FROM users WHERE email = ?", new String[]{email});
            if (!cursor.moveToFirst()) {
                Toast.makeText(this, "Correo no registrado", Toast.LENGTH_SHORT).show();
                cursor.close();
                db.close();
                return;
            }

            String storedPassword = cursor.getString(0);
            cursor.close();

            if (!password.equals(storedPassword)) {
                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                db.close();
                return;
            }

            // ⚠️ Here we are getting the user's data and save it in an user instance to optimize the use of the db
            // If we wanna get some properties of the user, we will get them from the user instance, not from the db
            Cursor userCursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
            if (userCursor.moveToFirst()) {
                int id = userCursor.getInt(userCursor.getColumnIndexOrThrow("id"));
                String fullName = userCursor.getString(userCursor.getColumnIndexOrThrow("full_name"));
                String profilePic = userCursor.getString(userCursor.getColumnIndexOrThrow("profile_pic_url"));
                String currency = userCursor.getString(userCursor.getColumnIndexOrThrow("currency"));
                String reminder = userCursor.getString(userCursor.getColumnIndexOrThrow("reminder_hour"));

                User user = new User(id, fullName, email, password, profilePic, currency, reminder);
                UserSession.getInstance().setUser(user);
            }
            userCursor.close();
            db.close();

            // Go to home
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}
