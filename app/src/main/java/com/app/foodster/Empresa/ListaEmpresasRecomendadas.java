package com.app.foodster.Empresa;

import android.graphics.Bitmap;

public class ListaEmpresasRecomendadas {

    private int id;
    private int idEmpresa;
    private String foto;
    private Bitmap bFoto;
    private String nombre;
    private String categoria;
    private String fecha;

    public ListaEmpresasRecomendadas(int id,int idEmpresa, String foto, String nombre, String categoria, String fecha) {
        this.id = id;
        this.idEmpresa = idEmpresa;
        this.foto = foto;
        this.nombre = nombre;
        this.categoria = categoria;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
