package com.boop.app.ui.alerts;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.R;
import com.boop.app.data.model.Pet;
import com.boop.app.data.model.PetAlert;
import com.boop.app.utils.ThemeManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddAlertActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String currentUserId;

    private TextView tvLabelDescription;
    private EditText etDescription, etDate, etTime;
    private LinearLayout containerPetSelector;

    private String selectedPetId   = null;
    private String selectedPetName = null;
    private int selectedType       = 0; // 0=VACCINE, 1=MED, 2=VET, 3=DEWORMING, 4=OTHER
    private String selectedRepeat  = PetAlert.RepeatMode.NONE.name();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alert);

        db            = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        tvLabelDescription  = findViewById(R.id.tv_label_description);
        etDescription       = findViewById(R.id.et_alert_description);
        etDate              = findViewById(R.id.et_alert_date);
        etTime              = findViewById(R.id.et_alert_time);
        containerPetSelector = findViewById(R.id.container_pet_selector);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // ── Tipos ────────────────────────────────────────────────────
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

        // ── Repetir ──────────────────────────────────────────────────
        TextView btnRepeatNone    = findViewById(R.id.btn_repeat_none);
        TextView btnRepeatDaily   = findViewById(R.id.btn_repeat_daily);
        TextView btnRepeatMonthly = findViewById(R.id.btn_repeat_monthly);
        TextView btnRepeatYearly  = findViewById(R.id.btn_repeat_yearly);

        View.OnClickListener repeatListener = v -> {
            resetRepeat(btnRepeatNone, btnRepeatDaily, btnRepeatMonthly, btnRepeatYearly);
            ((TextView) v).setBackgroundResource(R.drawable.bg_chip_active);
            ((TextView) v).setTextColor(getColor(R.color.boop_green_dark));

            int id = v.getId();
            if      (id == R.id.btn_repeat_daily)   selectedRepeat = PetAlert.RepeatMode.DAILY.name();
            else if (id == R.id.btn_repeat_monthly) selectedRepeat = PetAlert.RepeatMode.MONTHLY.name();
            else if (id == R.id.btn_repeat_yearly)  selectedRepeat = PetAlert.RepeatMode.ANNUAL.name();
            else                                    selectedRepeat = PetAlert.RepeatMode.NONE.name();
        };

        btnRepeatNone.setOnClickListener(repeatListener);
        btnRepeatDaily.setOnClickListener(repeatListener);
        btnRepeatMonthly.setOnClickListener(repeatListener);
        btnRepeatYearly.setOnClickListener(repeatListener);

        // ── Guardar ──────────────────────────────────────────────────
        findViewById(R.id.btn_save_alert).setOnClickListener(v -> saveAlert());

        // ── Cargar mascotas ──────────────────────────────────────────
        loadPets();
    }

    private void loadPets() {
        db.collection("pets")
                .whereEqualTo("ownerId", currentUserId)
                .get()
                .addOnSuccessListener(snapshots -> {
                    containerPetSelector.removeAllViews();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Pet pet = doc.toObject(Pet.class);
                        pet.setId(doc.getId());
                        containerPetSelector.addView(buildPetButton(pet));
                    }
                });
    }

    private View buildPetButton(Pet pet) {
        LinearLayout btn = new LinearLayout(this);
        btn.setOrientation(LinearLayout.VERTICAL);
        btn.setGravity(android.view.Gravity.CENTER);
        btn.setBackgroundResource(R.drawable.bg_species_btn_inactive);
        btn.setClickable(true);
        btn.setFocusable(true);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(72), dpToPx(64));
        params.setMarginEnd(dpToPx(8));
        btn.setLayoutParams(params);
        btn.setPadding(dpToPx(4), dpToPx(8), dpToPx(4), dpToPx(8));

        ImageView icon = new ImageView(this);
        icon.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(24), dpToPx(24)));
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        if (Pet.PetSpecies.DOG.name().equals(pet.getSpecies())) {
            icon.setImageResource(R.drawable.ic_dog);
        } else if (Pet.PetSpecies.RABBIT.name().equals(pet.getSpecies())) {
            icon.setImageResource(R.drawable.ic_rabbit);
        } else if (Pet.PetSpecies.BIRD.name().equals(pet.getSpecies())) {
            icon.setImageResource(R.drawable.ic_bird);
        } else {
            icon.setImageResource(R.drawable.ic_cat);
        }
        icon.setColorFilter(getColor(R.color.text_secondary_light));
        btn.addView(icon);

        TextView tvName = new TextView(this);
        tvName.setText(pet.getName());
        tvName.setTextSize(10);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        tvName.setTextColor(getColor(R.color.text_secondary_light));
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        nameParams.topMargin = dpToPx(4);
        tvName.setLayoutParams(nameParams);
        btn.addView(tvName);

        btn.setOnClickListener(v -> {
            // Reset visual de todos los botones
            for (int i = 0; i < containerPetSelector.getChildCount(); i++) {
                View child = containerPetSelector.getChildAt(i);
                child.setBackgroundResource(R.drawable.bg_species_btn_inactive);
                if (child instanceof LinearLayout) {
                    setTypeIconTint((LinearLayout) child, R.color.text_secondary_light);
                    setTypeTextColor((LinearLayout) child, R.color.text_secondary_light);
                }
            }
            // Activar el seleccionado
            btn.setBackgroundResource(R.drawable.bg_species_btn_active);
            icon.setColorFilter(getColor(R.color.boop_green));
            tvName.setTextColor(getColor(R.color.boop_green_dark));

            selectedPetId   = pet.getId();
            selectedPetName = pet.getName();
        });

        return btn;
    }

    private void saveAlert() {
        String description = etDescription.getText().toString().trim();
        String dateStr     = etDate.getText().toString().trim();

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("La descripción es obligatoria");
            etDescription.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(dateStr)) {
            etDate.setError("La fecha es obligatoria");
            etDate.requestFocus();
            return;
        }
        if (selectedPetId == null) {
            Toast.makeText(this, "Selecciona una mascota", Toast.LENGTH_SHORT).show();
            return;
        }

        // ── Parsear fecha y hora ──────────────────────────────────────
        String timeStr = etTime.getText().toString().trim();
        String pattern = TextUtils.isEmpty(timeStr) ? "dd/MM/yyyy" : "dd/MM/yyyy HH:mm";
        String fullStr = TextUtils.isEmpty(timeStr) ? dateStr : dateStr + " " + timeStr;

        Date dueDate;
        try {
            dueDate = new SimpleDateFormat(pattern, Locale.getDefault()).parse(fullStr);
        } catch (ParseException e) {
            etDate.setError("Formato de fecha inválido (DD/MM/AAAA)");
            return;
        }

        // ── Calcular urgencyLevel ─────────────────────────────────────
        long diff = dueDate.getTime() - System.currentTimeMillis();
        long days = diff / (1000 * 60 * 60 * 24);
        String urgency;
        if (days <= 0) {
            urgency = PetAlert.UrgencyLevel.URGENT.name();
        } else if (days <= 14) {
            urgency = PetAlert.UrgencyLevel.UPCOMING.name();
        } else {
            urgency = PetAlert.UrgencyLevel.SCHEDULED.name();
        }

        // ── Construir objeto ──────────────────────────────────────────
        PetAlert alert = new PetAlert();
        alert.setPetId(selectedPetId);
        alert.setTitle(description);
        alert.setType(getSelectedType());
        alert.setDueDate(dueDate);
        alert.setRepeatMode(selectedRepeat);
        alert.setEnabled(true);
        alert.setUrgencyLevel(urgency);
        alert.setCreatedBy(currentUserId);

        // ── Guardar en Firestore ──────────────────────────────────────
        db.collection("pets").document(selectedPetId)
                .collection("reminders")
                .add(alert)
                .addOnSuccessListener(ref -> {
                    ref.update("id", ref.getId());
                    Toast.makeText(this, "Recordatorio guardado 🐾", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar. Inténtalo de nuevo.",
                                Toast.LENGTH_SHORT).show());
    }

    private String getSelectedType() {
        switch (selectedType) {
            case 1:  return PetAlert.AlertType.MEDICATION.name();
            case 2:  return PetAlert.AlertType.VET_VISIT.name();
            case 3:  return PetAlert.AlertType.DEWORMING.name();
            case 4:  return PetAlert.AlertType.OTHER.name();
            default: return PetAlert.AlertType.VACCINE.name();
        }
    }

    private void updateFieldsByType(int viewId) {
        if (viewId == R.id.btn_type_vaccine) {
            selectedType = 0;
            tvLabelDescription.setText("Descripción");
            etDescription.setHint("Vacuna antirrábica");
        } else if (viewId == R.id.btn_type_med) {
            selectedType = 1;
            tvLabelDescription.setText("Descripción");
            etDescription.setHint("Nombre del medicamento");
        } else if (viewId == R.id.btn_type_vet) {
            selectedType = 2;
            tvLabelDescription.setText("Nombre veterinario");
            etDescription.setHint("Clínica Vetica / Dr. López");
        } else if (viewId == R.id.btn_type_deworming) {
            selectedType = 3;
            tvLabelDescription.setText("Descripción");
            etDescription.setHint("Pastilla desparasitante");
        } else {
            selectedType = 4;
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

    private void resetRepeat(TextView... buttons) {
        for (TextView btn : buttons) {
            btn.setBackgroundResource(R.drawable.bg_chip_inactive);
            btn.setTextColor(getColor(R.color.text_secondary_light));
        }
    }

    private void setTypeIconTint(LinearLayout layout, int colorRes) {
        View child = layout.getChildAt(0);
        if (child instanceof ImageView)
            ((ImageView) child).setColorFilter(getColor(colorRes));
    }

    private void setTypeTextColor(LinearLayout layout, int colorRes) {
        View child = layout.getChildAt(1);
        if (child instanceof TextView)
            ((TextView) child).setTextColor(getColor(colorRes));
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
