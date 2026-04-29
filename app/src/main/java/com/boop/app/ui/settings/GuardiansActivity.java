package com.boop.app.ui.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.boop.app.R;
import com.boop.app.utils.ThemeManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class GuardiansActivity extends AppCompatActivity {

    public static final String EXTRA_PET_ID   = "pet_id";
    public static final String EXTRA_PET_NAME = "pet_name";

    private FirebaseFirestore db;
    private String currentUserId;
    private String petId;
    private String petName;

    private LinearLayout containerGuardians;
    private LinearLayout layoutSearchResult;
    private TextView tvSearchResultName;

    // Usuario encontrado en la búsqueda
    private String foundUserId   = null;
    private String foundUserName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardians);

        db            = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        petId         = getIntent().getStringExtra(EXTRA_PET_ID);
        petName       = getIntent().getStringExtra(EXTRA_PET_NAME);

        containerGuardians = findViewById(R.id.container_guardians);
        layoutSearchResult = findViewById(R.id.layout_search_result);
        tvSearchResultName = findViewById(R.id.tv_search_result_name);

        // Subtítulo con nombre real de la mascota
        TextView tvSubtitle = findViewById(R.id.tv_guardians_subtitle);
        tvSubtitle.setText("Personas con acceso al perfil y alertas de " + petName);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Mostrar campo de búsqueda
        View layoutSearch  = findViewById(R.id.layout_search_guardian);
        View btnShowAdd    = findViewById(R.id.btn_show_add_field);
        btnShowAdd.setOnClickListener(v -> {
            layoutSearch.setVisibility(View.VISIBLE);
            btnShowAdd.setVisibility(View.GONE);
        });

        // Buscar usuario por email
        EditText etEmail = findViewById(R.id.et_search_guardian_email);
        findViewById(R.id.btn_search_user).setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Introduce un email");
                return;
            }
            searchUserByEmail(email);
        });

        // Añadir cuidador encontrado
        findViewById(R.id.btn_add_guardian).setOnClickListener(v -> addGuardian());

        loadGuardians();
    }

    // ── Cargar cuidadores desde Firestore ─────────────────────────
    private void loadGuardians() {
        containerGuardians.removeAllViews();

        // Primero añadimos al owner
        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(doc -> {
                    String ownerName  = doc.getString("name");
                    String ownerEmail = doc.getString("email");
                    containerGuardians.addView(buildGuardianCard(
                            ownerName != null ? ownerName : "Propietario",
                            ownerEmail != null ? ownerEmail : "",
                            "Propietario",
                            null // el owner no se puede eliminar
                    ));
                });

        // Luego los guardians
        db.collection("pets").document(petId)
                .collection("guardians")
                .get()
                .addOnSuccessListener(snapshots -> {
                    for (QueryDocumentSnapshot doc : snapshots) {
                        String guardianId = doc.getId();
                        // Leemos los datos del usuario desde users/
                        db.collection("users").document(guardianId).get()
                                .addOnSuccessListener(userDoc -> {
                                    String name  = userDoc.getString("name");
                                    String email = userDoc.getString("email");
                                    containerGuardians.addView(buildGuardianCard(
                                            name  != null ? name  : "Cuidador",
                                            email != null ? email : "",
                                            "Cuidador",
                                            guardianId
                                    ));
                                });
                    }
                });
    }

    // ── Buscar usuario por email ───────────────────────────────────
    private void searchUserByEmail(String email) {
        foundUserId   = null;
        foundUserName = null;
        layoutSearchResult.setVisibility(View.GONE);

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots.isEmpty()) {
                        Toast.makeText(this,
                                "Este usuario no tiene cuenta en BOOP",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    QueryDocumentSnapshot doc =
                            (QueryDocumentSnapshot) snapshots.getDocuments().get(0);

                    foundUserId   = doc.getId();
                    foundUserName = doc.getString("name");

                    // Comprobar que no es el propio owner
                    if (foundUserId.equals(currentUserId)) {
                        Toast.makeText(this,
                                "No puedes añadirte a ti mismo como cuidador",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Comprobar que no es ya cuidador
                    db.collection("pets").document(petId)
                            .collection("guardians").document(foundUserId).get()
                            .addOnSuccessListener(guardianDoc -> {
                                if (guardianDoc.exists()) {
                                    Toast.makeText(this,
                                            foundUserName + " ya es cuidador de " + petName,
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                tvSearchResultName.setText(
                                        "Usuario encontrado: " + foundUserName + " · ¿Añadir como cuidador?");
                                layoutSearchResult.setVisibility(View.VISIBLE);
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al buscar. Inténtalo de nuevo.",
                                Toast.LENGTH_SHORT).show());
    }

    // ── Añadir guardian en Firestore ──────────────────────────────
    private void addGuardian() {
        if (foundUserId == null) return;

        Map<String, Object> guardian = new HashMap<>();
        guardian.put("role",      "EDITOR");
        guardian.put("invitedBy", currentUserId);
        guardian.put("invitedAt", new java.util.Date());

        db.collection("pets").document(petId)
                .collection("guardians").document(foundUserId)
                .set(guardian)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this,
                            foundUserName + " añadido como cuidador 🐾",
                            Toast.LENGTH_SHORT).show();
                    layoutSearchResult.setVisibility(View.GONE);
                    foundUserId   = null;
                    foundUserName = null;
                    loadGuardians(); // recarga la lista
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al añadir. Inténtalo de nuevo.",
                                Toast.LENGTH_SHORT).show());
    }

    // ── Construir card de guardian ────────────────────────────────
    private View buildGuardianCard(String name, String email,
                                   String role, String guardianId) {
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

        // Avatar emoji
        TextView avatar = new TextView(this);
        avatar.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(40), dpToPx(40)));
        avatar.setText("Propietario".equals(role) ? "👦" : "👧");
        avatar.setTextSize(20);
        avatar.setGravity(android.view.Gravity.CENTER);
        avatar.setBackgroundResource(R.drawable.bg_avatar_card);
        row.addView(avatar);

        // Nombre y email
        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textsParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textsParams.setMarginStart(dpToPx(12));
        texts.setLayoutParams(textsParams);

        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        texts.addView(tvName);

        TextView tvEmail = new TextView(this);
        tvEmail.setText(email);
        tvEmail.setTextSize(11);
        tvEmail.setTextColor(getColor(R.color.text_tertiary_light));
        texts.addView(tvEmail);
        row.addView(texts);

        // Role o botón eliminar
        if (guardianId == null) {
            // Es el owner — solo muestra el rol
            TextView tvRole = new TextView(this);
            tvRole.setText(role);
            tvRole.setTextSize(11);
            tvRole.setTextColor(getColor(R.color.text_tertiary_light));
            row.addView(tvRole);
        } else {
            // Es un guardian — botón eliminar
            TextView btnRemove = new TextView(this);
            btnRemove.setText("Eliminar");
            btnRemove.setTextSize(11);
            btnRemove.setTextColor(getColor(R.color.alert_red));
            btnRemove.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
            btnRemove.setClickable(true);
            btnRemove.setFocusable(true);
            btnRemove.setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("Eliminar cuidador")
                            .setMessage("¿Seguro que quieres eliminar a " + name + " como cuidador?")
                            .setPositiveButton("Eliminar", (dialog, which) ->
                                    db.collection("pets").document(petId)
                                            .collection("guardians").document(guardianId)
                                            .delete()
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(this,
                                                        name + " eliminado como cuidador",
                                                        Toast.LENGTH_SHORT).show();
                                                loadGuardians();
                                            })
                            )
                            .setNegativeButton("Cancelar", null)
                            .show()
            );
            row.addView(btnRemove);
        }

        return card;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}