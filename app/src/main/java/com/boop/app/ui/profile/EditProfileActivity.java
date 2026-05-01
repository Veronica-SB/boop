package com.boop.app.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.R;
import com.boop.app.utils.ThemeManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db          = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_save_profile).setOnClickListener(v -> saveProfile());

        loadCurrentData();
    }

    private void loadCurrentData() {
        if (currentUser == null) return;

        // Cargar email de Firebase Auth
        EditText etEmail = findViewById(R.id.et_email);
        if (currentUser.getEmail() != null)
            etEmail.setText(currentUser.getEmail());

        // Cargar resto de datos desde Firestore
        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(doc -> {
                    String name      = doc.getString("name");
                    String phone     = doc.getString("phone");
                    String birthDate = doc.getString("birthDate");

                    if (name != null)      ((EditText) findViewById(R.id.et_name)).setText(name);
                    if (phone != null)     ((EditText) findViewById(R.id.et_phone)).setText(phone);
                    if (birthDate != null) ((EditText) findViewById(R.id.et_birthdate)).setText(birthDate);
                });
    }

    private void saveProfile() {
        String name      = getValue(R.id.et_name);
        String email     = getValue(R.id.et_email);
        String phone     = getValue(R.id.et_phone);
        String birthDate = getValue(R.id.et_birthdate);

        if (TextUtils.isEmpty(name)) {
            ((EditText) findViewById(R.id.et_name)).setError("El nombre es obligatorio");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            ((EditText) findViewById(R.id.et_email)).setError("El email es obligatorio");
            return;
        }

        // Actualizar displayName en Firebase Auth
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        currentUser.updateProfile(profileUpdate);

        // Actualizar email en Firebase Auth si cambió
        if (!email.equals(currentUser.getEmail())) {
            currentUser.updateEmail(email)
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al actualizar email: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show());
        }

        // Actualizar en Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("name",      name);
        updates.put("email",     email);
        updates.put("phone",     phone);
        updates.put("birthDate", birthDate);

        db.collection("users").document(currentUser.getUid())
                .update(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Perfil actualizado ✅", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar. Inténtalo de nuevo.",
                                Toast.LENGTH_SHORT).show());
    }

    private String getValue(int viewId) {
        EditText et = findViewById(viewId);
        return et != null ? et.getText().toString().trim() : "";
    }
}
