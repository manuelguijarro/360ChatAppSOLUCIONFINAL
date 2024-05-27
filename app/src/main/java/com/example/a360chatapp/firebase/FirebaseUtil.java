package com.example.a360chatapp.firebase;


import com.example.a360chatapp.db.models.Usuario;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {


    public static String obtenerUsuarioUid() {
        String usuarioUid = null;
        try {
            usuarioUid = FirebaseAuth.getInstance().getUid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuarioUid;
    }
    public static boolean estaUsuarioLogeado(){
        if (null != obtenerUsuarioUid()){
            return true;
        }else {
            return false;
        }
    }
    public static String obtenerUsuarioEmail() {
        String emailUsuario = "";
        try {
            emailUsuario = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emailUsuario;
    }

    public static DocumentReference obtenerDetallesUsuarioActual() {
        DocumentReference referenciaDocumentoUsuario = null;
        try {
            referenciaDocumentoUsuario = FirebaseFirestore.getInstance().collection("users").document(obtenerUsuarioUid());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return referenciaDocumentoUsuario;
    }

    public static CollectionReference usuariosCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference obtenerReferenciaChat(String idChat){
        DocumentReference referenciaDocumentoChat = null;
        try{
            referenciaDocumentoChat = FirebaseFirestore.getInstance().collection("chatrooms").document(idChat);
        }catch (Exception e){
            e.printStackTrace();
        }
        return referenciaDocumentoChat;
    }

    public static String obtenerIdChat(String idUsuario1, String idUsuario2){
        if (idUsuario1.hashCode() < idUsuario2.hashCode())
            return idUsuario1 + "_" + idUsuario2;
        else{
            return idUsuario2 + "_" + idUsuario1;
        }
    }
    public static CollectionReference obtenerMensajeChatReferencia(String idChat){
        return obtenerReferenciaChat(idChat).collection("chats");
    }

    public static CollectionReference obtenerListadoChatCollectionReference(){

        return FirebaseFirestore.getInstance().collection("chatrooms");

    }


    public static DocumentReference obtenerChatsUsuarios(List<String> idsUsuario){
        if (idsUsuario.get(0).equals(FirebaseUtil.obtenerUsuarioUid())){
            return usuariosCollectionReference().document(idsUsuario.get(1));
        }else {
            return usuariosCollectionReference().document(idsUsuario.get(0));
        }
    }
    public static void cerrarSesion(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference obtenerReferenciaStorage(){
        return FirebaseStorage
                .getInstance()
                .getReference()
                .child("imagen_perfil")
                .child(obtenerUsuarioUid());
    }
    public static StorageReference obtenerOtraReferenciaStorage(String idOtroUsuario){
        return FirebaseStorage
                .getInstance()
                .getReference()
                .child("imagen_perfil")
                .child(idOtroUsuario);
    }

}
