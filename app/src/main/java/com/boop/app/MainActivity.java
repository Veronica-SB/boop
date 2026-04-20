package com.boop.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.ui.onboarding.OnboardingActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION_MS = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // TODO: Comprobar si el usuario ya ha hecho el onboarding
            //       (SharedPreferences o Firebase Auth). Por ahora siempre
            //       va al onboarding.
            startActivity(new Intent(MainActivity.this, OnboardingActivity.class));
            finish();
        }, SPLASH_DURATION_MS);
    }
}
