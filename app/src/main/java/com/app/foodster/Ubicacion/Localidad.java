package com.app.foodster.Ubicacion;

public class Localidad {

    private int idDepartamento;
    private String departamento;
    private int idCiudad;
    private String ciudad;

    public Localidad(int idDepartamento, String departamento, int idCiudad, String ciudad) {
        this.idDepartamento = idDepartamento;
        this.departamento = departamento;
        this.idCiudad = idCiudad;
        this.ciudad = ciudad;
    }


    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}
