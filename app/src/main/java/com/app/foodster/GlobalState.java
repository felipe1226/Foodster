package com.app.foodster;

import android.app.Application;
import android.support.v4.app.Fragment;

import com.app.foodster.Empresa.DatosEmpresa;
import com.app.foodster.Empresa.ListaEmpresasRecomendadas;
import com.app.foodster.Empresa.ListaHorarios;
import com.app.foodster.Persona.HiloPedidos;
import com.app.foodster.Persona.ListaCarrito;
import com.app.foodster.Persona.ListaDireccion;
import com.app.foodster.Persona.ListaEmpresaCarrito;
import com.app.foodster.Persona.ListaHistoricoPedidos;
import com.app.foodster.Persona.ListaPedido;
import com.app.foodster.Persona.ListaProductosHistorico;
import com.app.foodster.Persona.ListaProductosPedido;
import com.app.foodster.Persona.ListaProductosFavoritos;
import com.app.foodster.Producto.ListaProductos;
import com.app.foodster.Ubicacion.Localidad;

import java.util.ArrayList;

public class GlobalState extends Application {

    public String ip = "foodster.com.co/consultasAndroid";

    public String busqueda = "";

    ArrayList<Localidad> localidades = new ArrayList<>();

    public Principal principal = null;
    public HiloPedidos hiloPedidos = null;

    public Fragment fragment = null;

    public String fragmentActual = null;

    public String usuario = "";
    public String password = "";

    public int idPersona = 0;
    public String nombre = "";
    public String telefono = "";
    public String email = "";
    public String departamento = "";
    public String ciudad = "";

    public boolean existePedidos = false;
    public boolean actualizaEmpresas = true;
    public boolean actualizaCarrito =  true;
    public boolean actualizaPedido =  true;
    public boolean actualizaHistorico =  true;
    public boolean actualizaProductosFavoritos =  true;
    public boolean actualizaDirecciones =  true;

    ArrayList<ListaEmpresasRecomendadas> datosRecomendadas = new ArrayList<>();
    ArrayList<DatosEmpresa> datosEmpresa = null;
    ArrayList<ListaHorarios> datosHorarios = new ArrayList<>();
    ArrayList<ListaProductos> datosProducto = new ArrayList<>();

    ArrayList<ListaEmpresaCarrito> datosEmpresaCarrito = null;
    ArrayList<ListaCarrito> datosCarrito = null;

    ArrayList<ListaPedido> datosPedido = null;
    ArrayList<ListaProductosPedido> datosProductoPedido = null;

    ArrayList<ListaHistoricoPedidos> datosHistorico = null;
    ArrayList<ListaProductosHistorico> datosProductoHistorico = null;

    ArrayList<ListaDireccion> datosDireccion = null;
    ArrayList<ListaProductosFavoritos> datosProductosFavoritos = null;
    ArrayList<String> categorias = null;
    public ArrayList<String> filtroCategorias = null;


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBusqueda() {
        return busqueda;
    }

    public void setBusqueda(String busqueda) {
        this.busqueda = busqueda;
    }

    public ArrayList<Localidad> getLocalidades() {
        return localidades;
    }

    public void setLocalidades(ArrayList<Localidad> localidades) {
        this.localidades = localidades;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    public HiloPedidos getHiloPedidos() {
        return hiloPedidos;
    }

    public void setHiloPedidos(HiloPedidos hiloPedidos) {
        this.hiloPedidos = hiloPedidos;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getFragmentActual() {
        return fragmentActual;
    }

    public void setFragmentActual(String fragmentActual) {
        this.fragmentActual = fragmentActual;
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

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public boolean isExistePedidos() {
        return existePedidos;
    }

    public void setExistePedidos(boolean existePedidos) {
        this.existePedidos = existePedidos;
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

    public boolean isActualizaPedido() {
        return actualizaPedido;
    }

    public void setActualizaPedido(boolean actualizaPedido) {
        this.actualizaPedido = actualizaPedido;
    }

    public boolean isActualizaHistorico() {
        return actualizaHistorico;
    }

    public void setActualizaHistorico(boolean actualizaHistorico) {
        this.actualizaHistorico = actualizaHistorico;
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

    public ArrayList<ListaEmpresasRecomendadas> getDatosRecomendadas() {
        return datosRecomendadas;
    }

    public void setDatosRecomendadas(ArrayList<ListaEmpresasRecomendadas> datosRecomendadas) {
        this.datosRecomendadas = datosRecomendadas;
    }

    public ArrayList<DatosEmpresa> getDatosEmpresa() {
        return datosEmpresa;
    }

    public void setDatosEmpresa(ArrayList<DatosEmpresa> datosEmpresa) {
        this.datosEmpresa = datosEmpresa;
    }

    public ArrayList<ListaHorarios> getDatosHorarios() {
        return datosHorarios;
    }

    public void setDatosHorarios(ArrayList<ListaHorarios> datosHorarios) {
        this.datosHorarios = datosHorarios;
    }

    public void addDatosHorarios(ArrayList<ListaHorarios> datosHorarios) {
        for(int i=0;i<datosHorarios.size();i++){
            this.datosHorarios.add(datosHorarios.get(i));
        }
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

    public ArrayList<ListaPedido> getDatosPedido() {
        return datosPedido;
    }

    public void setDatosPedido(ArrayList<ListaPedido> datosPedido) {
        this.datosPedido = datosPedido;
    }

    public ArrayList<ListaProductosPedido> getDatosProductoPedido() {
        return datosProductoPedido;
    }

    public void setDatosProductoPedido(ArrayList<ListaProductosPedido> datosProductoPedido) {
        this.datosProductoPedido = datosProductoPedido;
    }

    public ArrayList<ListaHistoricoPedidos> getDatosHistorico() {
        return datosHistorico;
    }

    public void setDatosHistorico(ArrayList<ListaHistoricoPedidos> datosHistorico) {
        this.datosHistorico = datosHistorico;
    }

    public ArrayList<ListaProductosHistorico> getDatosProductoHistorico() {
        return datosProductoHistorico;
    }

    public void setDatosProductoHistorico(ArrayList<ListaProductosHistorico> datosProductoHistorico) {
        this.datosProductoHistorico = datosProductoHistorico;
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
