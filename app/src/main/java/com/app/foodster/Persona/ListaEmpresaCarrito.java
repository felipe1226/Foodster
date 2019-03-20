package com.app.foodster.Persona;

public class ListaEmpresaCarrito {

    private int id;
    private String empresa;
    private int total;

    public ListaEmpresaCarrito(int id, String empresa, int total) {
        this.id = id;
        this.empresa = empresa;
        this.total = total;
    }

    public ListaEmpresaCarrito(int id, String empresa) {
        this.id = id;
        this.empresa = empresa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
