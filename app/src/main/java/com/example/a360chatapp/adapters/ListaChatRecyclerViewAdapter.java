package com.example.a360chatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a360chatapp.R;
import com.example.a360chatapp.activities.ChatIndividualActivity;
import com.example.a360chatapp.db.models.Chat;
import com.example.a360chatapp.db.models.Usuario;
import com.example.a360chatapp.firebase.FirebaseUtil;
import com.example.a360chatapp.utils.GeneralUtil;
import com.example.a360chatapp.utils.IntentUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class ListaChatRecyclerViewAdapter extends FirestoreRecyclerAdapter<Chat, ListaChatRecyclerViewAdapter.ChatViewHolder> {

    private Context context;

    public ListaChatRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Chat> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i, @NonNull Chat chat) {
        FirebaseUtil.obtenerChatsUsuarios(chat.getIdUsuarios())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Usuario usuario = task.getResult().toObject(Usuario.class);

                        FirebaseUtil.obtenerOtraReferenciaStorage(usuario.getId()).getDownloadUrl().addOnCompleteListener(t -> {
                            if (t.isSuccessful()){
                                Uri uri = t.getResult();
                                GeneralUtil.setImagenPerfil(context,uri,chatViewHolder.imagenPerfil);
                            }
                        });

                        chatViewHolder.textViewEmailUsuario.setText(usuario.getNombre());
                        if (chat.getIdUltimoMensajeEmisor().equals(FirebaseUtil.obtenerUsuarioUid()) && null != chat.getUltimoMensaje()){
                            chatViewHolder.ultimoMensaje.setText("Tu : " + chat.getUltimoMensaje());
                            chatViewHolder.tiempoUltimoMensaje.setText(GeneralUtil.timestampToString(chat.getUltimoMensajeTimestamp()));

                        }else if (!chat.getIdUltimoMensajeEmisor().equals(FirebaseUtil.obtenerUsuarioUid()) && null != chat.getUltimoMensaje()) {
                            chatViewHolder.ultimoMensaje.setText(chat.getUltimoMensaje());
                            chatViewHolder.tiempoUltimoMensaje.setText(GeneralUtil.timestampToString(chat.getUltimoMensajeTimestamp()));

                        }
                        chatViewHolder.itemView.setOnClickListener(v -> {

                            //De aquí iremos al chat activity
                            Intent intent = new Intent(context, ChatIndividualActivity.class);
                            IntentUtil.enviarUsuarioIntent(intent,usuario);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
                    }
                });
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lista_chat_recycler_row, parent, false);
        return new ChatViewHolder(view);
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        private TextView ultimoMensaje;
        private TextView textViewEmailUsuario;
        private TextView tiempoUltimoMensaje;
        private ImageView imagenPerfil;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            ultimoMensaje = itemView.findViewById(R.id.ultimo_mensaje_text_view);
            textViewEmailUsuario = itemView.findViewById(R.id.email_usuario_text_view);
            tiempoUltimoMensaje = itemView.findViewById(R.id.ultimo_mensaje_tiempo_text_view);
            imagenPerfil = itemView.findViewById(R.id.perfil_imagen_view);
        }
    }
}
