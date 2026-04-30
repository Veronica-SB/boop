package com.boop.app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.boop.app.R;
import com.boop.app.data.model.Pet;
import com.boop.app.data.model.PetAlert;
import com.boop.app.ui.addpet.AddPetActivity;
import com.boop.app.ui.pet.PetListActivity;
import com.boop.app.ui.pet.PetProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Saludo con nombre del usuario

        String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        TextView tvGreeting = view.findViewById(R.id.tv_greeting);

        if (displayName != null && !displayName.isEmpty()) {
            tvGreeting.setText("Hola, " + displayName + " 👋");
        } else {
            db.collection("users").document(currentUserId).get()
                    .addOnSuccessListener(doc -> {
                        String name = doc.getString("name");
                        tvGreeting.setText("Hola, " +
                                (name != null && !name.isEmpty() ? name : " 👋"));
                    });
        }


        // Campana de notificaciones -> Recordatorios
        view.findViewById(R.id.btn_notifications).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.alertsFragment);
        });

        // "Ver todas" las alertas -> Recordatorios
        view.findViewById(R.id.tv_see_all_alerts).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.alertsFragment);
        });

        // "Ver todas" las mascotas
        view.findViewById(R.id.tv_see_all_pets).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), PetListActivity.class)));


        // Botón añadir mascota
        view.findViewById(R.id.btn_add_pet).setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddPetActivity.class));
        });

        // "Ver mapa" -> Ir al fragmento de mapa
        view.findViewById(R.id.tv_see_map).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.mapFragment);
        });
        loadPets(view);
        loadReminders(view);
    }
    //Mascotas
    private void loadPets(View view) {
        LinearLayout petsContainer = view.findViewById(R.id.pets_container);

        db.collection("pets")
                .whereEqualTo("ownerId", currentUserId)
                .get()
                .addOnSuccessListener(snapshots -> {
                    // Elimina las cards hardcodeadas del XML, dejando solo el botón añadir
                    petsContainer.removeAllViews();

                    for (QueryDocumentSnapshot doc : snapshots) {
                        Pet pet = doc.toObject(Pet.class);
                        pet.setId(doc.getId());
                        petsContainer.addView(buildPetCard(pet),
                                petsContainer.getChildCount());
                    }

                    // El botón "Añadir" siempre al final
                    View btnAdd = buildAddPetButton();
                    petsContainer.addView(btnAdd);
                    btnAdd.setOnClickListener(v ->
                            startActivity(new Intent(requireContext(), AddPetActivity.class)));
                });
    }

    private View buildPetCard(Pet pet) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(android.view.Gravity.CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(dpToPx(16));
        card.setLayoutParams(params);

        // Icono según especie
        ImageView icon = new ImageView(requireContext());
        int size = dpToPx(72);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(size, size);
        icon.setLayoutParams(iconParams);
        icon.setBackgroundResource(R.drawable.bg_circle_green_light);
        icon.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
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
        card.addView(icon);

        // Nombre
        TextView tvName = new TextView(requireContext());
        tvName.setText(pet.getName());
        tvName.setTextSize(12);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        tvName.setTextColor(requireContext().getColor(R.color.text_primary_light));
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        nameParams.topMargin = dpToPx(6);
        tvName.setLayoutParams(nameParams);
        card.addView(tvName);

        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PetProfileActivity.class);
            intent.putExtra(PetProfileActivity.EXTRA_PET_ID, pet.getId());
            startActivity(intent);
        });

        return card;
    }

    private View buildAddPetButton() {
        LinearLayout btn = new LinearLayout(requireContext());
        btn.setOrientation(LinearLayout.VERTICAL);
        btn.setGravity(android.view.Gravity.CENTER);
        btn.setClickable(true);
        btn.setFocusable(true);

        ImageView icon = new ImageView(requireContext());
        int size = dpToPx(72);
        icon.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        icon.setBackgroundResource(R.drawable.bg_circle_dashed_green);
        icon.setImageResource(R.drawable.ic_plus);
        icon.setPadding(dpToPx(22), dpToPx(22), dpToPx(22), dpToPx(22));
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setColorFilter(requireContext().getColor(R.color.boop_green));
        btn.addView(icon);

        TextView label = new TextView(requireContext());
        label.setText("Añadir");
        label.setTextSize(12);
        label.setTextColor(requireContext().getColor(R.color.text_secondary_light));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dpToPx(6);
        label.setLayoutParams(lp);
        btn.addView(label);

        return btn;
    }

    // ── Recordatorios ─────────────────────────────────────────────────

    private void loadReminders(View view) {
        LinearLayout alertsContainer = view.findViewById(R.id.alerts_container);
        if (alertsContainer == null) return;

        alertsContainer.removeAllViews();

        db.collection("pets")
                .whereEqualTo("ownerId", currentUserId)
                .get()
                .addOnSuccessListener(pets -> {
                    for (QueryDocumentSnapshot petDoc : pets) {
                        String petName = petDoc.getString("name");
                        String petId   = petDoc.getId();

                        db.collection("pets").document(petId)
                                .collection("reminders")
                                .whereEqualTo("isEnabled", true)
                                .get()
                                .addOnSuccessListener(reminders -> {
                                    for (QueryDocumentSnapshot reminderDoc : reminders) {
                                        PetAlert alert = reminderDoc.toObject(PetAlert.class);
                                        alert.setId(reminderDoc.getId());
                                        alertsContainer.addView(buildAlertCard(alert, petName));
                                    }
                                });
                    }
                });
    }

    private View buildAlertCard(PetAlert alert, String petName) {
        String urgency = alert.getUrgencyLevel();

        // Colores según urgencia
        int bgColor, barColor;
        String badgeText;
        int badgeTextColor;
        int badgeBgRes;

        if (PetAlert.UrgencyLevel.URGENT.name().equals(urgency)) {
            bgColor       = requireContext().getColor(R.color.alert_red_bg);
            barColor      = requireContext().getColor(R.color.alert_red);
            badgeText     = "🚨 Urgente";
            badgeTextColor = requireContext().getColor(R.color.alert_red);
            badgeBgRes    = R.drawable.bg_pill_red;
        } else if (PetAlert.UrgencyLevel.UPCOMING.name().equals(urgency)) {
            bgColor       = requireContext().getColor(R.color.alert_yellow_bg);
            barColor      = requireContext().getColor(R.color.alert_yellow);
            badgeText     = "⏰ Próxima";
            badgeTextColor = requireContext().getColor(R.color.alert_yellow_text);
            badgeBgRes    = R.drawable.bg_pill_yellow;
        } else {
            bgColor       = requireContext().getColor(R.color.boop_green_light);
            barColor      = requireContext().getColor(R.color.boop_green);
            badgeText     = "✅ Al día";
            badgeTextColor = requireContext().getColor(R.color.boop_green_dark);
            badgeBgRes    = R.drawable.bg_pill_green;
        }

        // CardView
        CardView card = new CardView(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = dpToPx(24);
        int marginBottom = dpToPx(10);
        cardParams.setMarginStart(margin);
        cardParams.setMarginEnd(margin);
        cardParams.bottomMargin = marginBottom;
        card.setLayoutParams(cardParams);
        card.setRadius(dpToPx(12));
        card.setCardElevation(0);
        card.setCardBackgroundColor(bgColor);

        // Fila interior
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        int pad = dpToPx(16);
        row.setPadding(pad, pad, pad, pad);
        card.addView(row);

        // Barra lateral de color
        View bar = new View(requireContext());
        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(dpToPx(4), dpToPx(44));
        barParams.setMarginEnd(dpToPx(12));
        bar.setLayoutParams(barParams);
        bar.setBackgroundColor(barColor);
        row.addView(bar);

        // Textos
        LinearLayout texts = new LinearLayout(requireContext());
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textsParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        texts.setLayoutParams(textsParams);

        TextView tvTitle = new TextView(requireContext());
        tvTitle.setText(alert.getTitle());
        tvTitle.setTextSize(14);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextColor(requireContext().getColor(R.color.text_primary_light));
        texts.addView(tvTitle);

        TextView tvSub = new TextView(requireContext());
        tvSub.setText(petName + " · " + getDueDateLabel(alert.getDueDate()));
        tvSub.setTextSize(12);
        tvSub.setTextColor(requireContext().getColor(R.color.text_secondary_light));
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        subParams.topMargin = dpToPx(2);
        tvSub.setLayoutParams(subParams);
        texts.addView(tvSub);
        row.addView(texts);

        // Badge
        TextView badge = new TextView(requireContext());
        badge.setText(badgeText);
        badge.setTextSize(11);
        badge.setTypeface(null, android.graphics.Typeface.BOLD);
        badge.setTextColor(badgeTextColor);
        badge.setBackgroundResource(badgeBgRes);
        badge.setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4));
        row.addView(badge);

        return card;
    }

    private String getDueDateLabel(Date dueDate) {
        if (dueDate == null) return "";
        long diff = dueDate.getTime() - System.currentTimeMillis();
        long days = diff / (1000 * 60 * 60 * 24);
        if (days < 0)      return "Vencida";
        if (days == 0)     return "Vence hoy";
        if (days == 1)     return "Vence mañana";
        if (days < 7)      return "En " + days + " días";
        if (days < 30)     return "En " + (days / 7) + " semanas";
        return "En " + (days / 30) + " meses";
    }

    private int dpToPx(int dp) {
        return Math.round(dp * requireContext().getResources().getDisplayMetrics().density);
    }
}
