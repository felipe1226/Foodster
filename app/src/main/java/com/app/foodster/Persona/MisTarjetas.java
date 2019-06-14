package com.app.foodster.Persona;


import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.foodster.GlobalState;
import com.app.foodster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MisTarjetas extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {

    GlobalState gs;

    String consulta;

    private AdaptadorTarjetas adaptadorTarjetas;
    private ArrayList<ListaTarjetas> listaTarjetas;

    ProgressDialog progressEliminar;
    ProgressDialog carga;

    Button btnNueva;
    RecyclerView rvTarjetas;

    AlertDialog dialogTarjeta;
    EditText etNombre;
    EditText etNumero;
    EditText etMes;
    EditText etYear;

    int posicion;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gs = (GlobalState)getActivity().getApplicationContext();

        request = Volley.newRequestQueue(getActivity().getApplicationContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mis_tarjetas, container, false);

        carga = new ProgressDialog(getContext());
        carga.setMessage("Registrando datos...");
        carga.setCanceledOnTouchOutside(false);

        progressEliminar = new ProgressDialog(getContext());
        progressEliminar.setMessage("Eliminando...");
        progressEliminar.setCanceledOnTouchOutside(false);

        btnNueva = v.findViewById(R.id.btnNueva);
        btnNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTarjeta();
            }
        });
        rvTarjetas = v.findViewById(R.id.rvTarjetas);

        verificarTarjetas();

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void verificarTarjetas(){
            if(gs.getDatosTarjeta() != null && gs.getDatosTarjeta().size() > 0){
                listaTarjetas = gs.getDatosTarjeta();
                generarTarjetas();
            }
            else{
                listarTarjetas();
            }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generarTarjetas(){

        adaptadorTarjetas = new AdaptadorTarjetas(this, getContext(), listaTarjetas);
        rvTarjetas.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvTarjetas.setAdapter(adaptadorTarjetas);
    }

    private void dialogTarjeta() {
        AlertDialog.Builder buider = new AlertDialog.Builder(getContext());
        buider.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_nueva_tarjeta, null);

        etNombre = view.findViewById(R.id.etNombre);
        etNumero = view.findViewById(R.id.etNumero);
        etMes = view.findViewById(R.id.etMes);
        etYear = view.findViewById(R.id.etYear);

        final Button btnCancelar = view.findViewById(R.id.btnCancelar);
        final Button btnConfirmar = view.findViewById(R.id.btnAplicar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTarjeta.cancel();
            }
        });


        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarCampos();
            }
        });


        buider.setView(view);
        dialogTarjeta = buider.create();

        dialogTarjeta.show();
    }

    private void validarCampos(){
        String nombre = etNombre.getText().toString();
        String numero = etNumero.getText().toString();
        String mes = etMes.getText().toString();
        String year = etYear.getText().toString();

        if(validarCadena(nombre) && validarCadena(numero) && validarCadena(mes) && validarCadena(year)){
            String fecha = mes +"/"+year;
            registrarTarjeta(nombre,"MasterCard", numero, fecha);
        }
        else{
            Toast.makeText(getContext(), "Complete los campos, por favor", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarCadena(String cadena){
        if(cadena.compareToIgnoreCase("") != 0){
            return true;
        }
        else{
            return false;
        }
    }

    public void listarTarjetas(){
        consulta = "tarjeta";
        String url = "http://" + gs.getIp() + "/Persona/listar_tarjetas.php?idPersona="+gs.getIdPersona();

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void registrarTarjeta(final String nombre, final String tipo, final String numero, final String fecha){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + gs.getIp() + "/Persona/registrar_tarjeta.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.compareTo("") != 0) {
                            Toast.makeText(getContext(), "Se ha registrado satisfactoriamente", Toast.LENGTH_SHORT).show();
                            carga.cancel();


                        }
                        else{
                            Toast.makeText(getContext(), "Error al registrar, intente de nuevo", Toast.LENGTH_SHORT).show();
                            carga.cancel();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();

                params.put("idUsuario", String.valueOf(gs.getIdUsuario()));
                params.put("nombre", nombre);
                params.put("tipo", tipo);
                params.put("numero", numero);
                params.put("fecha", fecha);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void actualizarDetalles(int idFavorito, String detalles){
        consulta = "actualizar_detalles";
        String url = "http://" + gs.getIp() + "/Persona/actualizar_detalles_favorito.php?idFavorito="+idFavorito+"&detalles="+detalles;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    public void borrarTarjeta(int id, int posicion){

        this.posicion = posicion;
        consulta = "borrar";
        String url = "http://" + gs.getIp() + "/Persona/borrar_tarjeta.php?idTarjeta="+id;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onResponse(JSONObject response) {

        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;
        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {
                switch (consulta){
                    case "tarjeta" : consultaTarjera(jsonObject, datos); break;

                    case "actualizar" : consultaActualizar(jsonObject, datos); break;

                    case "borrar" : consultaBorrar(); break;
                }
            }
            else{
                if(consulta.compareTo("borrar") == 0){
                    Toast.makeText(getContext(), "No se pudo borrar la tarjeta", Toast.LENGTH_SHORT).show();
                }
                if(consulta.compareTo("actualizar_detalles") == 0){
                    Toast.makeText(getContext(), "No se pudo actualizar la tarjeta", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void consultaTarjera(JSONObject jsonObject, JSONArray datos) throws JSONException {
        listaTarjetas = new ArrayList<>();
        for (int i = 0; i < datos.length(); i++) {
            jsonObject = datos.getJSONObject(i);

            listaTarjetas.add(new ListaTarjetas(jsonObject.optInt("id"),
                    jsonObject.optString("tipo"),
                    jsonObject.optString("numero"),
                    jsonObject.optString("fecha_vencimiento"),
                    jsonObject.optString("nombre")));
        }
        generarTarjetas();

        gs.setDatosTarjeta(listaTarjetas);
        rvTarjetas.setVisibility(View.VISIBLE);
    }

    private void consultaActualizar(JSONObject jsonObject, JSONArray datos){

    }

    private void consultaBorrar(){
        progressEliminar.cancel();
        listaTarjetas.remove(posicion);
        gs.setDatosTarjeta(listaTarjetas);

        if(listaTarjetas.size() > 0){
            adaptadorTarjetas.actualizar(listaTarjetas);
        }
        else{
            rvTarjetas.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onErrorResponse(VolleyError error) {
        progressEliminar.cancel();
        Toast.makeText(getContext(), "Error "+ error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("ERROR", error.toString());
    }
}
