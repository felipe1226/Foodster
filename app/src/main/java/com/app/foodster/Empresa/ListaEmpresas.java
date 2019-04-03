package com.app.foodster.Empresa;

import android.graphics.Bitmap;

public class ListaEmpresas {

    private int id;
    private String foto;
    private Bitmap bFoto;
    private String nombre;
    private String categoria;

    public ListaEmpresas(int id, String foto, String nombre, String categoria) {
        this.id = id;
        this.foto = foto;
        this.nombre = nombre;
        this.categoria = categoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Bitmap getbFoto() {
        return bFoto;
    }

    public void setbFoto(Bitmap bFoto) {
        this.bFoto = bFoto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
