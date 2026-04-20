package com.boop.app.ui.settings;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.R;
import com.boop.app.ui.addpet.AddPetActivity;
import com.boop.app.utils.ThemeManager;
import android.content.Intent;

public class SettingsActivity extends AppCompatActivity {

    // Vistas de los radio buttons de apariencia
    private View radioSystem, radioLight, radioDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // ── Bind vistas ──────────────────────────────────────────────────
        radioSystem = findViewById(R.id.radio_system);
        radioLight  = findViewById(R.id.radio_light);
        radioDark   = findViewById(R.id.radio_dark);

        // ── Marcar radio según el tema guardado ──────────────────────────
        updateRadioButtons(ThemeManager.getSavedMode(this));

        // ── Listeners de apariencia ──────────────────────────────────────
        findViewById(R.id.opt_theme_system).setOnClickListener(v -> applyTheme(ThemeManager.MODE_SYSTEM));
        findViewById(R.id.opt_theme_light).setOnClickListener(v  -> applyTheme(ThemeManager.MODE_LIGHT));
        findViewById(R.id.opt_theme_dark).setOnClickListener(v   -> applyTheme(ThemeManager.MODE_DARK));

        // ── Navegación ───────────────────────────────────────────────────
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Editar perfil (maqueta)
        findViewById(R.id.btn_edit_profile).setOnClickListener(v -> {
            // TODO: Abrir pantalla de edición de perfil
        });

        // Añadir mascota
        findViewById(R.id.row_add_pet).setOnClickListener(v ->
                startActivity(new Intent(this, AddPetActivity.class)));

        // Mascotas existentes (maqueta)
        findViewById(R.id.row_pet_1).setOnClickListener(v -> {
            // TODO: Abrir detalle de mascota Mochi
        });
        findViewById(R.id.row_pet_2).setOnClickListener(v -> {
            // TODO: Abrir detalle de mascota Tofu
        });

        // Cerrar sesión
        findViewById(R.id.row_logout).setOnClickListener(v -> {
            // TODO: Firebase Auth signOut + volver a AuthActivity
        });

        // Privacidad / Términos / Política (maqueta)
        findViewById(R.id.row_privacy).setOnClickListener(v -> { /* TODO */ });
        findViewById(R.id.row_terms).setOnClickListener(v   -> { /* TODO */ });
        findViewById(R.id.row_policy).setOnClickListener(v  -> { /* TODO */ });

        // Toggles de notificaciones (maqueta visual)
        setupToggle(R.id.toggle_health,    true);
        setupToggle(R.id.toggle_community, true);
        setupToggle(R.id.toggle_messages,  false);
    }

    // ── Tema ─────────────────────────────────────────────────────────────────

    private void applyTheme(int mode) {
        ThemeManager.setTheme(this, mode);
        // AppCompatDelegate recrea la Activity automáticamente aplicando
        // el nuevo tema DayNight — no necesitamos recreate() manual.
        updateRadioButtons(mode);
    }

    private void updateRadioButtons(int mode) {
        int active   = R.drawable.bg_radio_active;
        int inactive = R.drawable.bg_radio_inactive;

        radioSystem.setBackgroundResource(mode == ThemeManager.MODE_SYSTEM ? active : inactive);
        radioLight .setBackgroundResource(mode == ThemeManager.MODE_LIGHT  ? active : inactive);
        radioDark  .setBackgroundResource(mode == ThemeManager.MODE_DARK   ? active : inactive);
    }

    // ── Toggle helper (estado visual) ────────────────────────────────────────

    private void setupToggle(int viewId, boolean initialOn) {
        View toggle = findViewById(viewId);
        toggle.setTag(initialOn);
        toggle.setBackgroundResource(initialOn
                ? R.drawable.bg_toggle_on : R.drawable.bg_toggle_off);

        toggle.setOnClickListener(v -> {
            boolean isOn = (boolean) v.getTag();
            boolean newState = !isOn;
            v.setTag(newState);
            v.setBackgroundResource(newState
                    ? R.drawable.bg_toggle_on : R.drawable.bg_toggle_off);
            // TODO: Persistir preferencia de notificación
        });
    }
}
