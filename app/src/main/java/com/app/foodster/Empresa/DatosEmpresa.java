package com.app.foodster.Empresa;

public class DatosEmpresa {

    int id;
    String tipo;
    String categoria;
    String logo;
    String nombre;
    String banner;
    String descripcion;
    String direccion;
    String ubicacion;
    String telefono;
    String movil;
    String email;
    String ciudad;
    int domicilio;
    int pago;

    public DatosEmpresa(int id, String tipo, String categoria, String logo, String nombre, String banner, String descripcion, String direccion, String ubicacion,
                        String telefono, String movil, String email, String ciudad, int domicilio, int pago) {
        this.id = id;
        this.tipo = tipo;
        this.categoria = categoria;
        this.logo = logo;
        this.nombre = nombre;
        this.banner = banner;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.ubicacion = ubicacion;
        this.telefono = telefono;
        this.movil = movil;
        this.email = email;
        this.ciudad = ciudad;
        this.domicilio = domicilio;
        this.pago = pago;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMovil() {
        return movil;
    }

    public void setMovil(String movil) {
        this.movil = movil;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public int getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(int domicilio) {
        this.domicilio = domicilio;
    }

    public int getPago() {
        return pago;
    }

    public void setPago(int pago) {
        this.pago = pago;
    }
}
