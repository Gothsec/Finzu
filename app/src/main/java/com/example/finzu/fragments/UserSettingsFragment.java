package com.example.finzu.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.finzu.R;
import com.example.finzu.utils.ProfileImageUtils;

public class UserSettingsFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imgProfile;
    private String profileImageUrl = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);

        imgProfile = view.findViewById(R.id.img_profile);

        // Load image
        ProfileImageUtils.loadProfilePicture(getContext(), profileImageUrl, imgProfile);

        imgProfile.setOnClickListener(v -> showImageSelectionDialog());

        return view;
    }

    private void showImageSelectionDialog() {
        String[] opciones = {"Elegir desde galería", "Pegar URL"};

        new AlertDialog.Builder(getContext())
                .setTitle("Seleccionar imagen de perfil")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        openGallery();
                    } else if (which == 1) {
                        promptImageUrl();
                    }
                })
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void promptImageUrl() {
        EditText input = new EditText(getContext());
        input.setHint("https://ejemplo.com/imagen.jpg");

        new AlertDialog.Builder(getContext())
                .setTitle("Ingresar URL de imagen")
                .setView(input)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    String url = input.getText().toString().trim();
                    if (!url.isEmpty()) {
                        profileImageUrl = url;
                        ProfileImageUtils.loadProfilePicture(getContext(), url, imgProfile);
                        // Save url
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            profileImageUrl = imageUri.toString();
            ProfileImageUtils.loadFromUri(getContext(), imageUri, imgProfile);
        }
    }
}
