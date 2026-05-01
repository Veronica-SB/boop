package com.boop.app.ui.alerts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.boop.app.R;
import com.boop.app.data.model.PetAlert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.content.Intent;

import java.util.Date;

public class AlertsFragment extends Fragment {

    private FirebaseFirestore db;
    private String currentUserId;

    private LinearLayout containerUrgent, containerUpcoming, containerScheduled;
    private TextView tvSectionUrgent, tvSectionUpcoming, tvSectionScheduled;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alerts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db            = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        containerUrgent    = view.findViewById(R.id.container_urgent);
        containerUpcoming  = view.findViewById(R.id.container_upcoming);
        containerScheduled = view.findViewById(R.id.container_scheduled);
        tvSectionUrgent    = view.findViewById(R.id.tv_section_urgent);
        tvSectionUpcoming  = view.findViewById(R.id.tv_section_upcoming);
        tvSectionScheduled = view.findViewById(R.id.tv_section_scheduled);

        // ── Botones nueva alerta ─────────────────────────────────────────
        View.OnClickListener addListener = v ->
                startActivity(new Intent(requireContext(), AddAlertActivity.class));
        view.findViewById(R.id.btn_new_alert).setOnClickListener(addListener);
        view.findViewById(R.id.btn_create_alert).setOnClickListener(addListener);

