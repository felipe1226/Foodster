package com.app.foodster.Persona;

public class ListaProductosHistorico {

    private int id;
    private int idPedido;
    private String nombre;
    private int precio;
    private int promocion;
    private String detalles;

    public ListaProductosHistorico(int id, int idPedido, String nombre, int precio, int promocion, String detalles) {
        this.id = id;
        this.idPedido = idPedido;
        this.nombre = nombre;
        this.precio = precio;
        this.promocion = promocion;
        this.detalles = detalles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getPromocion() {
        return promocion;
    }

    public void setPromocion(int promocion) {
        this.promocion = promocion;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }
}
