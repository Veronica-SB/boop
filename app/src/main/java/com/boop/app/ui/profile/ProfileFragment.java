package com.boop.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.boop.app.R;
import com.boop.app.ui.auth.AuthActivity;
import com.boop.app.ui.settings.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FirebaseFirestore db;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();

            // Nombre del usuario
            TextView tvName = view.findViewById(R.id.tv_user_name);
            if (firebaseUser.getDisplayName() != null && !firebaseUser.getDisplayName().isEmpty()) {
                tvName.setText(firebaseUser.getDisplayName());
            } else {
                // Si no tiene displayName, lo leemos de Firestore
                db.collection("users").document(currentUserId).get()
                        .addOnSuccessListener(doc -> {
                            String name = doc.getString("name");
                            if (name != null && !name.isEmpty()) tvName.setText(name);
                        });
            }

            // Stats
            loadStats(view);
        }

        // Navegación
        view.findViewById(R.id.btn_settings).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SettingsActivity.class)));

        view.findViewById(R.id.btn_edit_profile).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Editar perfil (Próximamente)", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.btn_notifications_settings).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SettingsActivity.class)));

        view.findViewById(R.id.btn_privacy).setOnClickListener(v ->
                Toast.makeText(requireContext(), "Privacidad (Próximamente)", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.btn_logout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadStats(View view) {
        TextView tvStatPets    = view.findViewById(R.id.tv_stat_pets);
        TextView tvStatAlerts  = view.findViewById(R.id.tv_stat_alerts);
        TextView tvStatPosts   = view.findViewById(R.id.tv_stat_posts);

        // Contar mascotas
        db.collection("pets")
                .whereEqualTo("ownerId", currentUserId)
                .get()
                .addOnSuccessListener(snap ->
                        tvStatPets.setText(String.valueOf(snap.size())));

        // Contar recordatorios activos
        db.collection("pets")
                .whereEqualTo("ownerId", currentUserId)
                .get()
                .addOnSuccessListener(pets -> {
                    final int[] total = {0};
                    final int[] petCount = {pets.size()};
                    if (petCount[0] == 0) {
                        tvStatAlerts.setText("0");
                        return;
                    }
                    for (com.google.firebase.firestore.QueryDocumentSnapshot petDoc : pets) {
                        db.collection("pets").document(petDoc.getId())
                                .collection("reminders")
                                .whereEqualTo("isEnabled", true)
                                .get()
                                .addOnSuccessListener(reminders -> {
                                    total[0] += reminders.size();
                                    petCount[0]--;
                                    if (petCount[0] == 0) {
                                        tvStatAlerts.setText(String.valueOf(total[0]));
                                    }
                                });
                    }
                });

        // Contar posts
        db.collection("community_posts")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(snap ->
                        tvStatPosts.setText(String.valueOf(snap.size())));
    }
}