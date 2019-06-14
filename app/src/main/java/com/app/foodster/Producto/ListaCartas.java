package com.app.foodster.Producto;

public class ListaCartas {

    private int id;
    private int idEmpresa;
    private String carta;

    public ListaCartas(int id, String carta) {
        this.id = id;
        this.carta = carta;
    }

    public ListaCartas(int id, int idEmpresa, String carta) {
        this.id = id;
        this.idEmpresa = idEmpresa;
        this.carta = carta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getCarta() {
        return carta;
    }

    public void setCarta(String carta) {
        this.carta = carta;
    }
}
