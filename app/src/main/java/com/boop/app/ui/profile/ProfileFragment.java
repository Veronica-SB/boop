package com.boop.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.boop.app.R;
import com.boop.app.ui.settings.SettingsActivity;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Botón de ajustes → abre SettingsActivity
        view.findViewById(R.id.btn_settings).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SettingsActivity.class)));

        // Editar perfil → también abre SettingsActivity (sección editar)
        view.findViewById(R.id.btn_edit_profile).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SettingsActivity.class)));

        // Cerrar sesión
        view.findViewById(R.id.btn_logout).setOnClickListener(v -> {
            // TODO: Firebase Auth signOut + volver a AuthActivity
        });

        // TODO: Cargar datos de usuario desde Firebase Auth
    }
}
