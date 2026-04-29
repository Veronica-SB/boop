package com.boop.app.ui.pet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.boop.app.R;
import com.boop.app.data.model.MedicalRecord;
import com.boop.app.data.model.Pet;
import com.boop.app.data.model.Vaccine;
import com.boop.app.ui.medical.AddMedicalVisitActivity;
import com.boop.app.ui.medical.VaccinesActivity;
import com.boop.app.ui.settings.GuardiansActivity;
import com.boop.app.utils.ThemeManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PetProfileActivity extends AppCompatActivity {

    public static final String EXTRA_PET_ID = "pet_id";

    private FirebaseFirestore db;
    private String petId;
    private Pet currentPet;

    private TextView tvTitle, tvName, tvSubtitle;
    private TextView tvChipNumber, tvWeight, tvVetClinic, tvLicense;
    private TextView pillChip, pillAllergies;
    private ImageView ivPetPhoto;
    private LinearLayout containerGuardians, containerVaccines, containerMedical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_profile);

        db    = FirebaseFirestore.getInstance();
        petId = getIntent().getStringExtra(EXTRA_PET_ID);

        tvTitle           = findViewById(R.id.tv_pet_profile_title);
        tvName            = findViewById(R.id.tv_pet_name);
        tvSubtitle        = findViewById(R.id.tv_pet_subtitle);
        tvChipNumber      = findViewById(R.id.tv_chip_number);
        tvWeight          = findViewById(R.id.tv_weight);
        tvVetClinic       = findViewById(R.id.tv_vet_clinic);
        tvLicense         = findViewById(R.id.tv_license);
        pillChip          = findViewById(R.id.pill_chip);
        pillAllergies     = findViewById(R.id.pill_allergies);
        ivPetPhoto        = findViewById(R.id.iv_pet_photo);
        containerGuardians = findViewById(R.id.container_guardians_profile);
        containerVaccines  = findViewById(R.id.container_vaccines_preview);
        containerMedical   = findViewById(R.id.container_medical_history);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_guardians).setOnClickListener(v -> {
            Intent intent = new Intent(this, GuardiansActivity.class);
            intent.putExtra(GuardiansActivity.EXTRA_PET_ID, petId);
            intent.putExtra(GuardiansActivity.EXTRA_PET_NAME,
                    currentPet != null ? currentPet.getName() : "");
            startActivity(intent);
        });

        findViewById(R.id.btn_add_medical).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMedicalVisitActivity.class);
            intent.putExtra(AddMedicalVisitActivity.EXTRA_PET_ID, petId);
            intent.putExtra(AddMedicalVisitActivity.EXTRA_PET_NAME,
                    currentPet != null ? currentPet.getName() : "");
            startActivity(intent);
        });

        findViewById(R.id.btn_see_vaccines).setOnClickListener(v -> {
            Intent intent = new Intent(this, VaccinesActivity.class);
            intent.putExtra(VaccinesActivity.EXTRA_PET_ID, petId);
            intent.putExtra(VaccinesActivity.EXTRA_PET_NAME,
                    currentPet != null ? currentPet.getName() : "");
            startActivity(intent);
        });

        loadPet();
        loadGuardians();
        loadVaccinesPreview();
        loadMedicalHistory();
    }

    // ── Datos básicos ─────────────────────────────────────────────

    private void loadPet() {
        db.collection("pets").document(petId).get()
                .addOnSuccessListener(doc -> {
                    Pet pet = doc.toObject(Pet.class);
                    if (pet == null) return;
                    pet.setId(doc.getId());
                    bindPet(pet);
                });
    }

    private void bindPet(Pet pet) {
        currentPet = pet;
        tvTitle.setText("Perfil de " + pet.getName());
        tvName.setText(pet.getName());

        String species = getSpeciesDisplay(pet.getSpecies());
        String sex     = getSexDisplay(pet.getSex());
        String age     = getAgeDisplay(pet.getBirthDate());
        tvSubtitle.setText(species + " · " + sex + " · " + age);

        if (Pet.PetSpecies.DOG.name().equals(pet.getSpecies()))
            ivPetPhoto.setImageResource(R.drawable.ic_dog);
        else if (Pet.PetSpecies.RABBIT.name().equals(pet.getSpecies()))
            ivPetPhoto.setImageResource(R.drawable.ic_rabbit);
        else if (Pet.PetSpecies.BIRD.name().equals(pet.getSpecies()))
            ivPetPhoto.setImageResource(R.drawable.ic_bird);
        else
            ivPetPhoto.setImageResource(R.drawable.ic_cat);

        tvChipNumber.setText(pet.getChipNumber() != null && !pet.getChipNumber().isEmpty()
                ? pet.getChipNumber() : "—");
        tvWeight.setText(pet.getWeightKg() != null ? pet.getWeightKg() + " kg" : "—");
        tvVetClinic.setText(pet.getVetClinic() != null && !pet.getVetClinic().isEmpty()
                ? pet.getVetClinic() : "—");
        tvLicense.setText(pet.getLicenseNumber() != null && !pet.getLicenseNumber().isEmpty()
                ? pet.getLicenseNumber() : "—");

        if (pet.getChipNumber() != null && !pet.getChipNumber().isEmpty())
            pillChip.setVisibility(View.VISIBLE);

        if (pet.getAllergies() != null && !pet.getAllergies().isEmpty()) {
            pillAllergies.setVisibility(View.VISIBLE);
            pillAllergies.setText(pet.getAllergies());
        } else {
            pillAllergies.setVisibility(View.VISIBLE);
            pillAllergies.setText("Sin alergias");
        }
    }

    // ── Cuidadores ────────────────────────────────────────────────

    private void loadGuardians() {
        db.collection("pets").document(petId)
                .collection("guardians")
                .get()
                .addOnSuccessListener(snapshots -> {
                    containerGuardians.removeAllViews();
                    if (snapshots.isEmpty()) return;
                    for (QueryDocumentSnapshot doc : snapshots) {
                        String guardianId = doc.getId();
                        db.collection("users").document(guardianId).get()
                                .addOnSuccessListener(userDoc -> {
                                    String name  = userDoc.getString("name");
                                    String email = userDoc.getString("email");
                                    containerGuardians.addView(buildSimpleRow(
                                            "👤 " + (name != null ? name : "Cuidador"),
                                            email != null ? email : ""
                                    ));
                                });
                    }
                });
    }

    // ── Vacunas (preview — últimas 3) ─────────────────────────────

    private void loadVaccinesPreview() {
        db.collection("pets").document(petId)
                .collection("vaccines")
                .limit(3)
                .get()
                .addOnSuccessListener(snapshots -> {
                    containerVaccines.removeAllViews();
                    if (snapshots.isEmpty()) return;
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Vaccine vaccine = doc.toObject(Vaccine.class);
                        vaccine.setId(doc.getId());
                        String status = vaccine.getStatus();
                        String badge = Vaccine.VaccineStatus.EXPIRED.name().equals(status) ? "Vencida"
                                : Vaccine.VaccineStatus.UPCOMING.name().equals(status) ? "Próxima" : "Al día ✓";
                        containerVaccines.addView(buildSimpleRow(
                                vaccine.getName(),
                                badge
                        ));
                    }
                });
    }

    // ── Historial médico (últimos 3) ──────────────────────────────

    private void loadMedicalHistory() {
        db.collection("pets").document(petId)
                .collection("medicalHistory")
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener(snapshots -> {
                    containerMedical.removeAllViews();
                    if (snapshots.isEmpty()) return;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    for (QueryDocumentSnapshot doc : snapshots) {
                        MedicalRecord record = doc.toObject(MedicalRecord.class);
                        record.setId(doc.getId());
                        String dateStr = record.getDate() != null
                                ? sdf.format(record.getDate()) : "";
                        String clinic = record.getClinic() != null
                                ? record.getClinic() : "";
                        containerMedical.addView(buildSimpleRow(
                                record.getTitle(),
                                dateStr + (clinic.isEmpty() ? "" : " · " + clinic)
                        ));
                    }
                });
    }

    // ── Helper: fila simple con título y subtítulo ────────────────

    private View buildSimpleRow(String title, String subtitle) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dpToPx(8);
        card.setLayoutParams(cardParams);
        card.setRadius(dpToPx(12));
        card.setCardElevation(dpToPx(1));
        card.setCardBackgroundColor(getColor(R.color.surface_light));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));
        card.addView(row);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextColor(getColor(R.color.text_primary_light));
        row.addView(tvTitle);

        if (subtitle != null && !subtitle.isEmpty()) {
            TextView tvSub = new TextView(this);
            tvSub.setText(subtitle);
            tvSub.setTextSize(11);
            tvSub.setTextColor(getColor(R.color.text_secondary_light));
            row.addView(tvSub);
        }

        return card;
    }

    // ── Helpers ───────────────────────────────────────────────────

    private String getSpeciesDisplay(String species) {
        if (species == null) return "—";
        try { return Pet.PetSpecies.valueOf(species).getDisplayName(); }
        catch (IllegalArgumentException e) { return species; }
    }

    private String getSexDisplay(String sex) {
        if (sex == null) return "—";
        try { return Pet.PetSex.valueOf(sex).getDisplayName(); }
        catch (IllegalArgumentException e) { return sex; }
    }

    private String getAgeDisplay(java.util.Date birthDate) {
        if (birthDate == null) return "—";
        long diff  = System.currentTimeMillis() - birthDate.getTime();
        long years = diff / (1000L * 60 * 60 * 24 * 365);
        if (years < 1) {
            long months = diff / (1000L * 60 * 60 * 24 * 30);
            return months + " meses";
        }
        return years + " años";
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}