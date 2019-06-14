package com.app.foodster.Empresa;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.foodster.GlobalState;
import com.app.foodster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Eventos extends Fragment{

    GlobalState gs;

    boolean existeEventos;
    int consultas;

    private AdaptadorEventos adaptadorEventos;
    private ArrayList<ListaEventos> listaEventos = null;

    ProgressBar progressBar;
    TextView tvMensaje;

    RecyclerView rvEventos;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gs = (GlobalState) getActivity().getApplication();
        gs.setFragment(this);
        request = Volley.newRequestQueue(getActivity().getApplicationContext());

        existeEventos = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_eventos, container, false);

        tvMensaje = v.findViewById(R.id.tvMensaje);
        progressBar = v.findViewById(R.id.progressBar);
        rvEventos = v.findViewById(R.id.rvEventos);

        existeEventos = false;

        listaEventos = new ArrayList<>();

        consultas = 0;
        listarEventos();
        listarPromociones();

        return v;
    }

    private void generarEventos(){

        adaptadorEventos = new AdaptadorEventos(getContext(), listaEventos);
        rvEventos.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvEventos.setAdapter(adaptadorEventos);

        progressBar.setVisibility(View.GONE);
        tvMensaje.setVisibility(View.GONE);
        rvEventos.setVisibility(View.VISIBLE);
    }

    private void listarEventos(){

        String url = "http://" + gs.getIp() + "/Empresa/listar_eventos.php";
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("evento");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaEventos(jsonObject, datos);
                    }
                    consultas++;
                    verificarEventos();
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

    private void listarPromociones(){

        String url = "http://" + gs.getIp() + "/Empresa/listar_promociones.php";
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("promocion");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaEventos(jsonObject, datos);
                    }
                    consultas++;
                    verificarEventos();

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

    /*public void onResponse(JSONObject response) {
        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;
        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {


                if(consulta.compareTo("evento") == 0) {
                    listaEventos = new ArrayList<>();
                    existeEventos = true;
                }
                if(consulta.compareTo("promocion") == 0 && !existeEventos) {
                    listaEventos = new ArrayList<>();
                    existeEventos = true;
                }

                for (int i = 0; i < datos.length(); i++) {

                    jsonObject = datos.getJSONObject(i);

                    listaEventos.add(new ListaEventos(jsonObject.optInt("id"),
                            jsonObject.optString("imagen"),
                            jsonObject.optString("empresa"),
                            jsonObject.optString("nombre"),
                            jsonObject.optString("fecha")));
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        accionConsulta();
    }*/

    private void consultaEventos(JSONObject jsonObject, JSONArray datos) throws JSONException {
        existeEventos = true;

        for(int i=0; i<datos.length();i++) {
            jsonObject = datos.getJSONObject(i);

            listaEventos.add(new ListaEventos(jsonObject.optInt("id"),
                    jsonObject.optString("imagen"),
                    jsonObject.optString("empresa"),
                    jsonObject.optString("nombre"),
                    jsonObject.optString("fecha")));
        }
    }

    private void verificarEventos(){
        if(consultas == 2) {
            if (existeEventos) {
                generarEventos();
            } else {
                progressBar.setVisibility(View.GONE);
                tvMensaje.setVisibility(View.VISIBLE);
                rvEventos.setVisibility(View.GONE);
            }
        }
    }

    /*private void accionConsulta(){

        switch (consulta){
            case "evento": listarPromociones(); break;

            case "promocion": if(existeEventos){
                                    generarEventos();
                                }
                                else{
                                    progressBar.setVisibility(View.GONE);
                                    tvMensaje.setVisibility(View.VISIBLE);
                                    rvEventos.setVisibility(View.GONE);
            }
        }
    }*/

    /*@Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error "+ error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("ERROR", error.toString());
    }*/

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
}
