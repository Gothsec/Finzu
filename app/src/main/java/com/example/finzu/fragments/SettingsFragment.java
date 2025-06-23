package com.example.finzu.fragments;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finzu.R;
import com.example.finzu.activities.Login;
import com.example.finzu.models.User;
import com.example.finzu.repositories.UserRepository;
import com.example.finzu.session.UserSession;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private EditText etUserName, etUserPassword, etReminderHour;
    private TextView tvUserEmail, tvUsername;
    private RadioGroup rgCurrency;
    private Switch switchReminder;
    private Button btnLogout, btnDeleteAccount, btnSaveUser, btnApplyCurrency, btnSaveNotification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etUserName = view.findViewById(R.id.et_user_name);
        etUserPassword = view.findViewById(R.id.et_user_password);
        etReminderHour = view.findViewById(R.id.et_reminder_hour);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        tvUsername = view.findViewById(R.id.tv_username);

        rgCurrency = view.findViewById(R.id.rg_currency);
        switchReminder = view.findViewById(R.id.switch_reminder);

        btnLogout = view.findViewById(R.id.btn_logout);
        btnDeleteAccount = view.findViewById(R.id.btn_delete_account);
        btnSaveUser = view.findViewById(R.id.btn_save_user);
        btnApplyCurrency = view.findViewById(R.id.btn_apply_currency);
        btnSaveNotification = view.findViewById(R.id.btn_save_notification);

        String email = UserSession.getUser().getEmail();
        UserRepository userRepo = new UserRepository(requireContext());
        User user = userRepo.getUserByEmail(email);

        if (user != null) {
            etUserName.setText(user.getFullName());
            tvUserEmail.setText(user.getEmail());
            tvUsername.setText(user.getFullName());
            switchReminder.setChecked(user.isReminder());
            etReminderHour.setText(user.getReminderHour());

            switch (user.getCurrency()) {
                case "COP":
                    rgCurrency.check(R.id.rb_cop);
                    break;
                case "USD":
                    rgCurrency.check(R.id.rb_usd);
                    break;
                case "EUR":
                    rgCurrency.check(R.id.rb_eur);
                    break;
            }
        }

        etReminderHour.setOnClickListener(v -> {
            TimePickerDialog dialog = new TimePickerDialog(requireContext(), (view1, hourOfDay, minute) -> {
                String formatted = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                etReminderHour.setText(formatted);
            }, 8, 0, true);
            dialog.show();
        });

        btnSaveUser.setOnClickListener(v -> {
            String newName = etUserName.getText().toString().trim();
            String newPass = etUserPassword.getText().toString().trim();

            if (!newName.isEmpty()) {
                userRepo.updateUserName(email, newName);
                tvUsername.setText(newName);
                Toast.makeText(requireContext(), "Nombre actualizado", Toast.LENGTH_SHORT).show();
            }

            if (!newPass.isEmpty()) {
                userRepo.updateUserPassword(email, newPass);
                Toast.makeText(requireContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show();
            }
        });

        btnApplyCurrency.setOnClickListener(v -> {
            int selectedId = rgCurrency.getCheckedRadioButtonId();
            RadioButton selectedRadio = view.findViewById(selectedId);
            if (selectedRadio != null) {
                String currency = selectedRadio.getText().toString();
                userRepo.updateUserCurrency(email, currency);
                Toast.makeText(requireContext(), "Moneda actualizada", Toast.LENGTH_SHORT).show();
            }
        });

        btnSaveNotification.setOnClickListener(v -> {
            boolean enabled = switchReminder.isChecked();
            String hour = etReminderHour.getText().toString();
            userRepo.updateNotificationSettings(email, enabled, hour);
            Toast.makeText(requireContext(), "Recordatorio actualizado", Toast.LENGTH_SHORT).show();
        });

        btnDeleteAccount.setOnClickListener(v -> {
            userRepo.softDeleteUser(email);
            UserSession.clearSession(requireContext());
            startActivity(new Intent(requireContext(), Login.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });

        btnLogout.setOnClickListener(v -> {
            UserSession.clearSession(requireContext());
            Intent intent = new Intent(requireContext(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
