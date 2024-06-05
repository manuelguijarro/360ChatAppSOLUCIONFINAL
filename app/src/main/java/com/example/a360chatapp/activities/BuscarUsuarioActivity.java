package com.example.a360chatapp.activities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a360chatapp.R;
import com.example.a360chatapp.WrapContentLinearLayoutManager;
import com.example.a360chatapp.adapters.BuscarUsuarioRecyclerViewAdapter;
import com.example.a360chatapp.db.models.Usuario;
import com.example.a360chatapp.firebase.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class BuscarUsuarioActivity extends AppCompatActivity {
    private EditText inputBuscar;
    private ImageButton btnBuscar;
    private ImageButton btnVolver;
    private RecyclerView recyclerView;
    private BuscarUsuarioRecyclerViewAdapter buscarUsuarioRecyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        aplicarTema();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscar_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cargarRecursosVista();
        inputBuscar.requestFocus();
        cargarEventosBtn();
        actualizarUI();
    }
    private void actualizarUI() {
        try {
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int colorPrimary = typedValue.data;

            // Actualiza el fondo del toolbar
            RelativeLayout toolbar = findViewById(R.id.toolbar);
            toolbar.setBackgroundColor(colorPrimary);

            // Actualiza otros componentes si es necesario
            ImageButton btnBuscarUsuario = findViewById(R.id.btn_buscar_usuario);
            btnBuscarUsuario.setColorFilter(colorPrimary);

        } catch (Exception e) {
            e.printStackTrace();
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


    private void cargarRecursosVista(){
        //input
        inputBuscar = findViewById(R.id.input_buscar_usuario);
        //botones
        btnBuscar = findViewById(R.id.btn_buscar_usuario);
        btnVolver = findViewById(R.id.btn_volver);
        //RecyclerView
        recyclerView = findViewById(R.id.buscar_usuario_recycler_view);

    }

    private void cargarEventosBtn() {
        btnVolver.setOnClickListener(this::cargarEventoVolver);
        btnBuscar.setOnClickListener(this::cargarEventoBuscar);
    }

    private void cargarEventoBuscar(View view) {
        String emailABuscar = inputBuscar.getText().toString().trim();
        if (emailABuscar.isEmpty() || 3 > emailABuscar.length()){
            inputBuscar.setError("Email no válido");
            return;
        }
        actualizarResultadoRecyclerView(emailABuscar);
    }

    private void actualizarResultadoRecyclerView(String emailABuscar) {
        Query query = FirebaseUtil.usuariosCollectionReference()
                .whereGreaterThanOrEqualTo("email",emailABuscar);

        FirestoreRecyclerOptions<Usuario> opt = new FirestoreRecyclerOptions
                .Builder<Usuario>()
                .setQuery(query,Usuario.class).build();
        buscarUsuarioRecyclerViewAdapter = new BuscarUsuarioRecyclerViewAdapter(opt,getApplicationContext());
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));//antes new LinearLayoutManager(this)
        recyclerView.setAdapter(buscarUsuarioRecyclerViewAdapter);
        buscarUsuarioRecyclerViewAdapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != buscarUsuarioRecyclerViewAdapter){
            buscarUsuarioRecyclerViewAdapter.startListening();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (buscarUsuarioRecyclerViewAdapter != null) {
            buscarUsuarioRecyclerViewAdapter.stopListening();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (null != buscarUsuarioRecyclerViewAdapter){
            buscarUsuarioRecyclerViewAdapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (buscarUsuarioRecyclerViewAdapter != null) {
            buscarUsuarioRecyclerViewAdapter.stopListening(); // Detener el adaptador
            buscarUsuarioRecyclerViewAdapter.startListening(); // Volver a iniciar el adaptador
        }
    }
    private void cargarEventoVolver(View view) {
        onBackPressed();
    }
}