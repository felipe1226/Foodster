package com.app.foodster.Ubicacion;

import android.widget.Spinner;

import java.util.ArrayList;

public class DatosLocalidad {

    ArrayList<String> departamentos = new ArrayList<>();
    ArrayList<String> ciudades = new ArrayList<>();

    ArrayList<Localidad> localidad;

    public DatosLocalidad(ArrayList<Localidad> localidad) {
        this.localidad = localidad;

        listaDepartamentos();
    }

    public void listaDepartamentos(){
        int id = 0;

        for (int i = 0; i < localidad.size(); i++) {
            if(id != localidad.get(i).getIdDepartamento()){
                id = localidad.get(i).getIdDepartamento();
                departamentos.add(localidad.get(i).getDepartamento());
            }
        }
    }


    public void listaCiudades(String depto){
        for (int i =0;i<localidad.size();i++){
            if(localidad.get(i).getDepartamento().compareTo(depto) == 0){
                ciudades.add(localidad.get(i).getCiudad());
            }
        }
    }

    public int obtenerPosicionItem(Spinner spinner, String item){

        int posicion = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)) {
                posicion = i;
            }
        }
        return posicion;
    }

    public ArrayList<String> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(ArrayList<String> departamentos) {
        this.departamentos = departamentos;
    }

    public ArrayList<String> getCiudades() {
        return ciudades;
    }

    public void setCiudades(ArrayList<String> ciudades) {
        this.ciudades = ciudades;
    }
}
