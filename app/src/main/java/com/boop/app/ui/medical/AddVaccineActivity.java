package com.boop.app.ui.medical;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.R;
import com.boop.app.data.model.Vaccine;
import com.boop.app.utils.ThemeManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddVaccineActivity extends AppCompatActivity {

    public static final String EXTRA_PET_ID   = "pet_id";
    public static final String EXTRA_PET_NAME = "pet_name";

    private FirebaseFirestore db;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vaccine);

        db    = FirebaseFirestore.getInstance();
        petId = getIntent().getStringExtra(EXTRA_PET_ID);

        String petName = getIntent().getStringExtra(EXTRA_PET_NAME);
        if (petName != null)
            ((TextView) findViewById(R.id.tv_pet_name)).setText(petName);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_save_vaccine).setOnClickListener(v -> saveVaccine());
    }

    private void saveVaccine() {
        String name     = getValue(R.id.et_vaccine_name);
        String dateStr  = getValue(R.id.et_administered_date);
        String nextStr  = getValue(R.id.et_next_dose_date);

        if (TextUtils.isEmpty(name)) {
            ((android.widget.EditText) findViewById(R.id.et_vaccine_name))
                    .setError("El nombre es obligatorio");
            return;
        }
        if (TextUtils.isEmpty(dateStr)) {
            ((android.widget.EditText) findViewById(R.id.et_administered_date))
                    .setError("La fecha es obligatoria");
            return;
        }
        if (petId == null) {
            Toast.makeText(this, "Error: mascota no identificada", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Vaccine vaccine = new Vaccine();
        vaccine.setPetId(petId);
        vaccine.setName(name);
        vaccine.setCreatedBy(FirebaseAuth.getInstance().getCurrentUser().getUid());

        try {
            Date administeredDate = sdf.parse(dateStr);
            vaccine.setAdministeredDate(administeredDate);

            // Calcular estado automáticamente
            if (!TextUtils.isEmpty(nextStr)) {
                Date nextDate = sdf.parse(nextStr);
                vaccine.setNextDoseDate(nextDate);

                long diff = nextDate.getTime() - System.currentTimeMillis();
                long days = diff / (1000 * 60 * 60 * 24);

                if (days < 0)
                    vaccine.setStatus(Vaccine.VaccineStatus.EXPIRED.name());
                else if (days <= 30)
                    vaccine.setStatus(Vaccine.VaccineStatus.UPCOMING.name());
                else
                    vaccine.setStatus(Vaccine.VaccineStatus.UP_TO_DATE.name());
            } else {
                vaccine.setStatus(Vaccine.VaccineStatus.UP_TO_DATE.name());
            }

        } catch (ParseException e) {
            ((android.widget.EditText) findViewById(R.id.et_administered_date))
                    .setError("Formato inválido (DD/MM/AAAA)");
            return;
        }

        db.collection("pets").document(petId)
                .collection("vaccines")
                .add(vaccine)
                .addOnSuccessListener(ref -> {
                    ref.update("id", ref.getId());
                    Toast.makeText(this, "Vacuna guardada 🐾", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar. Inténtalo de nuevo.",
                                Toast.LENGTH_SHORT).show());
    }

    private String getValue(int viewId) {
        android.widget.EditText et = findViewById(viewId);
        return et != null ? et.getText().toString().trim() : "";
    }
}
