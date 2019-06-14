package com.app.foodster.Empresa;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.app.foodster.Producto.AdaptadorListaProductos;
import com.app.foodster.Producto.ListaCartas;
import com.app.foodster.Producto.ListaProductos;
import com.app.foodster.R;

import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class Empresas extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefresh;

    GlobalState gs;

    private ArrayList<DatosEmpresa> datosEmpresa;
    ArrayList<Integer> empresasBusqueda;

    private AdaptadorListaProductos adaptadorProductos;
    private ArrayList<ListaProductos> productosRecomendados = null;

    private AdaptadorEmpresasRecomendadas adaptadorEmpresasRecomendadas;
    private ArrayList<ListaEmpresasRecomendadas> listaEmpresasRecomendadas;

    private AdaptadorEmpresas adaptadorEmpresas;
    private ArrayList<ListaEmpresas> listaEmpresas = null;

    private ArrayList<ListaCartas> listaCartas;
    private ArrayList<ListaProductos> listaProductos;

    ProgressBar progressBar;
    Toolbar toolbar;
    android.support.v7.widget.SearchView svBuscar;
    SearchView.SearchAutoComplete searchAutoComplete;
    ArrayList<String> categorias;

    ArrayList<String> filtroCategorias;
    AlertDialog dialogFiltroCategorias;
    View viewFiltroCategorias;

    AlertDialog dialogFiltroPrecios;
    View viewFiltroPrecios;

    private int rangoMinimo;
    private int rangoMaximo;

    private int precioMinimo;
    private int precioMaximo;

    LinearLayout layout_recomendadas;
    TextView tvProductos;
    RecyclerView rvProductos;
    TextView tvResultados;
    RecyclerView rvRecomendadas;
    RecyclerView rvEmpresas;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_empresas, container, false);

        toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        swipeRefresh = v.findViewById(R.id.refresh);
        swipeRefresh.setOnRefreshListener(this);

        progressBar = v.findViewById(R.id.progressBar);

        layout_recomendadas = v.findViewById(R.id.layout_recomendadas);
        tvResultados = v.findViewById(R.id.tvResultados);

        rvRecomendadas = v.findViewById(R.id.rvRecomendadas);
        rvEmpresas = v.findViewById(R.id.rvEmpresas);

        rvProductos = v.findViewById(R.id.rvProductos);

        return v;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);//Make su

        gs = (GlobalState) getActivity().getApplication();
        gs.setFragment(this);
        request = Volley.newRequestQueue(getActivity().getApplicationContext());
    }


    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_empresas, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.action_search);

        svBuscar = (android.support.v7.widget.SearchView) item.getActionView();
        svBuscar.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(query.compareTo("") != 0){
                    gs.setBusqueda(query);
                    busqueda();
                    InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(svBuscar.getWindowToken(), 0);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(query.compareTo("") == 0){
                    gs.setBusqueda(query);
                    generarEmpresas();
                }
                return true;
            }
        });

        svBuscar.setIconifiedByDefault(false);

        verificarProductos();

        verificarEmpresas();
    }

    private void inicializarRangoPrecios(){

        ArrayList<Integer> precios = new ArrayList<>();

        for (int i=0;i<listaProductos.size();i++){
            precios.add(listaProductos.get(i).getPrecio());
        }

        Collections.sort(precios);

        if(precios.size() > 0){
            rangoMinimo = precios.get(0);
            rangoMaximo = precios.get(precios.size()-1);

            precioMinimo = rangoMinimo;
            precioMaximo = rangoMaximo;
        }
    }

    private void obtenerSugerencias(){

        searchAutoComplete = (SearchView.SearchAutoComplete)svBuscar.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(Color.WHITE);
        searchAutoComplete.setDropDownBackgroundResource(android.R.color.background_light);
        ArrayList<String> listaBusqueda = new ArrayList<>();

        for(DatosEmpresa empresas: gs.getDatosEmpresa()){
            String nombre = empresas.getNombre().toLowerCase();
            listaBusqueda.add(Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1, nombre.length()).toLowerCase());
        }

        ArrayList<String> listaCarta = new ArrayList<>();
        for (ListaCartas cartas: gs.getDatosCartas()){
            String nombre = cartas.getCarta().toLowerCase();
            if(listaCarta.size() == 0){
                listaCarta.add(nombre);
            }
            else{
                boolean existe = false;
                for(String carta: listaCarta){
                    if(carta.compareToIgnoreCase(nombre) == 0){
                        existe = true;
                        break;
                    }
                }
                if(!existe){
                    listaCarta.add(Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1, nombre.length()).toLowerCase());
                }
            }
        }
        listaBusqueda.addAll(listaCarta);

        ArrayList<String> listaProducto = new ArrayList<>();
        for (ListaProductos productos: gs.getDatosProducto()){
            String nombre = productos.getNombre().toLowerCase();
            if(listaProducto.size() == 0){
                listaProducto.add(nombre);
            }
            else{
                boolean existe = false;
                for(String producto: listaProducto){

                        if(producto.compareToIgnoreCase(nombre) == 0){
                            existe = true;
                            break;
                        }
                }
                for(String lista: listaBusqueda){
                    if(lista.compareToIgnoreCase(nombre) == 0){
                        existe = true;
                        break;
                    }
                }
                if(!existe){
                    listaProducto.add(Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1, nombre.length()).toLowerCase());
                }
            }
        }
        listaBusqueda.addAll(listaProducto);

        Collections.sort(listaBusqueda);

        ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, listaBusqueda);
        searchAutoComplete.setAdapter(newsAdapter);

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
                svBuscar.setQuery(queryString, true);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_precios:
                dialogFiltroPrecios();
                break;
            case R.id.action_categorias:
                dialogFiltroCategorias();
                break;
            default: break;
        }
        return false;
    }

    private void generarBusqueda() {

        datosEmpresa = gs.getDatosEmpresa();
        listaEmpresas = new ArrayList<>();

        for(int i=0;i<datosEmpresa.size();i++){
            for (int j = 0; j < empresasBusqueda.size(); j++) {
                if (datosEmpresa.get(i).id == empresasBusqueda.get(j)) {
                    listaEmpresas.add(new ListaEmpresas(datosEmpresa.get(i).getId(),
                            datosEmpresa.get(i).getBanner(),
                            datosEmpresa.get(i).getNombre(),
                            datosEmpresa.get(i).getCategoria()));
                }
            }
        }
        datosEmpresas();
    }

    private void generarEmpresas() {
        datosEmpresa = new ArrayList<>();
        listaEmpresas = new ArrayList<>();

        datosEmpresa = gs.getDatosEmpresa();

        for(int i=0;i<datosEmpresa.size();i++){
            for (int j = 0; j < gs.filtroCategorias.size(); j++) {
                if (datosEmpresa.get(i).getCategoria().compareTo(gs.filtroCategorias.get(j)) == 0) {
                    listaEmpresas.add(new ListaEmpresas(datosEmpresa.get(i).getId(),
                            datosEmpresa.get(i).getBanner(),
                            datosEmpresa.get(i).getNombre(),
                            datosEmpresa.get(i).getCategoria()));
                }
            }
        }

        inicializarRangoPrecios();

        filtrarPrecios();
    }

    private void generarRecomendadas() {
        listaEmpresasRecomendadas = new ArrayList<>();
        listaEmpresasRecomendadas = gs.getDatosRecomendadas();

        datosRecomendadas();
    }

    private void datosRecomendadas(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        adaptadorEmpresasRecomendadas = new AdaptadorEmpresasRecomendadas(this, getContext(), listaEmpresasRecomendadas);
        rvRecomendadas.setLayoutManager(layoutManager);
        rvRecomendadas.setAdapter(adaptadorEmpresasRecomendadas);

        progressBar.setVisibility(View.GONE);
        rvRecomendadas.setVisibility(View.VISIBLE);
    }

    private void verificarProductos(){

        listaProductos = gs.getDatosProducto();
        productosRecomendados = new ArrayList<>();

        if(listaProductos != null){
            for(int i=0;i<6;i++){
                productosRecomendados.add(listaProductos.get(i));
            }

            if(productosRecomendados.size() > 0){
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

                adaptadorProductos = new AdaptadorListaProductos(getContext(), productosRecomendados);
                rvProductos.setLayoutManager(layoutManager);
                rvProductos.setAdapter(adaptadorProductos);
            }
            else{
                tvProductos.setVisibility(View.GONE);
                rvProductos.setVisibility(View.GONE);
            }
        }
    }

    private void verificarEmpresas(){
        if(gs.isActualizaEmpresas()){
            gs.setActualizaEmpresas(false);
            progressBar.setVisibility(View.VISIBLE);
            rvEmpresas.setVisibility(View.GONE);

            listarEmpresas();
            listarEmpresasRecomendadas();
            listarCartas();
            listarProductos();
        }
        else{
            generarEmpresas();

            if(gs.getDatosRecomendadas().size() > 0){
                listaEmpresasRecomendadas = gs.getDatosRecomendadas();

                generarRecomendadas();
            }
            else{
                listarEmpresasRecomendadas();
            }
        }

        String busqueda = gs.getBusqueda().toString();
        if(busqueda.compareTo("") != 0){
            svBuscar.setQuery(busqueda, true);
        }

        inicializarRangoPrecios();

        obtenerSugerencias();
    }

    private void datosEmpresas(){

        adaptadorEmpresas = new AdaptadorEmpresas(this, getContext(), listaEmpresas);
        rvEmpresas.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvEmpresas.setAdapter(adaptadorEmpresas);

        tvResultados.setText("Establecimientos: "+listaEmpresas.size());

        progressBar.setVisibility(View.GONE);
        rvEmpresas.setVisibility(View.VISIBLE);
    }

    private void dialogFiltroCategorias(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        viewFiltroCategorias = getLayoutInflater().inflate(R.layout.dialog_filtro_categorias, null);

        RecyclerView rvCategorias = viewFiltroCategorias.findViewById(R.id.rvCategorias);

        filtroCategorias = new ArrayList<>();

        AdaptadorCategorias adaptadorCategorias = new AdaptadorCategorias(getContext(), gs.getCategorias(), filtroCategorias, gs);
        rvCategorias.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvCategorias.setAdapter(adaptadorCategorias);

        Button btnAplicar = viewFiltroCategorias.findViewById(R.id.btnAplicar);
        Button btnCancelar = viewFiltroCategorias.findViewById(R.id.btnCancelar);

        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gs.setFiltroCategorias(filtroCategorias);
                generarEmpresas();
                dialogFiltroCategorias.hide();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFiltroCategorias.hide();
            }
        });

        builder.setView(viewFiltroCategorias);
        builder.setCancelable(false);
        dialogFiltroCategorias = builder.create();
        dialogFiltroCategorias.show();
    }

    private void dialogFiltroPrecios(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        viewFiltroPrecios = getLayoutInflater().inflate(R.layout.dialog_rango_precios, null);

        TextView tvRango = viewFiltroPrecios.findViewById(R.id.tvRango);
        tvRango.setText(rangoMinimo+ " - "+ rangoMaximo);

        final EditText etMinimo = viewFiltroPrecios.findViewById(R.id.etMinimo);
        final EditText etMaximo = viewFiltroPrecios.findViewById(R.id.etMaximo);

        RangeSeekBar sbPrecios = viewFiltroPrecios.findViewById(R.id.sbPrecios);
        sbPrecios.setRangeValues(rangoMinimo, rangoMaximo);
        sbPrecios.setSelectedMinValue(rangoMinimo);
        sbPrecios.setSelectedMaxValue(rangoMaximo);

        etMinimo.setText(String.valueOf(precioMinimo));
        etMaximo.setText(String.valueOf(precioMaximo));

        Button btnAplicar = viewFiltroPrecios.findViewById(R.id.btnAplicar);
        Button btnCancelar = viewFiltroPrecios.findViewById(R.id.btnCancelar);


        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etMinimo.getText().toString() != ""){
                    precioMinimo = Integer.parseInt(etMinimo.getText().toString());
                }
                if(etMaximo.getText().toString() != ""){
                    precioMaximo = Integer.parseInt(etMaximo.getText().toString());
                }
                generarEmpresas();
                dialogFiltroPrecios.hide();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFiltroPrecios.hide();
            }
        });

        builder.setView(viewFiltroPrecios);
        builder.setCancelable(false);
        dialogFiltroPrecios = builder.create();
        dialogFiltroPrecios.show();
    }

    private void filtrarPrecios(){
        ArrayList<ListaProductos> listaProductos = gs.getDatosProducto();
        boolean empresaFiltrada;

        for(int e=0;e < listaEmpresas.size();e++){
            empresaFiltrada = false;
            for(int i=0;i<listaProductos.size();i++){
                if(listaEmpresas.get(e).getId() == listaProductos.get(i).getIdEmpresa()){
                    if(listaProductos.get(i).getPromocion() != 0){
                        if(listaProductos.get(i).getPromocion() >= precioMinimo && listaProductos.get(i).getPromocion() <= precioMaximo){
                            empresaFiltrada = true;
                        }
                    }
                    else{
                        if(listaProductos.get(i).getPrecio() >= precioMinimo && listaProductos.get(i).getPrecio() <= precioMaximo){
                            empresaFiltrada = true;
                        }
                    }
                }
            }
            if(!empresaFiltrada){
                listaEmpresas.remove(e);
            }
        }

        datosEmpresas();
    }

    private void listarEmpresasRecomendadas(){

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
                        consultaRecomendadas(jsonObject, datos);
                    }
                    else{
                        layout_recomendadas.setVisibility(View.GONE);
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
                        consultaEmpresa(jsonObject, datos);
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

    private void busqueda(){

        String url = "http://" + gs.getIp() + "/Empresa/listar_empresas_busqueda.php?cadena="+svBuscar.getQuery().toString();
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("busqueda");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaBusqueda(jsonObject, datos);
                    }
                    else{
                        Toast.makeText(getContext(), "No hay resultados de busqueda", Toast.LENGTH_SHORT).show();
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

    public void generarVisita(int idEmpresa){
        String url = "http://" + gs.getIp() + "/Empresa/generar_visita.php?idEmpresa="
                + idEmpresa + "&idPersona="+gs.getIdPersona()+"&busqueda="+svBuscar.getQuery().toString();
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("visita");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
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

    /*public void onResponse(JSONObject response) {

        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;
        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {
                if(consulta.compareTo("empresa") == 0){
                    consultaEmpresa(jsonObject, datos);
                }
                if(consulta.compareTo("empresa_recomendada") == 0){
                    consultaRecomendadas(jsonObject, datos);
                }
                if(consulta.compareTo("carta") == 0){
                    consultaCartas(jsonObject, datos);
                }
                if(consulta.compareTo("producto") == 0){
                    consultaProductos(jsonObject, datos);
                }
                if(consulta.compareTo("busqueda") == 0){
                    consultaBusqueda(jsonObject, datos);
                }
            }
            else {
                if(consulta.compareTo("empresa_recomendada") == 0){
                    layout_recomendadas.setVisibility(View.GONE);
                }
                if(consulta.compareTo("busqueda") == 0){
                    Toast.makeText(getContext(), "No hay resultados de busqueda", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        accionConsulta();
    }*/

    /*private void accionConsulta(){
        switch (consulta){
            case "empresa" : listarEmpresasRecomendadas(); break;

            case "empresa_recomendada": listarCartas(); break;

            case "carta" : listarProductos(); break;
        }
    }*/

    private void consultaEmpresa(JSONObject jsonObject, JSONArray datos ) throws JSONException {

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

        datosEmpresas();

        gs.setDatosEmpresa(datosEmpresa);
        gs.setCategorias(categorias);
        gs.setFiltroCategorias(categorias);
    }

    private void consultaRecomendadas(JSONObject jsonObject, JSONArray datos) throws JSONException {
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

        datosRecomendadas();

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

    private void consultaBusqueda(JSONObject jsonObject, JSONArray datos) throws JSONException {
        empresasBusqueda = new ArrayList<>();
        for (int i = 0; i < datos.length(); i++) {
            jsonObject = datos.getJSONObject(i);


            empresasBusqueda.add(jsonObject.optInt("id"));
        }

        generarBusqueda();
    }

    /*@Override
    public void onErrorResponse(VolleyError error) {
        Log.i("ERROR", error.toString());
    }*/

    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Collections.shuffle(listaEmpresas);
                if(listaEmpresasRecomendadas != null){
                    Collections.shuffle(listaEmpresasRecomendadas);
                }
                datosEmpresas();
                datosRecomendadas();
                swipeRefresh.setRefreshing(false);
            }
        }, 1000);
    }
}
