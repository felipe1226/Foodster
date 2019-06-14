package com.app.foodster.Empresa;

import android.graphics.Bitmap;

public class ListaEventos {

    private int id;
    private String foto;
    private Bitmap bFoto;
    private String empresa;
    private String nombre;
    private String fecha;

    public ListaEventos(int id, String foto, String empresa, String nombre, String fecha) {
        this.id = id;
        this.foto = foto;
        this.empresa = empresa;
        this.nombre = nombre;
        this.fecha = fecha;
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

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
