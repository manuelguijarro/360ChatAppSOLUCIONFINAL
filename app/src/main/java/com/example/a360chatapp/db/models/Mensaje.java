package com.example.a360chatapp.db.models;

import com.google.firebase.Timestamp;

public class Mensaje {
    private String mensaje;
    private String idEmisor;
    private Timestamp timestamp;

    public Mensaje() {
    }

    public Mensaje(String mensaje, String idEmisor, Timestamp timestamp) {
        this.mensaje = mensaje;
        this.idEmisor = idEmisor;
        this.timestamp = timestamp;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getIdEmisor() {
        return idEmisor;
    }

    public void setIdEmisor(String idEmisor) {
        this.idEmisor = idEmisor;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
