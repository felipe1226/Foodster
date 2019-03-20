package com.app.foodster.Producto;

public class ListaCartas {

    private int id;
    private String carta;

    public ListaCartas(int id, String carta) {
        this.id = id;
        this.carta = carta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarta() {
        return carta;
    }

    public void setCarta(String carta) {
        this.carta = carta;
    }
}
