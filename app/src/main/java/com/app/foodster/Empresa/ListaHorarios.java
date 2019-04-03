package com.app.foodster.Empresa;

public class ListaHorarios {

    private int idEmpresa;
    private String dia;
    private String apertura;
    private String cierre;
    private int estado;

    public ListaHorarios(int idEmpresa, String dia, String apertura, String cierre, int estado) {
        this.idEmpresa = idEmpresa;
        this.dia = dia;
        this.apertura = apertura;
        this.cierre = cierre;
        this.estado = estado;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getApertura() {
        return apertura;
    }

    public void setApertura(String apertura) {
        this.apertura = apertura;
    }

    public String getCierre() {
        return cierre;
    }

    public void setCierre(String cierre) {
        this.cierre = cierre;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
