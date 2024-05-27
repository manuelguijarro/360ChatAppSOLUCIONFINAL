package com.example.a360chatapp.activities;


import static com.example.a360chatapp.firebase.FirebaseUtil.obtenerUsuarioUid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a360chatapp.R;
import com.example.a360chatapp.controllers.EmailController;
import com.example.a360chatapp.controllers.PasswordController;
import com.example.a360chatapp.db.models.Usuario;
import com.example.a360chatapp.firebase.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;


public class RegistroActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextRepetirPassword;
    private Button btnRegistrarse;
    private Button btnVolverAtras;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
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
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRepetirPassword = findViewById(R.id.editTextRepetirPassword);
        //botones
        btnRegistrarse = findViewById(R.id.registrarButton);
        btnVolverAtras = findViewById(R.id.volverButton);
        //progressBar
        progressBar = findViewById(R.id.progressBarRegistro);

    }
    private void cargarEventosBtn() {
        btnRegistrarse.setOnClickListener(this::enviarFormulario);
        btnVolverAtras.setOnClickListener(this::cargarActivityInicioSesion);
    }
    private void enviarFormulario(View v) {
        setEnProgreso(true);
        String emailUsuario = editTextEmail.getText().toString();
        String passwordUsuario = editTextPassword.getText().toString();
        String passwordRepetirUsuario = editTextRepetirPassword.getText().toString();

        if (passwordUsuario.equals(passwordRepetirUsuario) &&
                EmailController.comprobarEmail(emailUsuario) &&
                PasswordController.comprobarPassword(passwordUsuario) &&
                PasswordController.comprobarPassword(passwordRepetirUsuario)) {

            crearNuevoUsuarioAuth( emailUsuario, passwordUsuario);
        } else {
            // mostrarMensajeAlerta("Datos incorrectos, vuelve a introducir los datos correctamente");
            setEnProgreso(false);
        }
    }


    private void crearNuevoUsuarioAuth(String email, String password){
        String passwordCifrada = PasswordController.get_SHA_512_SecurePassword(password,"ambgk");
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, passwordCifrada)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        continuarProcesoRegistro();
                    } else {
                        setEnProgreso(false);
                        // If sign in fails, display a message to the user.
                       // Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                        //        Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }
                });
    }
    private void continuarProcesoRegistro(){
        crearNuevoUsuarioDB();
    }
    public  void crearNuevoUsuarioDB() {
        try {
            String usuarioUid = FirebaseUtil.obtenerUsuarioUid();
            String emailUsuario = FirebaseUtil.obtenerUsuarioEmail();
            String nombreUsuario = emailUsuario.split("@")[0];
            Usuario usuario = new Usuario(usuarioUid, nombreUsuario, emailUsuario, Timestamp.now());
            FirebaseUtil.obtenerDetallesUsuarioActual().set(usuario).addOnCompleteListener(task -> {
                //Aqui iria codigo si queremos notificar.
                //Lo enviamos al main porque a tenido exito
                cargarActivityMain();
            }).addOnFailureListener(e -> {
                //AQUI IRIA EN CASO DE FALLO PARA NOTIFICAR AL USUARIO
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void setEnProgreso(boolean enProgreso){
        if (enProgreso){
            btnRegistrarse.setVisibility(View.GONE);
            btnVolverAtras.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }else {
            btnRegistrarse.setVisibility(View.VISIBLE);
            btnVolverAtras.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
    private void cargarActivityInicioSesion(View view) {
        try {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(this, InicioSesionActivity.class);
                startActivity(intent);
                finish();
            }, 1000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void cargarActivityMain() {
        try {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 1000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}