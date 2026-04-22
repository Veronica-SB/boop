package com.boop.app.ui.alerts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
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

        // ── Botones nueva alerta ─────────────────────────────────────────
        view.findViewById(R.id.btn_new_alert).setOnClickListener(v -> {
            // TODO: Abrir bottom sheet de nueva alerta
        });
        view.findViewById(R.id.btn_create_alert).setOnClickListener(v -> {
            // TODO: Abrir bottom sheet de nueva alerta
        });

        // ── Check "¿Ya lo has hecho?" ────────────────────────────────────
        setupCheckButton(view,
                R.id.btn_check_alert_1,
                R.id.checkbox_alert_1,
                R.id.check_icon_1);

        setupCheckButton(view,
                R.id.btn_check_alert_2,
                R.id.checkbox_alert_2,
                R.id.check_icon_2);

        setupCheckButton(view,
                R.id.btn_check_alert_3,
                R.id.checkbox_alert_3,
                R.id.check_icon_3);

        // ── Eliminar alertas ─────────────────────────────────────────────
        setupDeleteButton(view, R.id.btn_delete_alert_1, R.id.card_alert_1, "Vacuna antirrábica");
        setupDeleteButton(view, R.id.btn_delete_alert_2, R.id.card_alert_2, "Desparasitación interna");
        setupDeleteButton(view, R.id.btn_delete_alert_3, R.id.card_alert_3, "Revisión anual");

        // TODO: Cargar lista real de alertas desde Firestore
    }

    /**
     * Alterna visualmente el estado check de una alerta.
     * Cambia el drawable del checkbox y el texto del botón.
     */
    private void setupCheckButton(View root, int btnId, int checkboxId, int iconId) {
        View btn          = root.findViewById(btnId);
        View checkbox     = root.findViewById(checkboxId);
        ImageView icon    = root.findViewById(iconId);
        TextView label    = (TextView) ((ViewGroup) btn).getChildAt(1);

        btn.setTag(false); // false = no hecho

        btn.setOnClickListener(v -> {
            boolean isDone = (boolean) v.getTag();
            boolean newState = !isDone;
            v.setTag(newState);

            if (newState) {
                // Marcar como hecho
                checkbox.setBackgroundResource(R.drawable.bg_check_done);
                icon.setVisibility(View.VISIBLE);
                btn.setBackgroundResource(R.drawable.bg_species_btn_active);
                label.setText("¡Marcado como hecho!");
                label.setTextColor(requireContext().getColor(R.color.boop_green_dark));
                // TODO: Guardar estado en Firestore
            } else {
                // Desmarcar
                checkbox.setBackgroundResource(R.drawable.bg_check_undone);
                icon.setVisibility(View.GONE);
                btn.setBackgroundResource(R.drawable.bg_species_btn_inactive);
                label.setText("¿Ya lo has hecho?");
                label.setTextColor(requireContext().getColor(R.color.text_secondary_light));
                // TODO: Actualizar estado en Firestore
            }
        });
    }

    /**
     * Muestra un diálogo de confirmación antes de eliminar la tarjeta.
     */
    private void setupDeleteButton(View root, int btnId, int cardId, String alertName) {
        root.findViewById(btnId).setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar recordatorio")
                    .setMessage("¿Seguro que quieres eliminar \"" + alertName + "\"?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        CardView card = root.findViewById(cardId);
                        if (card != null) {
                            ((ViewGroup) card.getParent()).removeView(card);
                        }
                        // TODO: Eliminar de Firestore
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }
}
