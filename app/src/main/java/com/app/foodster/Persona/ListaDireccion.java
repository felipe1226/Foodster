package com.app.foodster.Persona;

public class ListaDireccion {

    private int id;
    private String titulo;
    private String direccion;
    private String ubicacion;
    private int predeterminada;

    public ListaDireccion(int id, String titulo, String direccion, String ubicacion, int predeterminada) {
        this.id = id;
        this.titulo = titulo;
        this.direccion = direccion;
        this.ubicacion = ubicacion;
        this.predeterminada = predeterminada;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getPredeterminada() {
        return predeterminada;
    }

    public void setPredeterminada(int predeterminada) {
        this.predeterminada = predeterminada;
    }
}
