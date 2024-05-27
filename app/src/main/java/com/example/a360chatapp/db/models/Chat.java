package com.example.a360chatapp.db.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class Chat {
    private String id;
    private List<String> idUsuarios;
    private String ultimoMensaje;
    private Timestamp ultimoMensajeTimestamp;
    private String idUltimoMensajeEmisor;

    public Chat() {
    }

    public Chat(String id, List<String> idUsuarios, Timestamp ultimoMensajeTimestamp, String idUltimoMensajeEnviado) {
        this.id = id;
        this.idUsuarios = idUsuarios;
        this.ultimoMensajeTimestamp = ultimoMensajeTimestamp;
        this.idUltimoMensajeEmisor = idUltimoMensajeEnviado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getIdUsuarios() {
        return idUsuarios;
    }

    public void setIdUsuarios(List<String> idUsuarios) {
        this.idUsuarios = idUsuarios;
    }

    public Timestamp getUltimoMensajeTimestamp() {
        return ultimoMensajeTimestamp;
    }

    public void setUltimoMensajeTimestamp(Timestamp ultimoMensajeTimestamp) {
        this.ultimoMensajeTimestamp = ultimoMensajeTimestamp;
    }

    public String getIdUltimoMensajeEmisor() {
        return idUltimoMensajeEmisor;
    }

    public void setIdUltimoMensajeEmisor(String idUltimoMensajeEnviado) {
        this.idUltimoMensajeEmisor = idUltimoMensajeEnviado;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(String ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }
}
