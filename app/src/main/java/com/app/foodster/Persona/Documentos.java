package com.app.foodster.Persona;

public class Documentos {

    private int id;
    private String tipo;
    private String abreviacion;

    public Documentos(int id, String tipo, String abreviacion) {
        this.id = id;
        this.tipo = tipo;
        this.abreviacion = abreviacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAbreviacion() {
        return abreviacion;
    }

    public void setAbreviacion(String abreviacion) {
        this.abreviacion = abreviacion;
    }
}
