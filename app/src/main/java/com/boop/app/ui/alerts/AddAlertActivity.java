package com.boop.app.ui.alerts;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.R;
import com.boop.app.utils.ThemeManager;

public class AddAlertActivity extends AppCompatActivity {

    private TextView tvLabelDescription;
    private EditText etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alert);

        tvLabelDescription = findViewById(R.id.tv_label_description);
        etDescription = findViewById(R.id.et_alert_description);

        // Botón atrás
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Tipos de recordatorio
        LinearLayout btnVaccine   = findViewById(R.id.btn_type_vaccine);
        LinearLayout btnMed       = findViewById(R.id.btn_type_med);
        LinearLayout btnVet       = findViewById(R.id.btn_type_vet);
        LinearLayout btnDeworming = findViewById(R.id.btn_type_deworming);
        LinearLayout btnOther     = findViewById(R.id.btn_type_other);

        View.OnClickListener typeListener = v -> {
            resetTypes(btnVaccine, btnMed, btnVet, btnDeworming, btnOther);
            v.setBackgroundResource(R.drawable.bg_species_btn_active);
            setTypeIconTint((LinearLayout) v, R.color.boop_green);
            setTypeTextColor((LinearLayout) v, R.color.boop_green_dark);

            updateFieldsByType(v.getId());
        };

        btnVaccine.setOnClickListener(typeListener);
        btnMed.setOnClickListener(typeListener);
        btnVet.setOnClickListener(typeListener);
        btnDeworming.setOnClickListener(typeListener);
        btnOther.setOnClickListener(typeListener);

        // Guardar
        findViewById(R.id.btn_save_alert).setOnClickListener(v -> {
            Toast.makeText(this, "Recordatorio guardado", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updateFieldsByType(int viewId) {
        if (viewId == R.id.btn_type_vaccine) {
            tvLabelDescription.setText("Descripción");
            etDescription.setHint("Vacuna antirrábica");
        } else if (viewId == R.id.btn_type_med) {
            tvLabelDescription.setText("Descripción");
            etDescription.setHint("Nombre del medicamento");
        } else if (viewId == R.id.btn_type_vet) {
            tvLabelDescription.setText("Nombre veterinario");
            etDescription.setHint("Clínica Vetica / Dr. López");
        } else if (viewId == R.id.btn_type_deworming) {
            tvLabelDescription.setText("Descripción");
            etDescription.setHint("Pastilla desparasitante");
        } else {
            tvLabelDescription.setText("Descripción");
            etDescription.setHint("Detalles del recordatorio");
        }
    }

    private void resetTypes(LinearLayout... buttons) {
        for (LinearLayout btn : buttons) {
            btn.setBackgroundResource(R.drawable.bg_species_btn_inactive);
            setTypeIconTint(btn, R.color.text_secondary_light);
            setTypeTextColor(btn, R.color.text_secondary_light);
        }
    }

    private void setTypeIconTint(LinearLayout layout, int colorRes) {
        View child = layout.getChildAt(0);
        if (child instanceof ImageView) {
            ((ImageView) child).setColorFilter(getColor(colorRes));
        }
    }

    private void setTypeTextColor(LinearLayout layout, int colorRes) {
        View child = layout.getChildAt(1);
        if (child instanceof TextView) {
            ((TextView) child).setTextColor(getColor(colorRes));
        }
    }
}
