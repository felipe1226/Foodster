package com.app.foodster.Producto;

import android.graphics.Bitmap;

public class ListaProductos {

    private int idEmpresa;
    private int idCarta;
    private int id;
    private String foto1;
    private String foto2;
    private String foto3;
    private Bitmap bFoto1;
    private Bitmap bFoto2;
    private Bitmap bFoto3;
    private String nombre;
    private String descripcion;
    private int precio;

    private int promocion;
    private String descPromocion;
    private int descuento;
    private String fecha;

    public ListaProductos(int idEmpresa, int idCarta, int id, String nombre, String descripcion, int precio,
                          int promocion) {
        this.idEmpresa = idEmpresa;
        this.idCarta = idCarta;
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.promocion = promocion;
    }

    public ListaProductos(int idEmpresa, int idCarta, int id, String nombre, String descProducto, int precio,
                          int promocion, String descPromocion, int descuento, String fecha) {
        this.idEmpresa = idEmpresa;
        this.idCarta = idCarta;
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.promocion = promocion;
        this.descPromocion = descPromocion;
        this.descuento = descuento;
        this.fecha = fecha;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getIdCarta() {
        return idCarta;
    }

    public void setIdCarta(int idCarta) {
        this.idCarta = idCarta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFoto1() {
        return foto1;
    }

    public void setFoto1(String foto1) {
        this.foto1 = foto1;
    }

    public String getFoto2() {
        return foto2;
    }

    public void setFoto2(String foto2) {
        this.foto2 = foto2;
    }

    public String getFoto3() {
        return foto3;
    }

    public void setFoto3(String foto3) {
        this.foto3 = foto3;
    }

    public Bitmap getbFoto1() {
        return bFoto1;
    }

    public void setbFoto1(Bitmap bFoto1) {
        this.bFoto1 = bFoto1;
    }

    public Bitmap getbFoto2() {
        return bFoto2;
    }

    public void setbFoto2(Bitmap bFoto2) {
        this.bFoto2 = bFoto2;
    }

    public Bitmap getbFoto3() {
        return bFoto3;
    }

    public void setbFoto3(Bitmap bFoto3) {
        this.bFoto3 = bFoto3;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public String getDescPromocion() {
        return descPromocion;
    }

    public void setDescPromocion(String descPromocion) {
        this.descPromocion = descPromocion;
    }

    public int getDescuento() {
        return descuento;
    }

    public void setDescuento(int descuento) {
        this.descuento = descuento;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
