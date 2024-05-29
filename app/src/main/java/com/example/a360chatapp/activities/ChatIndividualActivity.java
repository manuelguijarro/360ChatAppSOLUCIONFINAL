package com.example.a360chatapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a360chatapp.R;
import com.example.a360chatapp.adapters.ChatRecyclerViewAdapter;
import com.example.a360chatapp.db.models.Chat;
import com.example.a360chatapp.db.models.Mensaje;
import com.example.a360chatapp.db.models.Usuario;
import com.example.a360chatapp.firebase.FirebaseUtil;
import com.example.a360chatapp.utils.GeneralUtil;
import com.example.a360chatapp.utils.IntentUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.UUID;

public class ChatIndividualActivity extends AppCompatActivity {
    private Chat chat;
    private String idChat;
    private Usuario usuarioChat;
    private TextView nombreUsuario;
    private EditText mensajeInput;
    private ImageButton btnEnviarMensaje;
    private ImageButton btnVolver;
    private ImageButton btnEnviarMensajeImagen;
    private RecyclerView recyclerView;
    private ChatRecyclerViewAdapter chatRecyclerViewAdapter;
    private ImageView imageViewPerfilUsuario;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri imagenSeleccionadaUri;
    private ProgressBar progressBarChatIndividualView;
    private TextView enviandoImagenTexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_individual);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            imagenSeleccionadaUri = data.getData();
                            enviarImagenSeleccionada();
                        }
                    }
                });
        // Objeto usuario
        usuarioChat = IntentUtil.obtenerUsuarioIntent(getIntent());
        idChat = FirebaseUtil.obtenerIdChat(FirebaseUtil.obtenerUsuarioUid(), usuarioChat.getId());
        cargarRecursosVista();
        cargarEventosBtn();
        ObtenerOCrearChat();
        actualizarChatRecyclerView();
    }

    private void actualizarChatRecyclerView() {
        Query query = FirebaseUtil.obtenerMensajeChatReferencia(idChat)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Mensaje> opt = new FirestoreRecyclerOptions
                .Builder<Mensaje>()
                .setQuery(query, Mensaje.class).build();
        chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(opt, getApplicationContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // Para que los mensajes se muestren de abajo hacia arriba
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatRecyclerViewAdapter);
        chatRecyclerViewAdapter.startListening();
        chatRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void ObtenerOCrearChat() {
        FirebaseUtil.obtenerReferenciaChat(idChat).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chat = task.getResult().toObject(Chat.class);
                if (chat == null) {
                    chat = new Chat(
                            idChat,
                            Arrays.asList(FirebaseUtil.obtenerUsuarioUid(), usuarioChat.getId()),
                            Timestamp.now(),
                            "");
                }
                FirebaseUtil.obtenerReferenciaChat(idChat).set(chat);
            }
        });
    }

    private void cargarRecursosVista() {
        // Input
        mensajeInput = findViewById(R.id.mensaje_chat_input);
        // Botones
        btnEnviarMensajeImagen = findViewById(R.id.btn_enviar_mensaje_imagen);
        btnEnviarMensaje = findViewById(R.id.btn_enviar_mensaje);
        btnVolver = findViewById(R.id.btn_volver_chat);
        // ProgressBar
        progressBarChatIndividualView = findViewById(R.id.progressBarChatIndividual);
        // TextView
        nombreUsuario = findViewById(R.id.nombre_usuario_chat);
        enviandoImagenTexto = findViewById(R.id.enviando_imagen_texto);
        // Cargar el nombre del usuario
        nombreUsuario.setText(usuarioChat.getNombre());
        // RecyclerView
        recyclerView = findViewById(R.id.chat_recycler_view);
        // Imagen
        imageViewPerfilUsuario = findViewById(R.id.perfil_imagen_view);
        FirebaseUtil.obtenerOtraReferenciaStorage(usuarioChat.getId()).getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri uri = task.getResult();
                GeneralUtil.setImagenPerfil(this, uri, imageViewPerfilUsuario);
            }
        });
    }

    private void cargarEventosBtn() {
        btnVolver.setOnClickListener(this::cargarEventoVolver);
        btnEnviarMensaje.setOnClickListener(this::obtenerMensaje);
        btnEnviarMensajeImagen.setOnClickListener(this::obtenerImagen);
    }

    private void enviarImagenSeleccionada() {
        if (imagenSeleccionadaUri != null) {
            //setEnProgreso(true);
            Toast.makeText(this, "Enviando imagen", Toast.LENGTH_SHORT).show();
            String uniqueID = UUID.randomUUID().toString();
            String mensaje = "imagen.jpg";
            String urlImagen = idChat + "_" + uniqueID;

            chat.setUltimoMensajeTimestamp(Timestamp.now());
            chat.setIdUltimoMensajeEmisor(FirebaseUtil.obtenerUsuarioUid());
            chat.setUltimoMensaje(mensaje);
            FirebaseUtil.obtenerReferenciaChat(idChat).set(chat);

            Mensaje mensajeModel = new Mensaje(mensaje, FirebaseUtil.obtenerUsuarioUid(), Timestamp.now(), true, urlImagen);

            FirebaseUtil.obtenerReferenciaStorageChatImagenes(FirebaseUtil.obtenerUsuarioUid(), usuarioChat.getId(), urlImagen)
                    .putFile(imagenSeleccionadaUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUtil.obtenerMensajeChatReferencia(idChat).add(mensajeModel)
                                    .addOnCompleteListener(taskSecond -> {
                                        //setEnProgreso(false);
                                        if (taskSecond.isSuccessful()) {
                                            Toast.makeText(this, "Imagen enviada correctamente", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(this, "Error al enviar la imagen", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                           // setEnProgreso(false);
                            Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void obtenerImagen(View view) {
        ImagePicker.with(this)
                .cropSquare()
                .compress(512)
                .maxResultSize(512, 512)
                .createIntent(intent -> {
                    imagePickerLauncher.launch(intent);
                    return null;
                });
    }

    private void obtenerMensaje(View view) {
        String mensaje = mensajeInput.getText().toString().trim();
        if (mensaje.isEmpty()) {
            mensajeInput.setError("El mensaje no puede estar vacío");
            return;
        }
        enviarMensajeAUsuario(mensaje);
    }

    private void enviarMensajeAUsuario(String mensaje) {
        chat.setUltimoMensajeTimestamp(Timestamp.now());
        chat.setIdUltimoMensajeEmisor(FirebaseUtil.obtenerUsuarioUid());
        chat.setUltimoMensaje(mensaje);
        FirebaseUtil.obtenerReferenciaChat(idChat).set(chat);

        Mensaje mensajeModel = new Mensaje(mensaje, FirebaseUtil.obtenerUsuarioUid(), Timestamp.now(), false, "");

        FirebaseUtil.obtenerMensajeChatReferencia(idChat).add(mensajeModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mensajeInput.setText("");
                    }
                });
    }

    private void cargarEventoVolver(View view) {
        onBackPressed();
    }
/*
    private void setEnProgreso(boolean enProgreso) {
        if (enProgreso) {
            btnEnviarMensaje.setVisibility(View.GONE);
            btnEnviarMensajeImagen.setVisibility(View.GONE);
            mensajeInput.setVisibility(View.GONE);
            btnVolver.setVisibility(View.INVISIBLE);
            progressBarChatIndividualView.setVisibility(View.VISIBLE);
            enviandoImagenTexto.setVisibility(View.VISIBLE);
        } else {
            progressBarChatIndividualView.setVisibility(View.GONE);
            enviandoImagenTexto.setVisibility(View.GONE);
            mensajeInput.setVisibility(View.VISIBLE);
            btnEnviarMensaje.setVisibility(View.VISIBLE);
            btnEnviarMensajeImagen.setVisibility(View.VISIBLE);
            btnVolver.setVisibility(View.VISIBLE);
        }
    }
    */
}
