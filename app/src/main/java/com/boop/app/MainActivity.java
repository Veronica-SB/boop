package com.boop.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.boop.app.ui.auth.AuthActivity;
import com.boop.app.ui.onboarding.OnboardingActivity;
import com.boop.app.utils.ThemeManager;
import com.google.firebase.auth.FirebaseAuth;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION_MS = 2500;
    private static final String PREFS_NAME = "boop_prefs";
    private static final String KEY_ONBOARDING_DONE = "onboarding_done";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Programar comprobación de alertas cada hora
        PeriodicWorkRequest alertWork =
                new PeriodicWorkRequest.Builder(AlertWorker.class, 1, TimeUnit.HOURS)
                        .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "boop_alert_check",
                ExistingPeriodicWorkPolicy.KEEP,
                alertWork);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return;
            }

            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean onboardingDone = prefs.getBoolean(KEY_ONBOARDING_DONE, false);

            if (onboardingDone) {
                startActivity(new Intent(this, AuthActivity.class));
            } else {
                startActivity(new Intent(this, OnboardingActivity.class));
            }

            finish();

        }, SPLASH_DURATION_MS);
    }
}