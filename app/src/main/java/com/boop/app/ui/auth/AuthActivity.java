package com.boop.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import com.boop.app.utils.ThemeManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.R;
import com.boop.app.ui.addpet.AddPetActivity;
import com.google.android.material.button.MaterialButton;
import android.view.View;

public class AuthActivity extends AppCompatActivity {

    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        TextView tabLogin    = findViewById(R.id.tab_login);
        TextView tabRegister = findViewById(R.id.tab_register);
        MaterialButton btnSubmit = findViewById(R.id.btn_auth_submit);
        TextView tvForgot    = findViewById(R.id.tv_forgot);
        MaterialButton btnGoogle = findViewById(R.id.btn_google);
        View layoutNameField = findViewById(R.id.layout_name_field);

        // ── Cambio de tab Login / Registro ──────────────────────────────────
        tabLogin.setOnClickListener(v -> {
            isLoginMode = true;
            tabLogin.setBackgroundResource(R.drawable.bg_tab_selected);
            tabLogin.setTextColor(getColor(R.color.white));
            tabRegister.setBackgroundResource(android.R.color.transparent);
            tabRegister.setTextColor(getColor(R.color.text_secondary_light));
            btnSubmit.setText("Entrar");
            layoutNameField.setVisibility(View.GONE);
            tvForgot.setVisibility(View.VISIBLE);
        });

        tabRegister.setOnClickListener(v -> {
            isLoginMode = false;
            tabRegister.setBackgroundResource(R.drawable.bg_tab_selected);
            tabRegister.setTextColor(getColor(R.color.white));
            tabLogin.setBackgroundResource(android.R.color.transparent);
            tabLogin.setTextColor(getColor(R.color.text_secondary_light));
            btnSubmit.setText("Crear cuenta");
            layoutNameField.setVisibility(View.VISIBLE);
            tvForgot.setVisibility(View.GONE);
        });

        // ── CTA principal ────────────────────────────────────────────────────
        btnSubmit.setOnClickListener(v -> {
            // TODO: Lógica Firebase Auth (login o registro)
            // Por ahora va directo a AddPet (flujo nuevo usuario)
            goToAddPet();
        });

        // ── Social login (maqueta) ───────────────────────────────────────────
        btnGoogle.setOnClickListener(v -> {
            // TODO: Google Sign-In
        });

        tvForgot.setOnClickListener(v -> {
            // TODO: Recuperar contraseña con Firebase
        });
    }

    private void goToAddPet() {
        startActivity(new Intent(this, AddPetActivity.class));
        // No llamamos finish() — el usuario puede volver al login
    }
}
