package com.example.a360chatapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a360chatapp.R;
import com.example.a360chatapp.firebase.FirebaseUtil;
import com.example.a360chatapp.fragments.AjusteFragment;
import com.example.a360chatapp.fragments.ChatFragment;
import com.example.a360chatapp.fragments.PerfilFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ImageButton btnBuscar;
    private ChatFragment chatFragment;
    private PerfilFragment perfilFragment;
    private AjusteFragment ajusteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        aplicarTema();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });

        if(FirebaseUtil.estaUsuarioLogeado()){

            instanciarFragmentos();
            cargarRecursosVista();
            cargarEventoBtnBuscar();
            cargarEventoMenuInferior();
            actualizarUI();
            obtenerTokenNotificacion();

        }else{
            cargarActivityInicioSesion();
        }
    }

    private void aplicarTema() {
        try {
            SharedPreferences preferences = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
            int themeId = preferences.getInt("selected_theme", R.style.Base_Theme__360ChatApp); // Default theme
            setTheme(themeId);
        }catch (Exception e){

        }
    }
    private void actualizarUI() {
        try {
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int colorPrimary = typedValue.data;

            // Actualiza el fondo del toolbar
            RelativeLayout toolbar = findViewById(R.id.main_toolbar);
            toolbar.setBackgroundColor(colorPrimary);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarEventoMenuInferior() {
        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.menu_chats){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_frame_layout, chatFragment)
                        .commit();
            }
            if (menuItem.getItemId() == R.id.menu_perfil){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_frame_layout, perfilFragment)
                        .commit();
            }
            if (menuItem.getItemId() == R.id.menu_ajustes){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_frame_layout, ajusteFragment)
                        .commit();
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chats);
    }
    private void cargarEventoBtnBuscar(){
        btnBuscar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BuscarUsuarioActivity.class));
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(!FirebaseUtil.estaUsuarioLogeado()){
            cargarActivityInicioSesion();
        }

    }
    private void cargarRecursosVista(){
        //Botones
        btnBuscar = findViewById(R.id.btn_buscar_main);
        //Menu inferior
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }


    private void obtenerTokenNotificacion(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                String tokenNotificacion = task.getResult();
                Log.i("Token de usuario: ", tokenNotificacion);
                FirebaseUtil.obtenerDetallesUsuarioActual().update("tokenNotificacion",tokenNotificacion);
            }
        });
    }

    private void cargarActivityInicioSesion() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, InicioSesionActivity.class);
            startActivity(intent);
            finish();
        }, 1650);
    }
    private void instanciarFragmentos() {
        chatFragment = new ChatFragment();
        perfilFragment = new PerfilFragment();
        ajusteFragment = new AjusteFragment();
    }
}