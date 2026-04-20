package com.boop.app.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import com.boop.app.utils.ThemeManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.boop.app.R;
import com.boop.app.ui.auth.AuthActivity;

public class OnboardingActivity extends AppCompatActivity {

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

        // Disable swipe — navegación sólo con botones
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
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }
}
