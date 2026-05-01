package com.boop.app.ui.addpet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.HomeActivity;
import com.boop.app.R;
import com.boop.app.data.model.Pet;
import com.boop.app.utils.ThemeManager;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddPetActivity extends AppCompatActivity {

    public static final String EXTRA_PET_ID = "pet_id";

    // 0=Gato, 1=Perro, 2=Conejo, 3=Ave
    private int selectedSpecies = 0;
    private Uri selectedImageUri;
    private boolean vetManualMode = false;
    private String petId = null;
    private boolean isEditMode = false;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String currentUserId;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        db            = FirebaseFirestore.getInstance();
        storage       = FirebaseStorage.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // ── Modo edición ──────────────────────────────────────────────
        petId      = getIntent().getStringExtra(EXTRA_PET_ID);
        isEditMode = petId != null;

        if (isEditMode) {
            com.google.android.material.button.MaterialButton btnCreate =
                    findViewById(R.id.btn_create_pet);
            btnCreate.setText("Guardar cambios");
            loadPetData();
        }

        // ── Species buttons ───────────────────────────────────────────
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

        setupVetTabs();

        // ── Foto ──────────────────────────────────────────────────────
        findViewById(R.id.btn_photo_upload).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        // ── Back ──────────────────────────────────────────────────────
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // ── Guardar ───────────────────────────────────────────────────
        findViewById(R.id.btn_create_pet).setOnClickListener(v -> {
            if (validateForm()) savePet();
        });

        updateChipFieldRequirement();
    }

    // ── Cargar datos en modo edición ──────────────────────────────

    private void loadPetData() {
        db.collection("pets").document(petId).get()
                .addOnSuccessListener(doc -> {
                    Pet pet = doc.toObject(Pet.class);
                    if (pet == null) return;

                    setValue(R.id.et_pet_name,    pet.getName());
                    setValue(R.id.et_breed,       pet.getBreed());
                    setValue(R.id.et_weight,
                            pet.getWeightKg() != null ? String.valueOf(pet.getWeightKg()) : "");
                    setValue(R.id.et_chip,        pet.getChipNumber());
                    setValue(R.id.et_pet_license, pet.getLicenseNumber());
                    setValue(R.id.et_allergies,   pet.getAllergies());
                    setValue(R.id.et_vet_manual,  pet.getVetClinic());

                    if (pet.getSex() != null) {
                        if (Pet.PetSex.MALE.name().equals(pet.getSex()))
                            setValue(R.id.et_sex, "Macho");
                        else if (Pet.PetSex.FEMALE.name().equals(pet.getSex()))
                            setValue(R.id.et_sex, "Hembra");
                    }

                    if (pet.getBirthDate() != null) {
                        setValue(R.id.et_birthdate,
                                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        .format(pet.getBirthDate()));
                    }

                    // Seleccionar especie
                    LinearLayout btnCat    = findViewById(R.id.btn_species_cat);
                    LinearLayout btnDog    = findViewById(R.id.btn_species_dog);
                    LinearLayout btnRabbit = findViewById(R.id.btn_species_rabbit);
                    LinearLayout btnBird   = findViewById(R.id.btn_species_bird);
                    resetSpecies(btnCat, btnDog, btnRabbit, btnBird);

                    LinearLayout selectedBtn;
                    if (Pet.PetSpecies.DOG.name().equals(pet.getSpecies())) {
                        selectedSpecies = 1; selectedBtn = btnDog;
                    } else if (Pet.PetSpecies.RABBIT.name().equals(pet.getSpecies())) {
                        selectedSpecies = 2; selectedBtn = btnRabbit;
                    } else if (Pet.PetSpecies.BIRD.name().equals(pet.getSpecies())) {
                        selectedSpecies = 3; selectedBtn = btnBird;
                    } else {
                        selectedSpecies = 0; selectedBtn = btnCat;
                    }
                    selectedBtn.setBackgroundResource(R.drawable.bg_species_btn_active);
                    setSpeciesTextColor(selectedBtn, R.color.boop_green_dark);
                    setSpeciesIconTint(selectedBtn, R.color.boop_green);
                });
    }

    // ── Guardar mascota ───────────────────────────────────────────

    private void savePet() {
        String name        = getValue(R.id.et_pet_name);
        String breed       = getValue(R.id.et_breed);
        String sexInput    = getValue(R.id.et_sex);
        String birthInput  = getValue(R.id.et_birthdate);
        String weightInput = getValue(R.id.et_weight);
        String chip        = getValue(R.id.et_chip);
        String license     = getValue(R.id.et_pet_license);
        String allergies   = getValue(R.id.et_allergies);
        String vetClinic   = vetManualMode
                ? getValue(R.id.et_vet_manual)
                : getValue(R.id.et_vet_search);

        Pet pet = new Pet();
        pet.setOwnerId(currentUserId);
        pet.setName(name);
        pet.setSpecies(getSpeciesEnum());
        pet.setBreed(breed);
        pet.setSex(getSexEnum(sexInput));
        pet.setWeightKg(weightInput.isEmpty() ? null : Float.parseFloat(weightInput));
        pet.setChipNumber(chip);
        pet.setLicenseNumber(license);
        pet.setAllergies(allergies);
        pet.setVetClinic(vetClinic);

        if (!birthInput.isEmpty()) {
            try {
                pet.setBirthDate(new SimpleDateFormat("dd/MM/yyyy",
                        Locale.getDefault()).parse(birthInput));
            } catch (ParseException e) {
                pet.setBirthDate(null);
            }
        }

        if (selectedImageUri != null) {
            uploadPhotoAndSave(pet);
        } else {
            saveToFirestore(pet);
        }
    }

    private void uploadPhotoAndSave(Pet pet) {
        StorageReference photoRef = storage.getReference()
                .child("pets/" + currentUserId + "/" + System.currentTimeMillis() + ".jpg");

        photoRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot ->
                        photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            pet.setPhotoUrl(uri.toString());
                            saveToFirestore(pet);
                        })
                )
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al subir la foto", Toast.LENGTH_SHORT).show();
                    saveToFirestore(pet);
                });
    }

    private void saveToFirestore(Pet pet) {
        if (isEditMode) {
            db.collection("pets").document(petId)
                    .update(
                            "name",          pet.getName(),
                            "breed",         pet.getBreed(),
                            "sex",           pet.getSex(),
                            "birthDate",     pet.getBirthDate(),
                            "weightKg",      pet.getWeightKg(),
                            "chipNumber",    pet.getChipNumber(),
                            "licenseNumber", pet.getLicenseNumber(),
                            "allergies",     pet.getAllergies(),
                            "vetClinic",     pet.getVetClinic()
                    )
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Mascota actualizada 🐾", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al guardar. Inténtalo de nuevo.",
                                    Toast.LENGTH_SHORT).show());
        } else {
            db.collection("pets")
                    .add(pet)
                    .addOnSuccessListener(ref -> {
                        ref.update("id", ref.getId());
                        Toast.makeText(this, pet.getName() + " añadido/a 🐾", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al guardar. Inténtalo de nuevo.",
                                    Toast.LENGTH_SHORT).show());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────

    private String getValue(int viewId) {
        EditText et = findViewById(viewId);
        return et != null ? et.getText().toString().trim() : "";
    }

    private void setValue(int viewId, String value) {
        EditText et = findViewById(viewId);
        if (et != null && value != null) et.setText(value);
    }

    private String getSpeciesEnum() {
        switch (selectedSpecies) {
            case 1:  return Pet.PetSpecies.DOG.name();
            case 2:  return Pet.PetSpecies.RABBIT.name();
            case 3:  return Pet.PetSpecies.BIRD.name();
            default: return Pet.PetSpecies.CAT.name();
        }
    }

    private String getSexEnum(String input) {
        String lower = input.toLowerCase(Locale.getDefault());
        if (lower.contains("macho") || lower.contains("male"))    return Pet.PetSex.MALE.name();
        if (lower.contains("hembra") || lower.contains("female")) return Pet.PetSex.FEMALE.name();
        return Pet.PetSex.UNKNOWN.name();
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
        if (mark != null)    mark.setVisibility(required ? View.VISIBLE : View.GONE);
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
            if (child instanceof TextView)
                ((TextView) child).setTextColor(getColor(colorRes));
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
