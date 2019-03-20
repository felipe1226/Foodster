package com.app.foodster.Persona;


public class ListaCarrito {

    private int idCarrito;
    private int idProducto;
    private int idEmpresa;
    private String empresa;
    private String nombre;
    private int precio;
    private int promocion;
    private int descuento;
    private String detalles;

    public ListaCarrito(int idCarrito, int idProducto, int idEmpresa, String empresa, String nombre, int precio, int promocion,
                        int descuento, String detalles) {
        this.idCarrito = idCarrito;
        this.idProducto = idProducto;
        this.idEmpresa = idEmpresa;
        this.empresa = empresa;
        this.nombre = nombre;
        this.precio = precio;
        this.promocion = promocion;
        this.descuento = descuento;
        this.detalles = detalles;
    }

    public int getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(int idCarrito) {
        this.idCarrito = idCarrito;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
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

    public int getDescuento() {
        return descuento;
    }

    public void setDescuento(int descuento) {
        this.descuento = descuento;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }
}
