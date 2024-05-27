package com.example.a360chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a360chatapp.R;
import com.example.a360chatapp.db.models.Usuario;
import com.example.a360chatapp.firebase.FirebaseUtil;
import com.example.a360chatapp.utils.IntentUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

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
        if (FirebaseUtil.estaUsuarioLogeado() && null != getIntent().getExtras()){
            String idUsuario = getIntent().getExtras().getString("idUsuario");
            FirebaseUtil.usuariosCollectionReference().document("idUsuario").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Usuario usuario = task.getResult().toObject(Usuario.class);

                    Intent intentMainActivity = new Intent(this,MainActivity.class);
                    //esto se ace para que por debajo vallamso a la actividad principal, para que cuando estemos en el chat y volvamos atras, estemos en el main
                    intentMainActivity.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentMainActivity);


                    Intent intentChatActivity = new Intent(this, ChatIndividualActivity.class);
                    IntentUtil.enviarUsuarioIntent(intentChatActivity,usuario);
                    intentChatActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentChatActivity);
                    finish();
                }
            });
        }else{
            new Handler().postDelayed(() -> {
                if (FirebaseUtil.estaUsuarioLogeado()){
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                }else {
                    startActivity(new Intent(SplashScreen.this, InicioSesionActivity.class));
                }
                finish();
            }, 2000);
        }
    }
}