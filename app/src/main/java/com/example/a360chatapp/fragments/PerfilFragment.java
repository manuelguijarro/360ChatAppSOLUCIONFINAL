package com.example.a360chatapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a360chatapp.R;
import com.example.a360chatapp.activities.SplashScreen;
import com.example.a360chatapp.db.models.Usuario;
import com.example.a360chatapp.firebase.FirebaseUtil;
import com.example.a360chatapp.utils.GeneralUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class PerfilFragment extends Fragment {
    private ImageView imagenPerfil;
    private EditText editTextNombreUsuario;
    private EditText editTextEmailUsuario;
    private ProgressBar progressBarPerfil;
    private Button btnActualizarPerfil;
    private TextView btnCerrarSesion;
    private Usuario usuario;
    private ActivityResultLauncher<Intent>imagePickerLauncher;
    private Uri imagenSeleccionadaUri;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result ->{
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (null != data && null != data.getData()){
                            imagenSeleccionadaUri = data.getData();
                            GeneralUtil.setImagenPerfil(getContext(),imagenSeleccionadaUri,imagenPerfil);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        cargarRecursosVista(view);
        cargarDatosUsuarioActual();
        cargarEventosBtn();
        return view;



    }

    private void cargarRecursosVista(View view) {
        //Imagen
        imagenPerfil = view.findViewById(R.id.imagen_usuario_perfil);
        //EditText
        editTextNombreUsuario = view.findViewById(R.id.nombre_usuario_perfil);
        editTextEmailUsuario = view.findViewById(R.id.email_usuario_perfil);
        //Boton
        btnActualizarPerfil = view.findViewById(R.id.btn_enviar_datos_perfil);
        //TextView -> Boton
        btnCerrarSesion = view.findViewById(R.id.cerrar_sesion_btn);
        //ProgressBar Perfil
        progressBarPerfil = view.findViewById(R.id.barra_progreso_perfil);
    }
    private void cargarEventosBtn() {
        btnActualizarPerfil.setOnClickListener(this::actualizarPerfilUsuario);
        btnCerrarSesion.setOnClickListener(this::cerrarSesionPerfilUsuario);
        imagenPerfil.setOnClickListener(this::actualizarImagenPerfilUsuario);
    }
    private void actualizarImagenPerfilUsuario(View view){
        ImagePicker.with(this)
                .cropSquare()
                .compress(512)
                .maxResultSize(512,512)
                .createIntent(intent -> {
                    imagePickerLauncher.launch(intent);
                    return null;

                });
    }
    private void actualizarPerfilUsuario(View view) {
        String nuevoNombreUsuario = editTextNombreUsuario.getText().toString().trim();
        if (nuevoNombreUsuario.isEmpty() || 3 > nuevoNombreUsuario.length()){
            editTextNombreUsuario.setError("El nombre de usuario tiene que tener mínimo 3 caracteres");
            return;
        }
        usuario.setNombre(nuevoNombreUsuario);
        setEnProgreso(true);
        if (null != imagenSeleccionadaUri){
            FirebaseUtil.obtenerReferenciaStorage()
                    .putFile(imagenSeleccionadaUri)
                    .addOnCompleteListener(task -> {
                        actualizarDatosEnFirestore();
            });
        }else{
            actualizarDatosEnFirestore();
        }

    }
    private void actualizarDatosEnFirestore(){
        FirebaseUtil.obtenerDetallesUsuarioActual().set(usuario).addOnCompleteListener(task -> {
            setEnProgreso(false);
            if (task.isSuccessful()){
                Toast.makeText(getContext(),"Perfil actualizado correctamente",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getContext(),"Fallo al actualizar el perfil",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void cerrarSesionPerfilUsuario(View view){
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FirebaseUtil.cerrarSesion();
                Intent intent = new Intent(getContext(),SplashScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }
    private void cargarDatosUsuarioActual(){
        setEnProgreso(true);
        FirebaseUtil.obtenerReferenciaStorage().getDownloadUrl().addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               Uri uri = task.getResult();
               GeneralUtil.setImagenPerfil(getContext(),uri,imagenPerfil);
           }
        });
        FirebaseUtil.obtenerDetallesUsuarioActual().get().addOnCompleteListener(task -> {
            setEnProgreso(false);
            usuario = task.getResult().toObject(Usuario.class);
            editTextNombreUsuario.setText(usuario.getNombre());
            editTextEmailUsuario.setText(usuario.getEmail());
        });
    }

    private void setEnProgreso(boolean enProgreso){
        if (enProgreso){
            progressBarPerfil.setVisibility(View.VISIBLE);
            btnActualizarPerfil.setVisibility(View.GONE);
        }else {
            progressBarPerfil.setVisibility(View.GONE);
            btnActualizarPerfil.setVisibility(View.VISIBLE);

        }
    }
}