package com.boop.app.ui.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.R;
import com.boop.app.utils.ThemeManager;

public class GuardiansActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardians);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        View layoutSearch = findViewById(R.id.layout_search_guardian);
        View btnShowAdd = findViewById(R.id.btn_show_add_field);
        EditText etEmail = findViewById(R.id.et_search_guardian_email);
        View btnSearch = findViewById(R.id.btn_search_user);
        View layoutResult = findViewById(R.id.layout_search_result);

        btnShowAdd.setOnClickListener(v -> {
            layoutSearch.setVisibility(View.VISIBLE);
            btnShowAdd.setVisibility(View.GONE);
        });

        btnSearch.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                etEmail.setError("Introduce un email");
            } else {
                // Simulación de búsqueda
                layoutResult.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Usuario encontrado", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_remove_brenda).setOnClickListener(v -> {
            Toast.makeText(this, "Cuidador eliminado", Toast.LENGTH_SHORT).show();
        });
    }
}
