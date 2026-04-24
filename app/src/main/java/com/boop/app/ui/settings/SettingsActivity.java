package com.boop.app.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.boop.app.R;
import com.boop.app.ui.auth.AuthActivity;
import com.boop.app.ui.medical.AddMedicalVisitActivity;
import com.boop.app.ui.addpet.AddPetActivity;
import com.boop.app.utils.ThemeManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // --- Perfil ---
        findViewById(R.id.avatar_container).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivity(intent);
        });

        findViewById(R.id.btn_edit_profile).setOnClickListener(v -> {
            Toast.makeText(this, "Editando perfil...", Toast.LENGTH_SHORT).show();
        });

        // --- Gestión de Mascotas ---
        findViewById(R.id.row_pet_mochi).setOnClickListener(v -> {
            startActivity(new Intent(this, AddMedicalVisitActivity.class));
        });

        findViewById(R.id.row_pet_max).setOnClickListener(v -> {
            startActivity(new Intent(this, AddMedicalVisitActivity.class));
        });

        findViewById(R.id.btn_delete_mochi).setOnClickListener(v -> {
            Toast.makeText(this, "Eliminar a Mochi", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_delete_max).setOnClickListener(v -> {
            Toast.makeText(this, "Eliminar a Max", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.row_add_pet).setOnClickListener(v -> {
            startActivity(new Intent(this, AddPetActivity.class));
        });

        findViewById(R.id.row_guardians).setOnClickListener(v -> {
            startActivity(new Intent(this, GuardiansActivity.class));
        });

        // --- Notificaciones ---
        setupNotificationSwitches();

        // --- Apariencia (Temas) ---
        setupThemeOptions();

        // --- Privacidad y Cuenta ---
        View rowPrivacy = findViewById(R.id.row_privacy);
        if (rowPrivacy != null) {
            rowPrivacy.setOnClickListener(v -> {
                Toast.makeText(this, "Privacidad de datos (Próximamente)", Toast.LENGTH_SHORT).show();
            });
        }

        View rowTerms = findViewById(R.id.row_terms);
        if (rowTerms != null) {
            rowTerms.setOnClickListener(v -> {
                Toast.makeText(this, "Términos y condiciones (Próximamente)", Toast.LENGTH_SHORT).show();
            });
        }

        // --- Cerrar Sesión ---
        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupNotificationSwitches() {
        SwitchCompat swHealth = findViewById(R.id.sw_notifications_health);
        SwitchCompat swComm   = findViewById(R.id.sw_notifications_community);

        if (swHealth != null) {
            swHealth.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String msg = isChecked ? "Notificaciones de salud activadas" : "Notificaciones de salud desactivadas";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            });
        }

        if (swComm != null) {
            swComm.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String msg = isChecked ? "Notificaciones de comunidad activadas" : "Notificaciones de comunidad desactivadas";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupThemeOptions() {
        View radioSystem = findViewById(R.id.radio_system);
        View radioLight  = findViewById(R.id.radio_light);
        View radioDark   = findViewById(R.id.radio_dark);

        if (radioSystem == null || radioLight == null || radioDark == null) return;

        int currentMode = ThemeManager.getSavedMode(this);
        updateThemeRadios(currentMode, radioSystem, radioLight, radioDark);

        findViewById(R.id.row_theme_system).setOnClickListener(v -> {
            ThemeManager.setTheme(this, ThemeManager.MODE_SYSTEM);
            recreate();
        });

        findViewById(R.id.row_theme_light).setOnClickListener(v -> {
            ThemeManager.setTheme(this, ThemeManager.MODE_LIGHT);
            recreate();
        });

        findViewById(R.id.row_theme_dark).setOnClickListener(v -> {
            ThemeManager.setTheme(this, ThemeManager.MODE_DARK);
            recreate();
        });
    }

    private void updateThemeRadios(int mode, View sys, View light, View dark) {
        sys.setBackgroundResource(mode == ThemeManager.MODE_SYSTEM ? R.drawable.bg_radio_active : R.drawable.bg_radio_inactive);
        light.setBackgroundResource(mode == ThemeManager.MODE_LIGHT ? R.drawable.bg_radio_active : R.drawable.bg_radio_inactive);
        dark.setBackgroundResource(mode == ThemeManager.MODE_DARK ? R.drawable.bg_radio_active : R.drawable.bg_radio_inactive);
    }
}