        loadReminders();
    }

    private void loadReminders() {
        containerUrgent.removeAllViews();
        containerUpcoming.removeAllViews();
        containerScheduled.removeAllViews();

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

                                        String ownerId = petDoc.getString("ownerId");
                                        View card = buildAlertCard(alert, petName, petId, ownerId);
                                        String urgency = alert.getUrgencyLevel();

                                        if (PetAlert.UrgencyLevel.URGENT.name().equals(urgency)) {
                                            containerUrgent.addView(card);
                                            tvSectionUrgent.setVisibility(View.VISIBLE);
                                        } else if (PetAlert.UrgencyLevel.UPCOMING.name().equals(urgency)) {
                                            containerUpcoming.addView(card);
                                            tvSectionUpcoming.setVisibility(View.VISIBLE);
                                        } else {
                                            containerScheduled.addView(card);
                                            tvSectionScheduled.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                    }
                });
    }

    private View buildAlertCard(PetAlert alert, String petName, String petId, String ownerId) {
        String urgency = alert.getUrgencyLevel();
        boolean isDone = alert.getConfirmedBy() != null;

        // Colores según urgencia
        int barColor;
        String badgeText;
        int badgeTextColor;
        int badgeBgRes;

        if (PetAlert.UrgencyLevel.URGENT.name().equals(urgency)) {
            barColor       = requireContext().getColor(R.color.alert_red);
            badgeText      = "🔴 Urgente";
            badgeTextColor = requireContext().getColor(R.color.alert_red);
            badgeBgRes     = R.drawable.bg_pill_red_light;
        } else if (PetAlert.UrgencyLevel.UPCOMING.name().equals(urgency)) {
            barColor       = requireContext().getColor(R.color.alert_yellow);
            badgeText      = "⏰ Próxima";
            badgeTextColor = requireContext().getColor(R.color.alert_yellow_text);
            badgeBgRes     = R.drawable.bg_pill_yellow_light;
        } else {
            barColor       = requireContext().getColor(R.color.boop_green);
            badgeText      = isDone ? "✅ Hecho" : "✅ Al día";
            badgeTextColor = requireContext().getColor(R.color.boop_green_dark);
            badgeBgRes     = R.drawable.bg_pill_green_light;
        }

        // ── CardView ──────────────────────────────────────────────────
        CardView card = new CardView(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dpToPx(10);
        card.setLayoutParams(cardParams);
        card.setRadius(dpToPx(12));
        card.setCardElevation(0);
        card.setCardBackgroundColor(requireContext().getColor(R.color.surface_light));

        LinearLayout cardContent = new LinearLayout(requireContext());
        cardContent.setOrientation(LinearLayout.VERTICAL);
        card.addView(cardContent);

        // ── Fila principal ────────────────────────────────────────────
        LinearLayout mainRow = new LinearLayout(requireContext());
        mainRow.setOrientation(LinearLayout.HORIZONTAL);
        mainRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        mainRow.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        cardContent.addView(mainRow);

        // Barra lateral
        View bar = new View(requireContext());
        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(dpToPx(4), dpToPx(50));
        barParams.setMarginEnd(dpToPx(12));
        bar.setLayoutParams(barParams);
        bar.setBackgroundColor(barColor);
        mainRow.addView(bar);

        // Textos
        LinearLayout texts = new LinearLayout(requireContext());
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvTitle = new TextView(requireContext());
        tvTitle.setText(alert.getTitle());
        tvTitle.setTextSize(14);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setTextColor(requireContext().getColor(R.color.text_primary_light));
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = dpToPx(4);
        tvTitle.setLayoutParams(titleParams);
        texts.addView(tvTitle);

        // Subfila: nombre mascota + badge
        LinearLayout subRow = new LinearLayout(requireContext());
        subRow.setOrientation(LinearLayout.HORIZONTAL);
        subRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        subRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView tvSub = new TextView(requireContext());
        tvSub.setText(petName + " · " + getDueDateLabel(alert.getDueDate()));
        tvSub.setTextSize(11);
        tvSub.setTextColor(requireContext().getColor(R.color.text_secondary_light));
        tvSub.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        subRow.addView(tvSub);

        TextView badge = new TextView(requireContext());
        badge.setText(badgeText);
        badge.setTextSize(10);
        badge.setTypeface(null, android.graphics.Typeface.BOLD);
        badge.setTextColor(badgeTextColor);
        badge.setBackgroundResource(badgeBgRes);
        badge.setPadding(dpToPx(8), dpToPx(3), dpToPx(8), dpToPx(3));
        subRow.addView(badge);

        texts.addView(subRow);
        mainRow.addView(texts);

        // ── Franja inferior: check + eliminar ─────────────────────────
        LinearLayout bottomRow = new LinearLayout(requireContext());
        bottomRow.setOrientation(LinearLayout.HORIZONTAL);
        bottomRow.setBackgroundColor(isDone
                ? requireContext().getColor(R.color.boop_green_light)
                : requireContext().getColor(R.color.background_light));
        bottomRow.setPadding(dpToPx(16), dpToPx(10), dpToPx(16), dpToPx(10));
        cardContent.addView(bottomRow);

        // Botón check
        LinearLayout btnCheck = new LinearLayout(requireContext());
        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                0, dpToPx(36), 1f);
        checkParams.setMarginEnd(dpToPx(8));
        btnCheck.setLayoutParams(checkParams);
        btnCheck.setOrientation(LinearLayout.HORIZONTAL);
        btnCheck.setGravity(android.view.Gravity.CENTER_VERTICAL);
        btnCheck.setBackgroundResource(isDone
                ? R.drawable.bg_species_btn_active
                : R.drawable.bg_species_btn_inactive);
        btnCheck.setPadding(dpToPx(12), 0, dpToPx(12), 0);
        btnCheck.setClickable(true);
        btnCheck.setFocusable(true);

        FrameLayout checkbox = new FrameLayout(requireContext());
        LinearLayout.LayoutParams cbParams = new LinearLayout.LayoutParams(dpToPx(20), dpToPx(20));
        cbParams.setMarginEnd(dpToPx(8));
        checkbox.setLayoutParams(cbParams);
        checkbox.setBackgroundResource(isDone
                ? R.drawable.bg_check_done
                : R.drawable.bg_check_undone);

        ImageView checkIcon = new ImageView(requireContext());
        FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(dpToPx(14), dpToPx(14));
        iconParams.gravity = android.view.Gravity.CENTER;
        checkIcon.setLayoutParams(iconParams);
        checkIcon.setImageResource(R.drawable.ic_check);
        checkIcon.setVisibility(isDone ? View.VISIBLE : View.GONE);
        checkbox.addView(checkIcon);
        btnCheck.addView(checkbox);

        TextView tvCheckLabel = new TextView(requireContext());
        tvCheckLabel.setText(isDone ? "¡Marcado como hecho!" : "¿Ya lo has hecho?");
        tvCheckLabel.setTextSize(12);
        tvCheckLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        tvCheckLabel.setTextColor(isDone
                ? requireContext().getColor(R.color.boop_green_dark)
                : requireContext().getColor(R.color.text_secondary_light));
        btnCheck.addView(tvCheckLabel);
        bottomRow.addView(btnCheck);

        // Estado del check (toggle)
        btnCheck.setTag(isDone);
        btnCheck.setOnClickListener(v -> {
            boolean wasDone = (boolean) v.getTag();
            boolean nowDone = !wasDone;
            v.setTag(nowDone);

            checkbox.setBackgroundResource(nowDone
                    ? R.drawable.bg_check_done : R.drawable.bg_check_undone);
            checkIcon.setVisibility(nowDone ? View.VISIBLE : View.GONE);
            btnCheck.setBackgroundResource(nowDone
                    ? R.drawable.bg_species_btn_active : R.drawable.bg_species_btn_inactive);
            tvCheckLabel.setText(nowDone ? "¡Marcado como hecho!" : "¿Ya lo has hecho?");
            tvCheckLabel.setTextColor(nowDone
                    ? requireContext().getColor(R.color.boop_green_dark)
                    : requireContext().getColor(R.color.text_secondary_light));
            bottomRow.setBackgroundColor(nowDone
                    ? requireContext().getColor(R.color.boop_green_light)
                    : requireContext().getColor(R.color.background_light));

            // Guardar en Firestore
            db.collection("pets").document(petId)
                    .collection("reminders").document(alert.getId())
                    .update(
                            "confirmedBy", nowDone ? currentUserId : null,
                            "confirmedAt", nowDone ? new Date() : null
                    );
        });

        // Botón eliminar
        FrameLayout btnDelete = new FrameLayout(requireContext());
        btnDelete.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(36), dpToPx(36)));
        btnDelete.setBackgroundResource(R.drawable.bg_ico_red);
        btnDelete.setClickable(true);
        btnDelete.setFocusable(true);
        // Solo el owner puede borrar
        if (!currentUserId.equals(ownerId)) {
            btnDelete.setVisibility(View.GONE);
        }

        ImageView trashIcon = new ImageView(requireContext());
        FrameLayout.LayoutParams trashParams = new FrameLayout.LayoutParams(dpToPx(18), dpToPx(18));
        trashParams.gravity = android.view.Gravity.CENTER;
        trashIcon.setLayoutParams(trashParams);
        trashIcon.setImageResource(R.drawable.ic_trash);
        trashIcon.setColorFilter(requireContext().getColor(R.color.alert_red));
        btnDelete.addView(trashIcon);
        bottomRow.addView(btnDelete);

        btnDelete.setOnClickListener(v ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Eliminar recordatorio")
                        .setMessage("¿Seguro que quieres eliminar \"" + alert.getTitle() + "\"?")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            db.collection("pets").document(petId)
                                    .collection("reminders").document(alert.getId())
                                    .delete()
                                    .addOnSuccessListener(unused -> {
                                        ((ViewGroup) card.getParent()).removeView(card);
                                        // Ocultar título de sección si el contenedor queda vacío
                                        recheckSectionVisibility();
                                    });
                        })
                        .setNegativeButton("Cancelar", null)
                        .show()
        );

        return card;
    }

    private void recheckSectionVisibility() {
        tvSectionUrgent.setVisibility(
                containerUrgent.getChildCount() == 0 ? View.GONE : View.VISIBLE);
        tvSectionUpcoming.setVisibility(
                containerUpcoming.getChildCount() == 0 ? View.GONE : View.VISIBLE);
        tvSectionScheduled.setVisibility(
                containerScheduled.getChildCount() == 0 ? View.GONE : View.VISIBLE);
    }

    private String getDueDateLabel(Date dueDate) {
        if (dueDate == null) return "";
        long diff = dueDate.getTime() - System.currentTimeMillis();
        long days = diff / (1000 * 60 * 60 * 24);
        if (days < 0)  return "Vencida";
        if (days == 0) return "Vence hoy";
        if (days == 1) return "Mañana";
        if (days < 7)  return "En " + days + " días";
        if (days < 30) return "En " + (days / 7) + " semanas";
        return "En " + (days / 30) + " meses";
    }

    private int dpToPx(int dp) {
        return Math.round(dp * requireContext().getResources().getDisplayMetrics().density);
    }
}