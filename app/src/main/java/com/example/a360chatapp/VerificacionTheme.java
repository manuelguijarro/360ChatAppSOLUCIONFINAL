package com.example.a360chatapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class VerificacionTheme extends Application {
    private static VerificacionTheme instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        checkThemeConfiguration();
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    private void checkThemeConfiguration() {
        SharedPreferences sharedPreferences = getSharedPreferences("theme_preferences", MODE_PRIVATE);
        // Comprueba si la configuración de tema existe
        if (!sharedPreferences.contains("theme_selected")) {
            // Si no existe, establece un tema predeterminado aquí
            // Por ejemplo:
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("theme_selected", R.style.Base_Theme__360ChatApp); // R.style.AppTheme es tu tema predeterminado
            editor.apply();
        }
    }
}
