package com.example.a360chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a360chatapp.R;
import com.example.a360chatapp.controllers.EmailController;
import com.example.a360chatapp.controllers.PasswordController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

public class InicioSesionActivity extends AppCompatActivity {
    private Button btnRegistro;
    private Button btnIniciarSesion;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressBar progressBar;

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

        // Inicializar App Check con Play Integrity
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
        );

        cargarRecursosVista();
        cargarEventosBtn();
    }

    private void cargarRecursosVista(){
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnRegistro = findViewById(R.id.registrarseButton);
        btnIniciarSesion = findViewById(R.id.iniciarSesionButton);
        progressBar = findViewById(R.id.progressBarInicioSesion);
    }

    private void cargarEventosBtn() {
        btnRegistro.setOnClickListener(this::cargarActivityRegistro);
        btnIniciarSesion.setOnClickListener(this::enviarFormulario);
    }

    private void cargarActivityRegistro(View view) {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, RegistroActivity.class);
            startActivity(intent);
            finish();
        }, 1000);
    }

    private void enviarFormulario(View v) {
        setEnProgreso(true);
        String emailUsuario = editTextEmail.getText().toString();
        String passwordUsuario = editTextPassword.getText().toString();

        boolean isValid = true;

        if (!EmailController.comprobarEmail(emailUsuario)) {
            editTextEmail.setError("Correo electrónico no válido");
            isValid = false;
        }

        if (!PasswordController.comprobarPassword(passwordUsuario)) {
            editTextPassword.setError("Contraseña no válida");
            isValid = false;
        }

        if (isValid) {
            iniciarSesion(emailUsuario, passwordUsuario);
        } else {
            setEnProgreso(false);
        }
    }

    private void iniciarSesion(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        cargarActivityMain();
                    } else {
                        setEnProgreso(false);
                        editTextEmail.setError("Error al iniciar sesión");
                    }
                }).addOnFailureListener(e -> {
                    setEnProgreso(false);
                    editTextEmail.setError("Error: " + e.getMessage());
                });
    }

    private void setEnProgreso(boolean enProgreso){
        if (enProgreso){
            btnIniciarSesion.setVisibility(View.GONE);
            btnRegistro.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btnIniciarSesion.setVisibility(View.VISIBLE);
            btnRegistro.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void cargarActivityMain() {
        try {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
