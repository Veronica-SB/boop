package com.boop.app.ui.medical;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.boop.app.R;
import com.boop.app.data.model.Vaccine;
import com.boop.app.utils.ThemeManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class VaccinesActivity extends AppCompatActivity {

    public static final String EXTRA_PET_ID   = "pet_id";
    public static final String EXTRA_PET_NAME = "pet_name";

    private FirebaseFirestore db;
    private String petId;
    private LinearLayout containerVaccines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccines);

        db    = FirebaseFirestore.getInstance();
        petId = getIntent().getStringExtra(EXTRA_PET_ID);

        String petName = getIntent().getStringExtra(EXTRA_PET_NAME);
        if (petName != null)
            ((TextView) findViewById(R.id.tv_pet_name)).setText(petName);

        containerVaccines = findViewById(R.id.container_vaccines);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // TODO: conectar btn_register_vaccine con formulario de registro
        findViewById(R.id.btn_register_vaccine).setOnClickListener(v ->
                android.widget.Toast.makeText(this,
                        "Registro de vacunas (Próximamente)", android.widget.Toast.LENGTH_SHORT).show());

        loadVaccines();
    }

    private void loadVaccines() {
        if (petId == null) return;

        db.collection("pets").document(petId)
                .collection("vaccines")
                .get()
                .addOnSuccessListener(snapshots -> {
                    containerVaccines.removeAllViews();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Vaccine vaccine = doc.toObject(Vaccine.class);
                        vaccine.setId(doc.getId());
                        containerVaccines.addView(buildVaccineCard(vaccine));
                    }
                });
    }

    private View buildVaccineCard(Vaccine vaccine) {
        String status = vaccine.getStatus();

        // Colores y textos según estado
        int iconBg, iconTint, badgeBg, badgeTextColor;
        String badgeText;

        if (Vaccine.VaccineStatus.EXPIRED.name().equals(status)) {
            iconBg        = R.drawable.bg_circle_green_light;
            iconTint      = R.color.alert_red;
            badgeBg       = R.drawable.bg_pill_red;
            badgeTextColor = R.color.white;
            badgeText     = "Vencida";
        } else if (Vaccine.VaccineStatus.UPCOMING.name().equals(status)) {
            iconBg        = R.drawable.bg_ico_orange;
            iconTint      = R.color.alert_yellow;
            badgeBg       = R.drawable.bg_pill_yellow;
            badgeTextColor = R.color.alert_yellow_text;
            badgeText     = "Próxima";
        } else {
            iconBg        = R.drawable.bg_circle_green_light;
            iconTint      = R.color.boop_green;
            badgeBg       = R.drawable.bg_pill_green;
            badgeTextColor = R.color.boop_green_dark;
            badgeText     = "Al día ✓";
        }

        // CardView
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dpToPx(12);
        card.setLayoutParams(cardParams);
        card.setRadius(dpToPx(16));
        card.setCardElevation(dpToPx(2));
        card.setCardBackgroundColor(getColor(R.color.surface_light));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        card.addView(row);

        // Icono
        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(36), dpToPx(36));
        iconParams.setMarginEnd(dpToPx(12));
        icon.setLayoutParams(iconParams);
        icon.setBackgroundResource(iconBg);
        icon.setImageResource(R.drawable.ic_vaccine);
        icon.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        icon.setColorFilter(getColor(iconTint));
        row.addView(icon);

        // Textos
        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(this);
        tvName.setText(vaccine.getName());
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        tvName.setTextColor(getColor(R.color.text_primary_light));
        texts.addView(tvName);

        TextView tvDates = new TextView(this);
        tvDates.setText(getDatesLabel(vaccine));
        tvDates.setTextSize(10);
        tvDates.setTextColor(getColor(R.color.text_secondary_light));
        texts.addView(tvDates);
        row.addView(texts);

        // Badge
        TextView badge = new TextView(this);
        badge.setText(badgeText);
        badge.setTextSize(10);
        badge.setTypeface(null, android.graphics.Typeface.BOLD);
        badge.setTextColor(getColor(badgeTextColor));
        badge.setBackgroundResource(badgeBg);
        badge.setPadding(dpToPx(8), dpToPx(3), dpToPx(8), dpToPx(3));
        row.addView(badge);

        return card;
    }

    private String getDatesLabel(Vaccine vaccine) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        StringBuilder sb = new StringBuilder();
        if (vaccine.getAdministeredDate() != null)
            sb.append(sdf.format(vaccine.getAdministeredDate()));
        if (vaccine.getNextDoseDate() != null)
            sb.append(" · Próxima: ").append(sdf.format(vaccine.getNextDoseDate()));
        return sb.toString();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}