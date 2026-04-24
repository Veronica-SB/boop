package com.boop.app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.boop.app.R;
import com.boop.app.ui.addpet.AddPetActivity;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Campana de notificaciones -> Recordatorios
        view.findViewById(R.id.btn_notifications).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.alertsFragment);
        });

        // "Ver todas" las alertas -> Recordatorios
        view.findViewById(R.id.tv_see_all_alerts).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.alertsFragment);
        });

        // "Ver todas" las mascotas -> Simulado (abre Ajustes para gestionarlas)
        view.findViewById(R.id.tv_see_all_pets).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.profileFragment);
        });

        // Botón añadir mascota
        view.findViewById(R.id.btn_add_pet).setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddPetActivity.class));
        });

        // "Ver mapa" -> Ir al fragmento de mapa
        view.findViewById(R.id.tv_see_map).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.mapFragment);
        });
    }
}
