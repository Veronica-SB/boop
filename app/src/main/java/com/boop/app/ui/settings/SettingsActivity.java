package com.boop.app.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.boop.app.R;
import com.boop.app.data.model.Pet;
import com.boop.app.ui.addpet.AddPetActivity;
import com.boop.app.ui.auth.AuthActivity;
import com.boop.app.ui.medical.AddMedicalVisitActivity;
import com.boop.app.utils.ThemeManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private LinearLayout containerPets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db          = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        containerPets = findViewById(R.id.container_pets_settings);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // ── Perfil usuario ────────────────────────────────────────
        if (currentUser != null) {
            TextView tvName = findViewById(R.id.tv_user_name);
            TextView tvEmail = findViewById(R.id.tv_user_email);

            if (currentUser.getDisplayName() != null)
                tvName.setText(currentUser.getDisplayName());
            else {
                db.collection("users").document(currentUser.getUid()).get()
                        .addOnSuccessListener(doc -> {
                            String name = doc.getString("name");
                            if (name != null) tvName.setText(name);
                        });
            }

            if (currentUser.getEmail() != null)
                tvEmail.setText(currentUser.getEmail());
        }

        // ── Avatar ────────────────────────────────────────────────
        findViewById(R.id.avatar_container).setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)));

        // ── Editar perfil ─────────────────────────────────────────
        findViewById(R.id.btn_edit_profile).setOnClickListener(v ->
                Toast.makeText(this, "Editar perfil...", Toast.LENGTH_SHORT).show());

        // ── Añadir mascota ────────────────────────────────────────
        findViewById(R.id.row_add_pet).setOnClickListener(v ->
                startActivity(new Intent(this, AddPetActivity.class)));

        // ── Notificaciones ────────────────────────────────────────
        setupNotificationSwitches();

        // ── Apariencia ────────────────────────────────────────────
        setupThemeOptions();

        // ── Privacidad ────────────────────────────────────────────
        View rowPrivacy = findViewById(R.id.row_privacy);
        if (rowPrivacy != null)
            rowPrivacy.setOnClickListener(v ->
                    Toast.makeText(this, "Privacidad de datos (Próximamente)", Toast.LENGTH_SHORT).show());

        View rowTerms = findViewById(R.id.row_terms);
        if (rowTerms != null)
            rowTerms.setOnClickListener(v ->
                    Toast.makeText(this, "Términos y condiciones (Próximamente)", Toast.LENGTH_SHORT).show());

        // ── Cerrar sesión ─────────────────────────────────────────
        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // ── Cargar mascotas ───────────────────────────────────────
        loadPets();
    }

    private void loadPets() {
        if (currentUser == null) return;

        db.collection("pets")
                .whereEqualTo("ownerId", currentUser.getUid())
                .get()
                .addOnSuccessListener(snapshots -> {
                    containerPets.removeAllViews();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Pet pet = doc.toObject(Pet.class);
                        pet.setId(doc.getId());
                        containerPets.addView(buildPetRow(pet));
                        containerPets.addView(buildDivider());
                    }
                });
    }

    private View buildPetRow(Pet pet) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        row.setClickable(true);
        row.setFocusable(true);
        row.setBackgroundResource(android.R.drawable.list_selector_background);

        // Icono
        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(36), dpToPx(36));
        icon.setLayoutParams(iconParams);
        icon.setBackgroundResource(R.drawable.bg_circle_green_light);
        icon.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        if (Pet.PetSpecies.DOG.name().equals(pet.getSpecies()))
            icon.setImageResource(R.drawable.ic_dog);
        else if (Pet.PetSpecies.RABBIT.name().equals(pet.getSpecies()))
            icon.setImageResource(R.drawable.ic_rabbit);
        else if (Pet.PetSpecies.BIRD.name().equals(pet.getSpecies()))
            icon.setImageResource(R.drawable.ic_bird);
        else
            icon.setImageResource(R.drawable.ic_cat);
        row.addView(icon);

        // Textos
        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textsParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textsParams.setMarginStart(dpToPx(12));
        texts.setLayoutParams(textsParams);

        TextView tvName = new TextView(this);
        tvName.setText(pet.getName());
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        texts.addView(tvName);

        TextView tvSub = new TextView(this);
        String species = "";
        try { species = Pet.PetSpecies.valueOf(pet.getSpecies()).getDisplayName(); }
        catch (Exception e) { species = pet.getSpecies(); }
        tvSub.setText(species);
        tvSub.setTextSize(11);
        tvSub.setTextColor(getColor(R.color.text_secondary_light));
        texts.addView(tvSub);
        row.addView(texts);

        // Botón eliminar
        ImageView btnDelete = new ImageView(this);
        btnDelete.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(20), dpToPx(20)));
        btnDelete.setImageResource(R.drawable.ic_trash);
        btnDelete.setColorFilter(getColor(R.color.alert_red));
        btnDelete.setClickable(true);
        btnDelete.setFocusable(true);
        btnDelete.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Eliminar mascota")
                        .setMessage("¿Seguro que quieres eliminar a " + pet.getName() + "?")
                        .setPositiveButton("Eliminar", (dialog, which) ->
                                db.collection("pets").document(pet.getId())
                                        .delete()
                                        .addOnSuccessListener(unused -> {
                                            containerPets.removeView(row);
                                            Toast.makeText(this, pet.getName() + " eliminado", Toast.LENGTH_SHORT).show();
                                        })
                        )
                        .setNegativeButton("Cancelar", null)
                        .show()
        );
        row.addView(btnDelete);

        return row;
    }

    private View buildDivider() {
        View divider = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1));
        params.setMarginStart(dpToPx(16));
        params.setMarginEnd(dpToPx(16));
        divider.setLayoutParams(params);
        divider.setBackgroundColor(getColor(R.color.border_light));
        return divider;
    }

    private void setupNotificationSwitches() {
        SwitchCompat swHealth = findViewById(R.id.sw_notifications_health);
        SwitchCompat swComm   = findViewById(R.id.sw_notifications_community);

        if (swHealth != null)
            swHealth.setOnCheckedChangeListener((btn, isChecked) ->
                    Toast.makeText(this, isChecked
                            ? "Notificaciones de salud activadas"
                            : "Notificaciones de salud desactivadas", Toast.LENGTH_SHORT).show());

        if (swComm != null)
            swComm.setOnCheckedChangeListener((btn, isChecked) ->
                    Toast.makeText(this, isChecked
                            ? "Notificaciones de comunidad activadas"
                            : "Notificaciones de comunidad desactivadas", Toast.LENGTH_SHORT).show());
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
        sys.setBackgroundResource(mode == ThemeManager.MODE_SYSTEM
                ? R.drawable.bg_radio_active : R.drawable.bg_radio_inactive);
        light.setBackgroundResource(mode == ThemeManager.MODE_LIGHT
                ? R.drawable.bg_radio_active : R.drawable.bg_radio_inactive);
        dark.setBackgroundResource(mode == ThemeManager.MODE_DARK
                ? R.drawable.bg_radio_active : R.drawable.bg_radio_inactive);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
