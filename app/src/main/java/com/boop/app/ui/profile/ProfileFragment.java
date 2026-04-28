package com.boop.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.boop.app.R;
import com.boop.app.ui.auth.AuthActivity;
import com.boop.app.ui.settings.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;

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

        // Abrir Ajustes (La pantalla de la tarjeta verde)
        view.findViewById(R.id.btn_settings).setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SettingsActivity.class));
        });

        // Editar perfil
        view.findViewById(R.id.btn_edit_profile).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Editar perfil...", Toast.LENGTH_SHORT).show();
        });

        // Notificaciones
        view.findViewById(R.id.btn_notifications_settings).setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SettingsActivity.class));
        });

        // Privacidad
        view.findViewById(R.id.btn_privacy).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Privacidad (Próximamente)", Toast.LENGTH_SHORT).show();
        });

        // Cerrar Sesión
        view.findViewById(R.id.btn_logout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
