package com.app.foodster.Empresa;

import android.graphics.Bitmap;

public class ListaEmpresas {

    private int id;
    private Bitmap foto;
    private String nombre;

    public ListaEmpresas(int id, Bitmap foto, String nombre) {
        this.id = id;
        this.foto = foto;
        this.nombre = nombre;
    }

    public ListaEmpresas(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
