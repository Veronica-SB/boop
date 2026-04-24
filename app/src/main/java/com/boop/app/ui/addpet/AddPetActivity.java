package com.boop.app.ui.addpet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.HomeActivity;
import com.boop.app.R;
import com.boop.app.utils.ThemeManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class AddPetActivity extends AppCompatActivity {

    // 0=Gato, 1=Perro, 2=Conejo, 3=Ave
    private int selectedSpecies = 0;
    private Uri selectedImageUri;

    // Launcher para seleccionar imagen de la galería
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Mostrar miniatura (asumiendo que hay un ImageView o similar)
                    Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        // ── Species buttons ──────────────────────────────────────────────────
        LinearLayout btnCat    = findViewById(R.id.btn_species_cat);
        LinearLayout btnDog    = findViewById(R.id.btn_species_dog);
        LinearLayout btnRabbit = findViewById(R.id.btn_species_rabbit);
        LinearLayout btnBird   = findViewById(R.id.btn_species_bird);

        View.OnClickListener speciesListener = v -> {
            resetSpecies(btnCat, btnDog, btnRabbit, btnBird);
            ((LinearLayout) v).setBackgroundResource(R.drawable.bg_species_btn_active);
            setSpeciesTextColor((LinearLayout) v, R.color.boop_green_dark);
            setSpeciesIconTint((LinearLayout) v, R.color.boop_green);

            if      (v.getId() == R.id.btn_species_cat)    selectedSpecies = 0;
            else if (v.getId() == R.id.btn_species_dog)    selectedSpecies = 1;
            else if (v.getId() == R.id.btn_species_rabbit) selectedSpecies = 2;
            else if (v.getId() == R.id.btn_species_bird)   selectedSpecies = 3;

            updateChipFieldRequirement();
        };
        btnCat.setOnClickListener(speciesListener);
        btnDog.setOnClickListener(speciesListener);
        btnRabbit.setOnClickListener(speciesListener);
        btnBird.setOnClickListener(speciesListener);

        // ── Vet mode tabs (Mantenemos funcionalidad previa) ────────────────
        // ... (asumiendo que el código previo está integrado o lo reintegro)
        setupVetTabs();

        // ── Foto ────────────────────────────────────────────────────────────
        findViewById(R.id.btn_photo_upload).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        // ── Back ────────────────────────────────────────────────────────────
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // ── Create pet ───────────────────────────────────────────────────────
        findViewById(R.id.btn_create_pet).setOnClickListener(v -> {
            if (validateForm()) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
        });

        updateChipFieldRequirement();
    }

    private void setupVetTabs() {
        TextView tabMaps   = findViewById(R.id.tab_vet_maps);
        TextView tabManual = findViewById(R.id.tab_vet_manual);
        View layoutMaps    = findViewById(R.id.layout_vet_maps);
        View layoutManual  = findViewById(R.id.layout_vet_manual);

        if (tabMaps == null) return;

        tabMaps.setOnClickListener(v -> {
            tabMaps.setBackgroundResource(R.drawable.bg_tab_selected);
            tabMaps.setTextColor(getColor(R.color.white));
            tabManual.setBackgroundResource(android.R.color.transparent);
            tabManual.setTextColor(getColor(R.color.text_secondary_light));
            layoutMaps.setVisibility(View.VISIBLE);
            layoutManual.setVisibility(View.GONE);
        });
        tabManual.setOnClickListener(v -> {
            tabManual.setBackgroundResource(R.drawable.bg_tab_selected);
            tabManual.setTextColor(getColor(R.color.white));
            tabMaps.setBackgroundResource(android.R.color.transparent);
            tabMaps.setTextColor(getColor(R.color.text_secondary_light));
            layoutManual.setVisibility(View.VISIBLE);
            layoutMaps.setVisibility(View.GONE);
        });
    }

    private void updateChipFieldRequirement() {
        boolean required = (selectedSpecies == 0 || selectedSpecies == 1);
        TextView mark = findViewById(R.id.tv_chip_required_mark);
        TextInputLayout tilChip = findViewById(R.id.til_chip);
        if (mark != null)   mark.setVisibility(required ? View.VISIBLE : View.GONE);
        if (tilChip != null) tilChip.setHint("Nº de microchip");
    }

    private boolean validateForm() {
        EditText etName = findViewById(R.id.et_pet_name);
        if (TextUtils.isEmpty(etName.getText().toString().trim())) {
            etName.setError("Nombre obligatorio");
            return false;
        }
        return true;
    }

    private void resetSpecies(LinearLayout... buttons) {
        for (LinearLayout btn : buttons) {
            btn.setBackgroundResource(R.drawable.bg_species_btn_inactive);
            setSpeciesTextColor(btn, R.color.text_secondary_light);
            setSpeciesIconTint(btn, R.color.text_secondary_light);
        }
    }

    private void setSpeciesTextColor(LinearLayout container, int colorRes) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof TextView) ((TextView) child).setTextColor(getColor(colorRes));
        }
    }

    private void setSpeciesIconTint(LinearLayout container, int colorRes) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof android.widget.ImageView)
                ((android.widget.ImageView) child).setColorFilter(getColor(colorRes));
        }
    }
}
