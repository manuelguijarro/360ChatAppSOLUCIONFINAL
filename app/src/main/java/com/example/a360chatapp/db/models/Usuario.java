package com.example.a360chatapp.db.models;

import com.google.firebase.Timestamp;

import java.util.LinkedList;
import java.util.List;

public class Usuario {
    private String id;
    private String nombre;
    private String email;
    private Timestamp fechaCreacion;
    private Boolean estadoConexion;
    private String tokenNotificacion;

    public Usuario() {
    }

    public Usuario(String id, String nombre , String email,Timestamp fechaCreacion ) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.estadoConexion = false;
        this.fechaCreacion = fechaCreacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public Boolean getEstadoConexion() {
        return estadoConexion;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setEstadoConexion(Boolean estadoConexion) {
        this.estadoConexion = estadoConexion;
    }

    public String getTokenNotificacion() {
        return tokenNotificacion;
    }

    public void setTokenNotificacion(String tokenNotificacion) {
        this.tokenNotificacion = tokenNotificacion;
    }
}
