package com.boop.app.ui.pet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.R;
import com.boop.app.data.model.Pet;
import com.boop.app.ui.addpet.AddPetActivity;
import com.boop.app.utils.ThemeManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class PetListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String currentUserId;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_list);

        db            = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        container     = findViewById(R.id.container_pet_list);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_add_pet).setOnClickListener(v ->
                startActivity(new Intent(this, AddPetActivity.class)));

        loadPets();
    }

    private void loadPets() {
        db.collection("pets")
                .whereEqualTo("ownerId", currentUserId)
                .get()
                .addOnSuccessListener(snapshots -> {
                    container.removeAllViews();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Pet pet = doc.toObject(Pet.class);
                        pet.setId(doc.getId());
                        container.addView(buildPetRow(pet));
                    }
                });
    }

    private View buildPetRow(Pet pet) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setBackgroundResource(R.drawable.bg_auth_card);
        row.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        row.setClickable(true);
        row.setFocusable(true);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dpToPx(12);
        row.setLayoutParams(params);

        // Icono
        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(40), dpToPx(40));
        iconParams.setMarginEnd(dpToPx(16));
        icon.setLayoutParams(iconParams);
        icon.setBackgroundResource(R.drawable.bg_circle_green_light);
        icon.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
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
        row.addView(icon);

        // Nombre y especie
        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(this);
        tvName.setText(pet.getName());
        tvName.setTextSize(15);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        tvName.setTextColor(getColor(R.color.text_primary_light));
        texts.addView(tvName);

        TextView tvSpecies = new TextView(this);
        try {
            tvSpecies.setText(Pet.PetSpecies.valueOf(pet.getSpecies()).getDisplayName());
        } catch (Exception e) {
            tvSpecies.setText(pet.getSpecies());
        }
        tvSpecies.setTextSize(12);
        tvSpecies.setTextColor(getColor(R.color.text_secondary_light));
        texts.addView(tvSpecies);
        row.addView(texts);

        // Flecha
        TextView arrow = new TextView(this);
        arrow.setText("›");
        arrow.setTextSize(20);
        arrow.setTextColor(getColor(R.color.text_tertiary_light));
        row.addView(arrow);

        // Click → abrir perfil
        row.setOnClickListener(v -> {
            Intent intent = new Intent(this, PetProfileActivity.class);
            intent.putExtra(PetProfileActivity.EXTRA_PET_ID, pet.getId());
            startActivity(intent);
        });

        return row;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
