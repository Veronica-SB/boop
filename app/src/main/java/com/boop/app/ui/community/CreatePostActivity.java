package com.boop.app.ui.community;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.R;
import com.boop.app.utils.ThemeManager;
import com.google.android.material.chip.ChipGroup;

public class CreatePostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());

        findViewById(R.id.btn_publish).setOnClickListener(v -> publishPost());
    }

    private void publishPost() {
        EditText etContent = findViewById(R.id.et_post_content);
        String content = etContent.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            etContent.setError("El mensaje no puede estar vacío");
            return;
        }

        Toast.makeText(this, "Publicación creada", Toast.LENGTH_SHORT).show();
        finish();
    }
}
