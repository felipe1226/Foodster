package com.app.foodster.Empresa;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.foodster.GlobalState;
import com.app.foodster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Empresas extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
                                                    Response.Listener<JSONObject>, Response.ErrorListener{

    private SwipeRefreshLayout swipeRefresh;

    GlobalState gs;

    private ArrayList<DatosEmpresa> datosEmpresa;

    private AdaptadorListaEmpresas adaptadorEmpresas;
    private ArrayList<ListaEmpresas> listaEmpresas;

    SearchView svBuscar;
    ArrayList<String> categorias;

    RecyclerView rvEmpresas;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_empresas, container, false);

        swipeRefresh = v.findViewById(R.id.refresh);
        swipeRefresh.setOnRefreshListener(this);

        svBuscar = v.findViewById(R.id.svBuscar);
        svBuscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String texto) {
                if(texto != ""){
                    texto = texto.toLowerCase();
                    ArrayList<ListaEmpresas> listaFiltrada = new ArrayList<>();

                    for(ListaEmpresas lista: listaEmpresas){
                        String nombre = lista.getNombre().toLowerCase();
                        if(nombre.contains(texto)){
                            listaFiltrada.add(lista);
                        }
                    }
                    adaptadorEmpresas.setFilter(listaFiltrada);
                    return true;
                }
                else{
                    return false;
                }
            }
        });

        rvEmpresas = v.findViewById(R.id.rvEmpresas);

        if(gs.isActualizaEmpresas()){
            gs.setActualizaEmpresas(false);
            listarEmpresas();
        }
        else{
            generarEmpresas();
        }

        return v;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gs = (GlobalState) getActivity().getApplication();

        request = Volley.newRequestQueue(getActivity().getApplicationContext());



    }


    private void generarEmpresas() {
        datosEmpresa = new ArrayList<>();
        listaEmpresas = new ArrayList<>();

        datosEmpresa = gs.getDatosEmpresa();


        for(int i=0;i<datosEmpresa.size();i++){
            listaEmpresas.add(new ListaEmpresas(datosEmpresa.get(i).getId(),
                    datosEmpresa.get(i).getNombre()));
        }

        adaptadorEmpresas = new AdaptadorListaEmpresas(getContext(), listaEmpresas);
        rvEmpresas.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvEmpresas.setAdapter(adaptadorEmpresas);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_principal, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.action_search);

        SearchView svBuscar = (SearchView) item.getActionView();
        /*svBuscar.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String texto) {
                if(texto != ""){
                    texto = texto.toLowerCase();
                    ArrayList<ListaEmpresas> listaFiltrada = new ArrayList<>();

                    for(ListaEmpresas lista: listaEmpresas){
                        String nombre = lista.getNombre().toLowerCase();
                        if(nombre.contains(texto)){
                            listaFiltrada.add(lista);
                        }
                    }
                    adaptadorEmpresas.setFilter(listaFiltrada);
                    return true;
                }
                else{
                    return false;
                }
            }
        });
        svBuscar.setIconifiedByDefault(false);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_ordenar:
                Toast.makeText(getContext(),"Ordenar", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_ajustes:
                Toast.makeText(getContext(),"Ajustes", Toast.LENGTH_SHORT).show();
                break;
                default:break;
        }
        return false;

    }

    private void listarEmpresas(){

        String url = "http://" + gs.getIp() + "/Empresa/listar_empresas.php";

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    public void onResponse(JSONObject response) {

        datosEmpresa = new ArrayList<>();
        listaEmpresas = new ArrayList<>();
        categorias = new ArrayList<>();
        boolean existeCategoria;

        JSONArray datos = response.optJSONArray("empresa");
        JSONObject jsonObject = null;
        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {
                for (int i = 0; i < datos.length(); i++) {
                    jsonObject = datos.getJSONObject(i);

                    String categoria = jsonObject.optString("categoria");
                    datosEmpresa.add(new DatosEmpresa(jsonObject.optInt("id"),
                                                        jsonObject.optString("tipo"),
                                                        categoria,
                                                        jsonObject.optString("logo"),
                                                        jsonObject.optString("nombre"),
                                                        jsonObject.optInt("sucursal"),
                                                        jsonObject.optString("nombre_sucursal"),
                                                        jsonObject.optString("banner"),
                                                        jsonObject.optString("descripcion"),
                                                        jsonObject.optInt("metodo_pago"),
                                                        jsonObject.optString("direccion"),
                                                        jsonObject.optString("ubicacion"),
                                                        jsonObject.optString("telefono"),
                                                        jsonObject.optString("movil"),
                                                        jsonObject.optString("ciudad")));

                    listaEmpresas.add(new ListaEmpresas(jsonObject.optInt("id"),
                                                        jsonObject.optString("nombre")));

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

                adaptadorEmpresas = new AdaptadorListaEmpresas(getContext(), listaEmpresas);
                rvEmpresas.setLayoutManager(new GridLayoutManager(getContext(), 1));
                rvEmpresas.setAdapter(adaptadorEmpresas);

                rvEmpresas.setScrollingTouchSlop(2);

                gs.setDatosEmpresa(datosEmpresa);
                gs.setCategorias(categorias);
                gs.setFiltroCategorias(categorias);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error "+ error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("ERROR", error.toString());
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {listarEmpresas();
                listarEmpresas();
                swipeRefresh.setRefreshing(false);
            }
        }, 2000);
    }
}
