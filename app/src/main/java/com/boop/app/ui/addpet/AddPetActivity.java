package com.boop.app.ui.addpet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.HomeActivity;
import com.boop.app.R;
import com.boop.app.utils.ThemeManager;
import com.google.android.material.button.MaterialButton;

public class AddPetActivity extends AppCompatActivity {

    // 0=Gato, 1=Perro, 2=Conejo, 3=Ave
    private int selectedSpecies = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        LinearLayout btnCat    = findViewById(R.id.btn_species_cat);
        LinearLayout btnDog    = findViewById(R.id.btn_species_dog);
        LinearLayout btnRabbit = findViewById(R.id.btn_species_rabbit);
        LinearLayout btnBird   = findViewById(R.id.btn_species_bird);
        MaterialButton btnCreate = findViewById(R.id.btn_create_pet);

        // ── Selector de especie ──────────────────────────────────────────
        View.OnClickListener speciesListener = v -> {
            // Reset todos
            for (LinearLayout btn : new LinearLayout[]{btnCat, btnDog, btnRabbit, btnBird}) {
                btn.setBackgroundResource(R.drawable.bg_species_btn_inactive);
                setSpeciesLabelColor(btn, R.color.text_secondary_light);
                setSpeciesIconTint(btn, R.color.text_secondary_light);
            }
            // Activar el pulsado
            ((LinearLayout) v).setBackgroundResource(R.drawable.bg_species_btn_active);
            setSpeciesLabelColor((LinearLayout) v, R.color.boop_green_dark);
            setSpeciesIconTint((LinearLayout) v, R.color.boop_green);

            if (v.getId() == R.id.btn_species_cat)    selectedSpecies = 0;
            else if (v.getId() == R.id.btn_species_dog)    selectedSpecies = 1;
            else if (v.getId() == R.id.btn_species_rabbit) selectedSpecies = 2;
            else if (v.getId() == R.id.btn_species_bird)   selectedSpecies = 3;
        };

        btnCat.setOnClickListener(speciesListener);
        btnDog.setOnClickListener(speciesListener);
        btnRabbit.setOnClickListener(speciesListener);
        btnBird.setOnClickListener(speciesListener);

        // ── Foto ─────────────────────────────────────────────────────────
        findViewById(R.id.btn_photo_upload).setOnClickListener(v -> {
            // TODO: Abrir galería / cámara
        });

        // ── Back ─────────────────────────────────────────────────────────
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // ── Crear mascota ─────────────────────────────────────────────────
        btnCreate.setOnClickListener(v -> {
            // TODO: Guardar mascota en Firestore
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void setSpeciesLabelColor(LinearLayout container, int colorRes) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof TextView) {
                ((TextView) child).setTextColor(getColor(colorRes));
            }
        }
    }

    private void setSpeciesIconTint(LinearLayout container, int colorRes) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setColorFilter(getColor(colorRes));
            }
        }
    }
}
