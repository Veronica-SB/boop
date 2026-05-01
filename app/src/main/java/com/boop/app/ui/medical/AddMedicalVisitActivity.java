package com.boop.app.ui.medical;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.R;
import com.boop.app.data.model.MedicalRecord;
import com.boop.app.utils.ThemeManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddMedicalVisitActivity extends AppCompatActivity {

    public static final String EXTRA_PET_ID   = "pet_id";
    public static final String EXTRA_PET_NAME = "pet_name";

    private FirebaseFirestore db;
    private String petId;
    private int selectedType = 0; // 0=VACCINE, 1=DEWORMING, 2=MEDICATION, 3=VET_VISIT, 4=OTHER

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_visit);

        db    = FirebaseFirestore.getInstance();
        petId = getIntent().getStringExtra(EXTRA_PET_ID);

        // Nombre de la mascota en el header
        String petName = getIntent().getStringExtra(EXTRA_PET_NAME);
        if (petName != null) {
            ((TextView) findViewById(R.id.tv_pet_name)).setText(petName);
        }

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_save_visit).setOnClickListener(v -> saveVisit());

        // ── Tipos ────────────────────────────────────────────────────
        LinearLayout btnVaccine    = findViewById(R.id.btn_type_vaccine);
        LinearLayout btnDeworming  = findViewById(R.id.btn_type_deworming);
        LinearLayout btnMedication = findViewById(R.id.btn_type_medication);
        LinearLayout btnVet        = findViewById(R.id.btn_type_vet);
        LinearLayout btnOther      = findViewById(R.id.btn_type_other);

        View.OnClickListener typeListener = v -> {
            resetTypes(btnVaccine, btnDeworming, btnMedication, btnVet, btnOther);
            v.setBackgroundResource(R.drawable.bg_species_btn_active);
            setTypeIconTint((LinearLayout) v, R.color.boop_green);
            setTypeTextColor((LinearLayout) v, R.color.boop_green_dark);

            int id = v.getId();
            if      (id == R.id.btn_type_vaccine)    selectedType = 0;
            else if (id == R.id.btn_type_deworming)  selectedType = 1;
            else if (id == R.id.btn_type_medication) selectedType = 2;
            else if (id == R.id.btn_type_vet)        selectedType = 3;
            else                                     selectedType = 4;
        };

        btnVaccine.setOnClickListener(typeListener);
        btnDeworming.setOnClickListener(typeListener);
        btnMedication.setOnClickListener(typeListener);
        btnVet.setOnClickListener(typeListener);
        btnOther.setOnClickListener(typeListener);
    }

    private void saveVisit() {
        String title    = getValue(R.id.et_visit_title);
        String dateStr  = getValue(R.id.et_visit_date);
        String nextStr  = getValue(R.id.et_next_date);
        String clinic   = getValue(R.id.et_clinic);
        String doctor   = getValue(R.id.et_doctor);
        String notes    = getValue(R.id.et_notes);

        if (TextUtils.isEmpty(title)) {
            ((android.widget.EditText) findViewById(R.id.et_visit_title))
                    .setError("El título es obligatorio");
            return;
        }
        if (TextUtils.isEmpty(dateStr)) {
            ((android.widget.EditText) findViewById(R.id.et_visit_date))
                    .setError("La fecha es obligatoria");
            return;
        }
        if (petId == null) {
            Toast.makeText(this, "Error: mascota no identificada", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        MedicalRecord record = new MedicalRecord();
        record.setPetId(petId);
        record.setType(getSelectedType());
        record.setTitle(title);
        record.setClinic(clinic);
        record.setDoctor(doctor);
        record.setNotes(notes);
        record.setCreatedBy(FirebaseAuth.getInstance().getCurrentUser().getUid());

        try {
            record.setDate(sdf.parse(dateStr));
        } catch (ParseException e) {
            ((android.widget.EditText) findViewById(R.id.et_visit_date))
                    .setError("Formato inválido (DD/MM/AAAA)");
            return;
        }

        if (!TextUtils.isEmpty(nextStr)) {
            try {
                record.setNextDate(sdf.parse(nextStr));
            } catch (ParseException e) {
                ((android.widget.EditText) findViewById(R.id.et_next_date))
                        .setError("Formato inválido (DD/MM/AAAA)");
                return;
            }
        }

        db.collection("pets").document(petId)
                .collection("medicalHistory")
                .add(record)
                .addOnSuccessListener(ref -> {
                    ref.update("id", ref.getId());
                    Toast.makeText(this, "Visita guardada 🐾", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar. Inténtalo de nuevo.",
                                Toast.LENGTH_SHORT).show());
    }

    private String getSelectedType() {
        switch (selectedType) {
            case 1:  return MedicalRecord.RecordType.DEWORMING.name();
            case 2:  return MedicalRecord.RecordType.MEDICATION.name();
            case 3:  return MedicalRecord.RecordType.VET_VISIT.name();
            case 4:  return MedicalRecord.RecordType.OTHER.name();
            default: return MedicalRecord.RecordType.VACCINE.name();
        }
    }

    private String getValue(int viewId) {
        android.widget.EditText et = findViewById(viewId);
        return et != null ? et.getText().toString().trim() : "";
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
        if (child instanceof ImageView)
            ((ImageView) child).setColorFilter(getColor(colorRes));
    }

    private void setTypeTextColor(LinearLayout layout, int colorRes) {
        View child = layout.getChildAt(1);
        if (child instanceof TextView)
            ((TextView) child).setTextColor(getColor(colorRes));
    }
}