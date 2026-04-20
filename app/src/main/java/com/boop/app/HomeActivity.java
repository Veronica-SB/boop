package com.boop.app;

import android.os.Bundle;
import com.boop.app.utils.ThemeManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.boop.app.R;

public class HomeActivity extends AppCompatActivity {

    private NavController navController;

    // Bottom nav views
    private LinearLayout btnHome, btnMap, btnCommunity, btnProfile;
    private ImageView iconHome, iconMap, iconCommunity, iconProfile;
    private TextView labelHome, labelMap, labelCommunity, labelProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Setup Navigation Component
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        // Bind bottom nav views
        btnHome = findViewById(R.id.nav_btn_home);
        btnMap = findViewById(R.id.nav_btn_map);
        btnCommunity = findViewById(R.id.nav_btn_community);
        btnProfile = findViewById(R.id.nav_btn_profile);

        iconHome = findViewById(R.id.nav_icon_home);
        iconMap = findViewById(R.id.nav_icon_map);
        iconCommunity = findViewById(R.id.nav_icon_community);
        iconProfile = findViewById(R.id.nav_icon_profile);

        labelHome = findViewById(R.id.nav_label_home);
        labelMap = findViewById(R.id.nav_label_map);
        labelCommunity = findViewById(R.id.nav_label_community);
        labelProfile = findViewById(R.id.nav_label_profile);

        // Set active tab to Home by default
        setActiveTab(0);

        // Click listeners
        btnHome.setOnClickListener(v -> {
            navController.navigate(R.id.homeFragment);
            setActiveTab(0);
        });
        btnMap.setOnClickListener(v -> {
            navController.navigate(R.id.mapFragment);
            setActiveTab(1);
        });
        btnCommunity.setOnClickListener(v -> {
            navController.navigate(R.id.communityFragment);
            setActiveTab(3);
        });
        btnProfile.setOnClickListener(v -> {
            navController.navigate(R.id.profileFragment);
            setActiveTab(4);
        });

        // FAB center button
        findViewById(R.id.nav_fab).setOnClickListener(v -> {
            // TODO: Abrir pantalla de nueva alerta / nuevo post
        });
    }

    /**
     * index: 0=Home, 1=Map, 3=Community, 4=Profile
     */
    private void setActiveTab(int index) {
        int green = getColor(R.color.boop_green);
        int gray = getColor(R.color.text_tertiary_light);

        iconHome.setColorFilter(index == 0 ? green : gray);
        iconMap.setColorFilter(index == 1 ? green : gray);
        iconCommunity.setColorFilter(index == 3 ? green : gray);
        iconProfile.setColorFilter(index == 4 ? green : gray);

        labelHome.setTextColor(index == 0 ? green : gray);
        labelHome.setTypeface(null, index == 0
                ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);

        labelMap.setTextColor(index == 1 ? green : gray);
        labelCommunity.setTextColor(index == 3 ? green : gray);
        labelProfile.setTextColor(index == 4 ? green : gray);
    }
}
