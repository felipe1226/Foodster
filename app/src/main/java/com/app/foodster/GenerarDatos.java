package com.app.foodster;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.foodster.Empresa.DatosEmpresa;
import com.app.foodster.Empresa.ListaEmpresas;
import com.app.foodster.Empresa.ListaEmpresasRecomendadas;
import com.app.foodster.Persona.Documentos;
import com.app.foodster.Producto.ListaCartas;
import com.app.foodster.Producto.ListaProductos;
import com.app.foodster.Ubicacion.Localidad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GenerarDatos {

    private ArrayList<DatosEmpresa> datosEmpresa;

    ArrayList<String> categorias;

    private ArrayList<ListaEmpresasRecomendadas> listaEmpresasRecomendadas;

    private ArrayList<ListaEmpresas> listaEmpresas;

    private ArrayList<ListaCartas> listaCartas;

    private ArrayList<ListaProductos> listaProductos;

    GlobalState gs;
    String consulta;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    public GenerarDatos(GlobalState gs, RequestQueue request) {
        this.gs = gs;
        this.request = request;

        listarDocumentos();
        listarCiudades();
        listarEmpresasRecomendadas();
        listarEmpresas();
        listarCartas();
        listarProductos();
    }

    private void listarDocumentos(){
        String url = "http://" + gs.getIp() + "/Persona/listar_documentos.php";
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("documento");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaDocumentos(jsonObject, datos);
                    }
                }
                catch (JSONException e) {
                        e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                detectarError(error);
            }
        });
        request.add(jsonObjectRequest);
    }

    private void listarCiudades(){
        consulta = "ciudad";

        String url = "http://" + gs.getIp() + "/Ubicacion/listar_ciudades.php";

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("ciudad");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaCiudades(jsonObject, datos);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                detectarError(error);
            }
        });
        request.add(jsonObjectRequest);

        //jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void listarEmpresasRecomendadas(){

        consulta = "empresa_recomendada";
        String url = "http://" + gs.getIp() + "/Empresa/listar_empresas_recomendadas.php";

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("empresa_recomendada");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaRecomentadas(jsonObject, datos);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                detectarError(error);
            }
        });
        request.add(jsonObjectRequest);
    }

    private void listarEmpresas(){

        consulta = "empresa";
        String url = "http://" + gs.getIp() + "/Empresa/listar_empresas.php";

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("empresa");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaEmpresas(jsonObject, datos);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                detectarError(error);
            }
        });
        request.add(jsonObjectRequest);
    }

    private void listarCartas(){

        consulta = "carta";
        String url = "http://" + gs.getIp() + "/Empresa/listar_cartas.php";

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("carta");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaCartas(jsonObject, datos);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                detectarError(error);
            }
        });
        request.add(jsonObjectRequest);
    }

    private void listarProductos(){

        consulta = "producto";
        String url = "http://" + gs.getIp() + "/Empresa/listar_productos.php";

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("producto");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaProductos(jsonObject, datos);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                detectarError(error);
            }
        });
        request.add(jsonObjectRequest);
    }

    private void detectarError(VolleyError error){
        if (error instanceof AuthFailureError){
            Log.e("VOLLEY", "Se ha producido un fallo con las credenciales. " + error.getMessage() );
        } else if (error instanceof NetworkError) {
            Log.e("VOLLEY", "Se ha producido un fallo en la red. "+ error.getMessage());
        } else if (error instanceof NoConnectionError) {
            Log.e("VOLLEY", "Se ha producido un fallo en la conexión. "+ error.getMessage());
        } else if (error instanceof TimeoutError) {
            Log.e("VOLLEY", "Fallo en tiempo de espera. "+ error.getMessage());
        }
    }


    /*@Override
    public void onResponse(JSONObject response) {


        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;
        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {
                if(consulta.compareTo("documento") == 0){
                    consultaDocumentos(jsonObject, datos);
                }
                if(consulta.compareTo("ciudad") == 0){
                    consultaCiudades(jsonObject, datos);
                }
                if(consulta.compareTo("empresa") == 0){
                    consultaEmpresas(jsonObject, datos);
                }
                if(consulta.compareTo("empresa_recomendada") == 0){
                    consultaRecomentadas(jsonObject, datos);
                }
                if(consulta.compareTo("carta") == 0){
                    consultaCartas(jsonObject, datos);
                }
                if(consulta.compareTo("producto") == 0){
                    consultaProductos(jsonObject, datos);
                }
            }
            else{
                //Toast.makeText(null, "Error de consulta", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        accionConsulta();
    }*/

    private void accionConsulta() {
        switch (consulta) {

            case "documento" : listarCiudades(); break;

            case "ciudad" : listarEmpresas(); break;

            case "empresa":
                listarEmpresasRecomendadas();
                break;

            case "empresa_recomendada":
                listarCartas();
                break;

            case "carta":
                listarProductos();
                break;
        }
    }

    private void consultaDocumentos(JSONObject jsonObject, JSONArray datos) throws JSONException {
        ArrayList<Documentos> documento = new ArrayList<>();
        for (int i = 0; i < datos.length(); i++) {
            jsonObject = datos.getJSONObject(i);
            documento.add(new Documentos(jsonObject.optInt("id"),
                    jsonObject.optString("tipo"),
                    jsonObject.optString("abreviacion")));
        }
        gs.setDocumentos(documento);
    }


    private void consultaCiudades(JSONObject jsonObject, JSONArray datos) throws JSONException {
        ArrayList<Localidad> localidad = new ArrayList<>();
        for (int i = 0; i < datos.length(); i++) {
            jsonObject = datos.getJSONObject(i);
            localidad.add(new Localidad(jsonObject.optInt("idDepartamento"),
                    jsonObject.optString("departamento"),
                    jsonObject.optInt("id"),
                    jsonObject.optString("ciudad")));
        }
        gs.setLocalidades(localidad);
    }

    private void consultaEmpresas(JSONObject jsonObject, JSONArray datos) throws JSONException {
        boolean existeCategoria;

        datosEmpresa = new ArrayList<>();
        listaEmpresas = new ArrayList<>();
        categorias = new ArrayList<>();
        for (int i = 0; i < datos.length(); i++) {
            jsonObject = datos.getJSONObject(i);

            String categoria = jsonObject.optString("categoria");
            datosEmpresa.add(new DatosEmpresa(jsonObject.optInt("id"),
                    jsonObject.optString("tipo"),
                    categoria,
                    jsonObject.optString("logo"),
                    jsonObject.optString("nombre"),
                    jsonObject.optString("banner"),
                    jsonObject.optString("descripcion"),
                    jsonObject.optString("direccion"),
                    jsonObject.optString("ubicacion"),
                    jsonObject.optString("telefono"),
                    jsonObject.optString("movil"),
                    jsonObject.optString("email"),
                    jsonObject.optString("ciudad"),
                    jsonObject.optInt("domicilio"),
                    jsonObject.optInt("pagos_online")));

            listaEmpresas.add(new ListaEmpresas(jsonObject.optInt("id"),
                    jsonObject.optString("banner"),
                    jsonObject.optString("nombre"),
                    categoria));

            if(i == 0){
                categorias.add(categoria);
            }
            else{
                existeCategoria = false;
                for(int j=0;j<categorias.size();j++){
                    if(categorias.get(j).compareTo(categoria) == 0){
                        existeCategoria = true;
                    }
                }
                if(!existeCategoria){
                    categorias.add(categoria);
                }
            }
        }

        gs.setDatosEmpresa(datosEmpresa);
        gs.setCategorias(categorias);
        gs.setFiltroCategorias(categorias);
    }

    private void consultaRecomentadas(JSONObject jsonObject, JSONArray datos) throws JSONException {
        listaEmpresasRecomendadas = new ArrayList<>();
        for (int i = 0; i < datos.length(); i++) {
            jsonObject = datos.getJSONObject(i);


            listaEmpresasRecomendadas.add(new ListaEmpresasRecomendadas(jsonObject.optInt("id"),
                    jsonObject.optInt("idEmpresa"),
                    jsonObject.optString("banner"),
                    jsonObject.optString("nombre"),
                    jsonObject.optString("categoria"),
                    jsonObject.optString("fecha")));
        }

        gs.setDatosRecomendadas(listaEmpresasRecomendadas);
    }

    private void consultaCartas(JSONObject jsonObject, JSONArray datos) throws JSONException {
        listaCartas = new ArrayList<>();
        for (int i = 0; i < datos.length(); i++) {
            jsonObject = datos.getJSONObject(i);


            listaCartas.add(new ListaCartas(jsonObject.optInt("id"),
                    jsonObject.optInt("id_empresa"),
                    jsonObject.optString("nombre")));
        }


        gs.setDatosCartas(listaCartas);
    }

    private void consultaProductos(JSONObject jsonObject, JSONArray datos) throws JSONException {
        listaProductos = new ArrayList<>();
        int cont = 0;
        int idAnterior = 0;
        for (int i = 0; i < datos.length(); i++) {
            jsonObject = datos.getJSONObject(i);


            int idProducto = jsonObject.optInt("idProducto");
            String imagen = jsonObject.optString("imagen");
            if(idProducto != idAnterior){
                idAnterior = idProducto;
                cont = 0;

                int idEmpresa = jsonObject.optInt("idEmpresa");
                int idCarta = jsonObject.optInt("idCarta");

                String producto = jsonObject.optString("producto");
                String descripcion = jsonObject.optString("descProducto");
                int precio = jsonObject.optInt("precio");
                int promocion = jsonObject.optInt("id_promocion");

                if(promocion != 0){
                    String descPromocion = (jsonObject.optString("descripcion"));
                    int descuento = (jsonObject.optInt("descuento"));
                    promocion = (int)(precio - ( precio * ((double)descuento/100)));
                    String fecha = jsonObject.optString("fecha_inicio") + " - " + jsonObject.optString("fecha_fin");
                    listaProductos.add(new ListaProductos(idEmpresa, idCarta, idProducto, producto, descripcion, precio,
                            promocion, descPromocion, descuento, fecha));
                }
                else{
                    listaProductos.add(new ListaProductos(idEmpresa, idCarta, idProducto, producto, descripcion, precio, promocion));

                }
                if(imagen != null){
                    listaProductos.get(listaProductos.size()-1).setFoto1(imagen);
                }
            }
            else{
                if(imagen != null){
                    if(cont == 1){
                        listaProductos.get(listaProductos.size()-1).setFoto2(imagen);
                    }
                    else{
                        listaProductos.get(listaProductos.size()-1).setFoto3(imagen);
                    }
                }
            }
            cont++;
        }
        gs.setDatosProducto(listaProductos);
    }

    /*@Override
    public void onErrorResponse(VolleyError error) {
        Log.i("ERROR", error.toString());
    }*/
}
