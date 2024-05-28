package com.example.a360chatapp.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a360chatapp.R;
import com.example.a360chatapp.activities.ChatIndividualActivity;
import com.example.a360chatapp.db.models.Chat;
import com.example.a360chatapp.db.models.Mensaje;
import com.example.a360chatapp.firebase.FirebaseUtil;
import com.example.a360chatapp.utils.GeneralUtil;
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

        if (mensaje.isEsImagen()){
            if (mensaje.getIdEmisor().equals(FirebaseUtil.obtenerUsuarioUid())){
                ChatViewHolder.plantillaChatIzquierda.setVisibility(View.GONE);
                ChatViewHolder.plantillaChatDerecha.setVisibility(View.VISIBLE);

                ChatViewHolder.imageViewDerecha.setVisibility(View.VISIBLE);
                ChatViewHolder.mensajeDerecha.setVisibility(View.GONE);
                String[] idImagenPals = mensaje.getUrlImagen().split("_");
                String rutaBaseImagen = idImagenPals[0]+"_"+idImagenPals[1];
                String rutaCompleta = mensaje.getUrlImagen();
                descargarImagen(rutaBaseImagen,rutaCompleta,ChatViewHolder.imageViewDerecha);
            }else{
                ChatViewHolder.plantillaChatDerecha.setVisibility(View.GONE);
                ChatViewHolder.plantillaChatIzquierda.setVisibility(View.VISIBLE);

                ChatViewHolder.imageViewIzquierda.setVisibility(View.VISIBLE);
                ChatViewHolder.mensajeIzquierda.setVisibility(View.GONE);
                String[] idImagenPals = mensaje.getUrlImagen().split("_");
                String rutaBaseImagen = idImagenPals[0]+"_"+idImagenPals[1];
                String rutaCompleta = mensaje.getUrlImagen();
                descargarImagen(rutaBaseImagen,rutaCompleta,ChatViewHolder.imageViewIzquierda);


            }

        }else{
            if (mensaje.getIdEmisor().equals(FirebaseUtil.obtenerUsuarioUid())){
                ChatViewHolder.plantillaChatIzquierda.setVisibility(View.GONE);
                ChatViewHolder.plantillaChatDerecha.setVisibility(View.VISIBLE);
                ChatViewHolder.imageViewDerecha.setVisibility(View.GONE);
                ChatViewHolder.mensajeDerecha.setVisibility(View.VISIBLE);

                ChatViewHolder.mensajeDerecha.setText(mensaje.getMensaje());
            }else{
                ChatViewHolder.plantillaChatDerecha.setVisibility(View.GONE);
                ChatViewHolder.plantillaChatIzquierda.setVisibility(View.VISIBLE);

                ChatViewHolder.imageViewIzquierda.setVisibility(View.GONE);
                ChatViewHolder.mensajeIzquierda.setVisibility(View.VISIBLE);
                ChatViewHolder.mensajeIzquierda.setText(mensaje.getMensaje());
            }
        }
    }

    private void descargarImagen(String rutaBaseImagen, String rutaCompleta, ImageView imageViewChat) {
        FirebaseUtil.obtenerReferenciaCompletaDescargar(rutaBaseImagen, rutaCompleta).getDownloadUrl().addOnCompleteListener(t -> {
            if (t.isSuccessful()) {
                Uri uri = t.getResult();
                GeneralUtil.setImagenChat(context, uri, imageViewChat);

                imageViewChat.setOnClickListener(v -> {
                    descargarImagenEnDispositivo(uri);
                });
            }
        });
    }
    private void descargarImagenEnDispositivo(Uri uri) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        Toast.makeText(context, "descargando imagen", Toast.LENGTH_LONG).show();
        request.setTitle("Descargando imagen");
        request.setDescription("Descargando imagen desde el chat");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());

        if (downloadManager != null) {
            downloadManager.enqueue(request);
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
        private ImageView imageViewIzquierda;
        private TextView mensajeIzquierda;
        private LinearLayout plantillaChatDerecha;
        private ImageView imageViewDerecha;
        private TextView mensajeDerecha;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            plantillaChatIzquierda = itemView.findViewById(R.id.left_chat_layout);
            plantillaChatDerecha = itemView.findViewById(R.id.right_chat_layout);
            mensajeIzquierda = itemView.findViewById(R.id.left_chat_text);
            mensajeDerecha = itemView.findViewById(R.id.right_chat_text);
            imageViewIzquierda = itemView.findViewById(R.id.left_chat_imagen);
            imageViewDerecha = itemView.findViewById(R.id.right_chat_imagen);
        }
    }
}
