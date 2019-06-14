package com.app.foodster.Persona;

public class ListaTarjetas {

    private int id;
    private String nombre;
    private String tipo;
    private String numero;
    private String fecha;

    public ListaTarjetas(int id, String nombre, String tipo, String numero, String fecha) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.numero = numero;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
