package com.example.a360chatapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a360chatapp.R;
import com.example.a360chatapp.db.models.Usuario;
import com.example.a360chatapp.firebase.FirebaseUtil;
import com.example.a360chatapp.utils.IntentUtil;

public class SplashScreen extends AppCompatActivity {
    private ImageView imagenLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        aplicarTema();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        actualizarUI();
        cargarRecursosVista();

        if (FirebaseUtil.estaUsuarioLogeado() && getIntent().getExtras() != null) {
            String idUsuario = getIntent().getExtras().getString("id");
            if (idUsuario != null) {
                FirebaseUtil.usuariosCollectionReference().document(idUsuario).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Usuario usuario = task.getResult().toObject(Usuario.class);
                        Intent mainIntent = new Intent(this, MainActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mainIntent);

                        Intent intentChatActivity = new Intent(this, ChatIndividualActivity.class);
                        IntentUtil.enviarUsuarioIntent(intentChatActivity, usuario);
                        intentChatActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentChatActivity);
                        finish();
                    }
                });
            } else {
                // Manejar el caso en que idUsuario sea nulo
                cargarActivity();
            }
        } else {
            cargarActivity();
        }
    }

    private void actualizarUI() {
        try {
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int colorPrimary = typedValue.data;

            ProgressBar progressBarSplashScreenObj = findViewById(R.id.progressBarSplashScreen);
            progressBarSplashScreenObj.getIndeterminateDrawable().setColorFilter(colorPrimary, PorterDuff.Mode.MULTIPLY);

            imagenLogo = findViewById(R.id.logo360chat);
            int drawableId;

            // Comparar el colorPrimary con los valores hexadecimales de los colores
            if (colorPrimary == 0xFFFFB3BA) { // Color rosa claro
                drawableId = R.drawable._60chatrosa;
            } else if (colorPrimary == 0xFFFFDFBA) { // Color marrón pastel
                drawableId = R.drawable._60chatmarron;
            } else if (colorPrimary == 0xFFFFFFBA) { // Color amarillo pastel
                drawableId = R.drawable._60chatamarrillo;
            } else if (colorPrimary == 0xFFBAFFC9) { // Color verde pastel
                drawableId = R.drawable._60chatverde;
            } else {
                drawableId = R.drawable._60chat; // Default drawable
            }

            imagenLogo.setImageResource(drawableId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void aplicarTema() {
        try {
            SharedPreferences preferences = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
            int themeId = preferences.getInt("selected_theme", R.style.Base_Theme__360ChatApp); // Default theme
            setTheme(themeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarRecursosVista() {
        imagenLogo = findViewById(R.id.logo360chat);
       // imagenLogo.setImageResource(R.drawable._60chat);
    }

    private void cargarActivity() {
        new Handler().postDelayed(() -> {
            Intent intent;
            if (FirebaseUtil.estaUsuarioLogeado()) {
                intent = new Intent(SplashScreen.this, MainActivity.class);
            } else {
                intent = new Intent(SplashScreen.this, InicioSesionActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}
