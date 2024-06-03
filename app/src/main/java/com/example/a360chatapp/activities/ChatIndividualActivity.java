package com.example.a360chatapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        aplicarTema();
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
        usuarioChat = IntentUtil.obtenerUsuarioIntent(getIntent());
        idChat = FirebaseUtil.obtenerIdChat(FirebaseUtil.obtenerUsuarioUid(), usuarioChat.getId());
        cargarRecursosVista();
        cargarEventosBtn();
        ObtenerOCrearChat();
        actualizarUI();
        actualizarChatRecyclerView();
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
            ImageButton btnEnviarMensaje = findViewById(R.id.btn_enviar_mensaje);
            btnEnviarMensaje.setColorFilter(colorPrimary);

            ImageButton btnEnviarMensajeImagen = findViewById(R.id.btn_enviar_mensaje_imagen);
            btnEnviarMensajeImagen.setColorFilter(colorPrimary);
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
    private void actualizarChatRecyclerView() {
        Query query = FirebaseUtil.obtenerMensajeChatReferencia(idChat)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Mensaje> opt = new FirestoreRecyclerOptions
                .Builder<Mensaje>()
                .setQuery(query, Mensaje.class).build();
        chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(opt, getApplicationContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
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
        mensajeInput = findViewById(R.id.mensaje_chat_input);
        btnEnviarMensajeImagen = findViewById(R.id.btn_enviar_mensaje_imagen);
        btnEnviarMensaje = findViewById(R.id.btn_enviar_mensaje);
        btnVolver = findViewById(R.id.btn_volver_chat);
        nombreUsuario = findViewById(R.id.nombre_usuario_chat);
        nombreUsuario.setText(usuarioChat.getNombre());
        recyclerView = findViewById(R.id.chat_recycler_view);
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
                                        if (taskSecond.isSuccessful()) {
                                            Toast.makeText(this, "Imagen enviada correctamente", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(this, "Error al enviar la imagen", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
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
                        enviarNotificacionServidor(mensaje);
                    }
                });
    }

    private void enviarNotificacionServidor(String mensaje) {

        FirebaseUtil.obtenerDetallesUsuarioActual().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Usuario usuarioActual = task.getResult().toObject(Usuario.class);

                try {
                    String tittle = usuarioActual.getNombre();
                    String body = mensaje;
                    String id = usuarioActual.getId();
                    String token = usuarioChat.getTokenNotificacion();
                    String url = "https://server-bender-express.onrender.com/notifications";
                    url += "?tittle=" + tittle + "&body=" + body + "&id=" + id + "&to=" + token;
                    callApi(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void callApi(String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("API_CALL", "Error sending request", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("API_CALL", "Request successful: " + response.body().string());
                } else {
                    Log.e("API_CALL", "Request failed: " + response.body().string());
                }
            }
        });
    }

    private void cargarEventoVolver(View view) {
        onBackPressed();
    }

}
