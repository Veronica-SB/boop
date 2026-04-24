package com.boop.app.ui.medical;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.R;
import com.boop.app.utils.ThemeManager;

public class AddMedicalVisitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_visit);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        findViewById(R.id.btn_save_visit).setOnClickListener(v -> saveVisit());
    }

    private void saveVisit() {
        EditText etDescription = findViewById(R.id.et_visit_description);
        String desc = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(desc)) {
            etDescription.setError("Por favor, añade una descripción");
            return;
        }

        // TODO: Lógica Firebase Firestore
        Toast.makeText(this, "Visita guardada correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }
}
