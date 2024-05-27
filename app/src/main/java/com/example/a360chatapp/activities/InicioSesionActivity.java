package com.example.a360chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a360chatapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class InicioSesionActivity extends AppCompatActivity {
    private Button btnRegistro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cargarRecursosVista();
        cargarEventosBtn();
    }

    private void cargarRecursosVista(){
        //input

        //botones
        btnRegistro = findViewById(R.id.registrarseButton);
    }

    private void cargarEventosBtn() {
        btnRegistro.setOnClickListener(this::cargarActivityRegistro);
    }
    private void cargarActivityRegistro(View view) {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, RegistroActivity.class);
            startActivity(intent);
            finish();
        }, 1000);
    }
}