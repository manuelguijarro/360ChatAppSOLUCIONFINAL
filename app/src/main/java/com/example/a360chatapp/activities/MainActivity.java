package com.example.a360chatapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ImageButton btnBuscar;
    private ChatFragment chatFragment;
    private PerfilFragment perfilFragment;
    private AjusteFragment ajusteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            aplicarTema();
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            if (FirebaseUtil.estaUsuarioLogeado()) {
                instanciarFragmentos();
                cargarRecursosVista();
                cargarEventoBtnBuscar();
                cargarEventoMenuInferior();
                actualizarUI();
                if (savedInstanceState == null) {
                    bottomNavigationView.setSelectedItemId(R.id.menu_chats);
                } else {
                    bottomNavigationView.setSelectedItemId(obtenerMenuItemSeleccionado());
                }
                obtenerTokenNotificacion();
            } else {
                cargarActivityInicioSesion();
            }
        } catch (Exception e) {
            e.printStackTrace();
            cargarActivityInicioSesion();
        }
    }

    private int obtenerMenuItemSeleccionado() {
        try {
            SharedPreferences preferences = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
            return preferences.getInt("selected_menu_item", R.id.menu_chats);
        } catch (Exception e) {
            e.printStackTrace();
            return R.id.menu_chats;
        }
    }

    private void aplicarTema() {
        try {
            SharedPreferences preferences = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
            int themeId = preferences.getInt("selected_theme", R.style.Base_Theme__360ChatApp);
            String idiomaSeleccionado = preferences.getString("selected_language", null);

            // Configurar el idioma si está definido
            if (idiomaSeleccionado != null) {
                Locale locale = new Locale(idiomaSeleccionado);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            }

            // Establecer el tema
            setTheme(themeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizarUI() {
        try {
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int colorPrimary = typedValue.data;


            RelativeLayout toolbar = findViewById(R.id.main_toolbar);
            toolbar.setBackgroundColor(colorPrimary);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarEventoMenuInferior() {
        try {
            bottomNavigationView.setOnItemSelectedListener(menuItem -> {
                try {
                    if (menuItem.getItemId() == R.id.menu_chats) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_frame_layout, chatFragment)
                                .commit();
                    }
                    if (menuItem.getItemId() == R.id.menu_perfil) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_frame_layout, perfilFragment)
                                .commit();
                    }
                    if (menuItem.getItemId() == R.id.menu_ajustes) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_frame_layout, ajusteFragment)
                                .commit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            });
            bottomNavigationView.setSelectedItemId(R.id.menu_chats);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarEventoBtnBuscar() {
        try {
            btnBuscar.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(MainActivity.this, BuscarUsuarioActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (!FirebaseUtil.estaUsuarioLogeado()) {
                cargarActivityInicioSesion();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarRecursosVista() {
        try {
            // Botones
            btnBuscar = findViewById(R.id.btn_buscar_main);
            // Menu inferior
            bottomNavigationView = findViewById(R.id.bottom_navigation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void obtenerTokenNotificacion() {
        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                try {
                    if (task.isSuccessful()) {
                        String tokenNotificacion = task.getResult();
                        Log.i("Token de usuario: ", tokenNotificacion);
                        FirebaseUtil.obtenerDetallesUsuarioActual().update("tokenNotificacion", tokenNotificacion);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarActivityInicioSesion() {
        try {
            new Handler().postDelayed(() -> {
                try {
                    Intent intent = new Intent(MainActivity.this, InicioSesionActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 1650);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void instanciarFragmentos() {
        try {
            chatFragment = new ChatFragment();
            perfilFragment = new PerfilFragment();
            ajusteFragment = new AjusteFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
