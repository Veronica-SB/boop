package com.boop.app.ui.addpet;

import android.content.Intent;
import android.os.Bundle;
import com.boop.app.utils.ThemeManager;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.HomeActivity;
import com.boop.app.R;
import com.google.android.material.button.MaterialButton;

public class AddPetActivity extends AppCompatActivity {

    private int selectedSpecies = 0; // 0=Gato, 1=Perro, 2=Conejo, 3=Otro

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        LinearLayout btnCat    = findViewById(R.id.btn_species_cat);
        LinearLayout btnDog    = findViewById(R.id.btn_species_dog);
        LinearLayout btnRabbit = findViewById(R.id.btn_species_rabbit);
        LinearLayout btnOther  = findViewById(R.id.btn_species_other);
        MaterialButton btnCreate = findViewById(R.id.btn_create_pet);
        View btnBack = findViewById(R.id.btn_back);

        // ── Selector de especie ──────────────────────────────────────────────
        View.OnClickListener speciesListener = v -> {
            // Reset todos
            for (LinearLayout btn : new LinearLayout[]{btnCat, btnDog, btnRabbit, btnOther}) {
                btn.setBackgroundResource(R.drawable.bg_species_btn_inactive);
            }
            // Activar el pulsado
            ((LinearLayout) v).setBackgroundResource(R.drawable.bg_species_btn_active);
            if (v.getId() == R.id.btn_species_cat)    selectedSpecies = 0;
            else if (v.getId() == R.id.btn_species_dog)    selectedSpecies = 1;
            else if (v.getId() == R.id.btn_species_rabbit) selectedSpecies = 2;
            else selectedSpecies = 3;
        };

        btnCat.setOnClickListener(speciesListener);
        btnDog.setOnClickListener(speciesListener);
        btnRabbit.setOnClickListener(speciesListener);
        btnOther.setOnClickListener(speciesListener);

        // ── Subir foto (maqueta) ─────────────────────────────────────────────
        findViewById(R.id.btn_photo_upload).setOnClickListener(v -> {
            // TODO: Abrir galería / cámara
        });

        // ── Back ─────────────────────────────────────────────────────────────
        btnBack.setOnClickListener(v -> finish());

        // ── Crear mascota ─────────────────────────────────────────────────────
        btnCreate.setOnClickListener(v -> {
            // TODO: Guardar mascota en Firestore
            // Por ahora navega a la pantalla principal
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
