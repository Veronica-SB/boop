package com.boop.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * BOOP — ThemeManager (Java)
 *
 * Gestiona el modo oscuro/claro de la app.
 * Persiste la preferencia del usuario en SharedPreferences.
 *
 * ── Uso desde ProfileActivity/Fragment (pantalla Ajustes) ──
 *
 *   // Activar modo oscuro:
 *   ThemeManager.setTheme(getContext(), ThemeManager.MODE_DARK);
 *
 *   // Activar modo claro:
 *   ThemeManager.setTheme(getContext(), ThemeManager.MODE_LIGHT);
 *
 *   // Seguir el sistema:
 *   ThemeManager.setTheme(getContext(), ThemeManager.MODE_SYSTEM);
 *
 * ── Uso en MainActivity.onCreate() ──
 *   // ANTES de setContentView():
 *   ThemeManager.applyTheme(this);
 */
public class ThemeManager {

    // Constantes de modo
    public static final int MODE_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    public static final int MODE_LIGHT  = AppCompatDelegate.MODE_NIGHT_NO;
    public static final int MODE_DARK   = AppCompatDelegate.MODE_NIGHT_YES;

    private static final String PREFS_NAME = "boop_prefs";
    private static final String KEY_THEME  = "theme_mode";

    /**
     * Aplica el tema guardado.
     * Llamar en MainActivity.onCreate() ANTES de setContentView().
     */
    public static void applyTheme(Context context) {
        int savedMode = getSavedMode(context);
        AppCompatDelegate.setDefaultNightMode(savedMode);
    }

    /**
     * Cambia el tema y lo persiste.
     * La Activity se recrea automáticamente aplicando el nuevo tema.
     *
     * @param mode  ThemeManager.MODE_SYSTEM | MODE_LIGHT | MODE_DARK
     */
    public static void setTheme(Context context, int mode) {
        saveMode(context, mode);
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    /**
     * Devuelve el modo guardado actualmente.
     * Por defecto: MODE_SYSTEM
     */
    public static int getSavedMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME, MODE_SYSTEM);
    }

    private static void saveMode(Context context, int mode) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_THEME, mode)
                .apply();
    }
}
