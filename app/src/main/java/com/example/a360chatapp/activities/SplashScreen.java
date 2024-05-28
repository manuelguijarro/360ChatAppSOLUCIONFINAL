package com.example.a360chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

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
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cargarRecursosVista();
        cargarActivity();
    }

    private void cargarRecursosVista() {
        imagenLogo = findViewById(R.id.logo360chat);
        imagenLogo.setImageResource(R.drawable._60chat);
    }

    private void cargarActivity() {
        if (FirebaseUtil.estaUsuarioLogeado() && null != getIntent().getExtras()) {
            String idUsuario = getIntent().getExtras().getString("idUsuario");
            FirebaseUtil.usuariosCollectionReference().document(idUsuario).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Usuario usuario = task.getResult().toObject(Usuario.class);
                    if (usuario != null) {
                        Intent intentMainActivity = new Intent(this, MainActivity.class);
                        startActivity(intentMainActivity);

                        Intent intentChatActivity = new Intent(this, ChatIndividualActivity.class);
                        IntentUtil.enviarUsuarioIntent(intentChatActivity, usuario);
                        intentChatActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentChatActivity);
                        finish();
                    } else {
                        manejarUsuarioNulo();
                    }
                } else {
                    manejarUsuarioNulo();
                }
            });
        } else {
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

    private void manejarUsuarioNulo() {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
