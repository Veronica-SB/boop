package com.boop.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.ui.auth.AuthActivity;
import com.boop.app.ui.onboarding.OnboardingActivity;
import com.boop.app.utils.ThemeManager;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION_MS = 2500;

    // Nombre del archivo SharedPreferences — mismo en toda la app
    private static final String PREFS_NAME = "boop_prefs";

    // Clave que indica si el onboarding ya se vio
    private static final String KEY_ONBOARDING_DONE = "onboarding_done";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema claro/oscuro ANTES de inflar la pantalla
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // PASO 1: ¿Hay sesión activa en Firebase?
            // getCurrentUser() devuelve null si no hay nadie logado
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // Usuario ya logado → ir directamente al home
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return; // no seguimos comprobando nada más
            }

            // PASO 2: No hay sesión. ¿Ya vio el onboarding?
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean onboardingDone = prefs.getBoolean(KEY_ONBOARDING_DONE, false);

            if (onboardingDone) {
                // Ya vio el onboarding → ir al login
                startActivity(new Intent(this, AuthActivity.class));
            } else {
                // Primera vez → mostrar onboarding
                startActivity(new Intent(this, OnboardingActivity.class));
            }

            finish(); // cerrar el splash en cualquier caso

        }, SPLASH_DURATION_MS);
    }
}