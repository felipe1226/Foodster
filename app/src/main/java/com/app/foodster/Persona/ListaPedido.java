package com.app.foodster.Persona;

public class ListaPedido {

    private int id;
    private String empresa;
    private String estado;
    private int cola;
    private String pago;
    private int total;

    public ListaPedido(int id, String empresa, String estado, int cola, String pago, int total) {
        this.id = id;
        this.empresa = empresa;
        this.estado = estado;
        this.cola = cola;
        this.pago = pago;
        this.total = total;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getCola() {
        return cola;
    }

    public void setCola(int cola) {
        this.cola = cola;
    }

    public String getPago() {
        return pago;
    }

    public void setPago(String pago) {
        this.pago = pago;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
