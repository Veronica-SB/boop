package com.boop.app.ui.alerts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.boop.app.R;

public class AlertsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alerts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Botón de nueva alerta — abre bottom sheet (maqueta)
        view.findViewById(R.id.btn_new_alert).setOnClickListener(v -> {
            // TODO: Abrir bottom sheet de nueva alerta
        });
        view.findViewById(R.id.btn_create_alert).setOnClickListener(v -> {
            // TODO: Abrir bottom sheet de nueva alerta
        });
        // TODO: Cargar lista de alertas desde base de datos
    }
}
