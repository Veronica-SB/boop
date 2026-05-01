package com.boop.app.ui.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.boop.app.R;
import com.boop.app.ui.auth.AuthActivity;
import com.boop.app.utils.ThemeManager;

public class OnboardingActivity extends AppCompatActivity {

    // Mismo archivo y clave que usa MainActivity
    private static final String PREFS_NAME = "boop_prefs";
    private static final String KEY_ONBOARDING_DONE = "onboarding_done";

    private ViewPager2 viewPager;
    private OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.onboarding_viewpager);
        adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);

        // Navegación solo con botones, sin swipe
        viewPager.setUserInputEnabled(false);
    }

    /** Llamado desde los fragmentos al pulsar "Siguiente" */
    public void goToNextStep() {
        int current = viewPager.getCurrentItem();
        if (current < adapter.getItemCount() - 1) {
            viewPager.setCurrentItem(current + 1, true);
        } else {
            // Último paso → ir al login
            goToAuth();
        }
    }

    /** Llamado desde los fragmentos al pulsar "Omitir" */
    public void skipOnboarding() {
        goToAuth();
    }

    private void goToAuth() {
        // Guardamos que el onboarding ya se vio para que el splash
        // no lo vuelva a mostrar en próximas aperturas de la app
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putBoolean(KEY_ONBOARDING_DONE, true)
                .apply();

        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }
}