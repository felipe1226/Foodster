package com.app.foodster.Persona;

public class ListaHistoricoPedidos {

    private int id;
    private String empresa;
    private String fecha;
    private String pago;
    private int total;

    public ListaHistoricoPedidos(int id, String empresa, String pago, int total, String fecha) {
        this.id = id;
        this.empresa = empresa;
        this.pago = pago;
        this.total = total;
        this.fecha = fecha;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
