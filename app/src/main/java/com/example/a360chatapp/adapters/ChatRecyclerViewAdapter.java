package com.example.a360chatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a360chatapp.R;
import com.example.a360chatapp.activities.ChatIndividualActivity;
import com.example.a360chatapp.db.models.Chat;
import com.example.a360chatapp.db.models.Mensaje;
import com.example.a360chatapp.firebase.FirebaseUtil;
import com.example.a360chatapp.utils.IntentUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerViewAdapter extends FirestoreRecyclerAdapter<Mensaje, ChatRecyclerViewAdapter.ChatViewHolder> {

    private Context context;

    public ChatRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Mensaje> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder ChatViewHolder, int i, @NonNull Mensaje mensaje) {
        //Aqui comprobamos si el mensaje es emitido por una persona con nuestro id
        //si lo es aparece a la izkierda sino a la derecha
        if (mensaje.getIdEmisor().equals(FirebaseUtil.obtenerUsuarioUid())){
            ChatViewHolder.plantillaChatIzquierda.setVisibility(View.GONE);
            ChatViewHolder.plantillaChatDerecha.setVisibility(View.VISIBLE);
            ChatViewHolder.mensajeDerecha.setText(mensaje.getMensaje());
        }else{
            ChatViewHolder.plantillaChatDerecha.setVisibility(View.GONE);
            ChatViewHolder.plantillaChatIzquierda.setVisibility(View.VISIBLE);
            ChatViewHolder.mensajeIzquierda.setText(mensaje.getMensaje());
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mensaje_chat_recycler_row, parent, false);
        return new ChatViewHolder(view);
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout plantillaChatIzquierda;
        private TextView mensajeIzquierda;
        private LinearLayout plantillaChatDerecha;
        private TextView mensajeDerecha;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            plantillaChatIzquierda = itemView.findViewById(R.id.left_chat_layout);
            plantillaChatDerecha = itemView.findViewById(R.id.right_chat_layout);
            mensajeIzquierda = itemView.findViewById(R.id.left_chat_text);
            mensajeDerecha = itemView.findViewById(R.id.right_chat_text);
        }
    }
}
