package com.boop.app.ui.medical;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.R;
import com.boop.app.utils.ThemeManager;

public class VaccinesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccines);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}
