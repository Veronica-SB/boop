package com.boop.app;

import android.content.Intent; // NUEVO: Para poder navegar
import android.os.Bundle;
import android.os.Handler; // NUEVO: Para el temporizador
import androidx.appcompat.app.AppCompatActivity;
import com.boop.app.utils.ThemeManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema ANTES que todo
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- LÓGICA OPERATIVA: SALTO A LA HOME ---
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Esto abrirá la pantalla principal después de 3 segundos
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);

                // Cerramos la Splash para que el usuario no vuelva atrás al pulsar "back"
                finish();
            }
        }, 3000); // 3000 milisegundos = 3 segundos
    }
}