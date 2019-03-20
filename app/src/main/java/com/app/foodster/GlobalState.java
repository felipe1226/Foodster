package com.app.foodster;

import android.app.Application;
import android.support.v4.app.Fragment;

import com.app.foodster.Empresa.DatosEmpresa;
import com.app.foodster.Persona.ListaCarrito;
import com.app.foodster.Persona.ListaDireccion;
import com.app.foodster.Persona.ListaEmpresaCarrito;
import com.app.foodster.Persona.ListaProductosFavoritos;
import com.app.foodster.Producto.ListaProductos;

import java.util.ArrayList;

public class GlobalState extends Application {

    public String ip = "foodster.com.co/consultasAndroid";

    public Fragment fragment = null;
    public Fragment fragmentEmpresas = null;
    public Fragment fragmentCarrito = null;

    public String fragmentActual = null;
    public String fragmentActualEmpresas = null;
    public String fragmentActualCarritos = null;

    public String usuario = "";
    public String password = "";

    public int idPersona = 0;
    public String nombre = "";
    public String telefono = "";
    public String email = "";
    public int idCiudad = 0;

    public boolean actualizaEmpresas = true;
    public boolean actualizaCarrito =  true;
    public boolean actualizaProductosFavoritos =  true;
    public boolean actualizaDirecciones =  true;

    ArrayList<DatosEmpresa> datosEmpresa = null;
    ArrayList<ListaProductos> datosProducto = new ArrayList<>();
    ArrayList<ListaEmpresaCarrito> datosEmpresaCarrito = null;
    ArrayList<ListaCarrito> datosCarrito = null;
    ArrayList<ListaDireccion> datosDireccion = null;
    ArrayList<ListaProductosFavoritos> datosProductosFavoritos = null;
    ArrayList<String> categorias = null;
    ArrayList<String> filtroCategorias = null;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment getFragmentEmpresas() {
        return fragmentEmpresas;
    }

    public void setFragmentEmpresas(Fragment fragmentEmpresas) {
        this.fragmentEmpresas = fragmentEmpresas;
    }

    public Fragment getFragmentCarrito() {
        return fragmentCarrito;
    }

    public void setFragmentCarrito(Fragment fragmentCarrito) {
        this.fragmentCarrito = fragmentCarrito;
    }

    public String getFragmentActualEmpresas() {
        return fragmentActualEmpresas;
    }

    public String getFragmentActual() {
        return fragmentActual;
    }

    public void setFragmentActual(String fragmentActual) {
        this.fragmentActual = fragmentActual;
    }

    public void setFragmentActualEmpresas(String fragmentActualEmpresas) {
        this.fragmentActualEmpresas = fragmentActualEmpresas;
    }

    public String getFragmentActualCarritos() {
        return fragmentActualCarritos;
    }

    public void setFragmentActualCarritos(String fragmentActualCarritos) {
        this.fragmentActualCarritos = fragmentActualCarritos;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getidCiudad() {
        return idCiudad;
    }

    public void setidCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }


    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }




    public boolean isActualizaEmpresas() {
        return actualizaEmpresas;
    }

    public void setActualizaEmpresas(boolean actualizaEmpresas) {
        this.actualizaEmpresas = actualizaEmpresas;
    }

    public boolean isActualizaCarrito() {
        return actualizaCarrito;
    }

    public void setActualizaCarrito(boolean actualizaCarrito) {
        this.actualizaCarrito = actualizaCarrito;
    }

    public boolean isActualizaDirecciones() {
        return actualizaDirecciones;
    }

    public void setActualizaDirecciones(boolean actualizaDirecciones) {
        this.actualizaDirecciones = actualizaDirecciones;
    }

    public boolean isActualizaProductosFavoritos() {
        return actualizaProductosFavoritos;
    }

    public void setActualizaProductosFavoritos(boolean actualizaProductosFavoritos) {
        this.actualizaProductosFavoritos = actualizaProductosFavoritos;
    }

    public ArrayList<DatosEmpresa> getDatosEmpresa() {
        return datosEmpresa;
    }

    public void setDatosEmpresa(ArrayList<DatosEmpresa> datosEmpresa) {
        this.datosEmpresa = datosEmpresa;
    }

    public ArrayList<ListaProductos> getDatosProducto() {
        return datosProducto;
    }

    public void setDatosProducto(ArrayList<ListaProductos> datosProducto) {
        this.datosProducto = datosProducto;
    }

    public void addDatosProducto(ArrayList<ListaProductos> datosProducto) {
        for(int i=0;i<datosProducto.size();i++){
            this.datosProducto.add(datosProducto.get(i));
        }
    }

    public ArrayList<ListaEmpresaCarrito> getDatosEmpresaCarrito() {
        return datosEmpresaCarrito;
    }

    public void setDatosEmpresaCarrito(ArrayList<ListaEmpresaCarrito> datosEmpresaCarrito) {
        this.datosEmpresaCarrito = datosEmpresaCarrito;
    }

    public ArrayList<ListaCarrito> getDatosCarrito() {
        return datosCarrito;
    }

    public void setDatosCarrito(ArrayList<ListaCarrito> datosCarrito) {
        this.datosCarrito = datosCarrito;
    }

    public ArrayList<ListaDireccion> getDatosDireccion() {
        return datosDireccion;
    }

    public void setDatosDireccion(ArrayList<ListaDireccion> datosDireccion) {
        this.datosDireccion = datosDireccion;
    }

    public ArrayList<ListaProductosFavoritos> getDatosProductosFavoritos() {
        return datosProductosFavoritos;
    }

    public void setDatosProductosFavoritos(ArrayList<ListaProductosFavoritos> datosProductosFavoritos) {
        this.datosProductosFavoritos = datosProductosFavoritos;
    }

    public ArrayList<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(ArrayList<String> categorias) {
        this.categorias = categorias;
    }

    public ArrayList<String> getFiltroCategorias() {
        return filtroCategorias;
    }

    public void setFiltroCategorias(ArrayList<String> filtroCategorias) {
        this.filtroCategorias = filtroCategorias;
    }
}
