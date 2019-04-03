package com.app.foodster.Empresa;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

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
import java.util.Collections;

public class Empresas extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
                                                    Response.Listener<JSONObject>, Response.ErrorListener{

    private SwipeRefreshLayout swipeRefresh;

    GlobalState gs;

    String consulta;

    private ArrayList<DatosEmpresa> datosEmpresa;

    private AdaptadorEmpresasRecomendadas adaptadorEmpresasRecomendadas;
    private ArrayList<ListaEmpresasRecomendadas> listaEmpresasRecomendadas;

    private AdaptadorEmpresas adaptadorEmpresas;
    private ArrayList<ListaEmpresas> listaEmpresas = null;

    ProgressBar progressBar;
    Toolbar toolbar;
    android.support.v7.widget.SearchView svBuscar;
    ArrayList<String> categorias;

    ArrayList<String> filtroCategorias;
    AlertDialog dialogFiltro;
    View viewFiltro;

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

       //CollapsingToolbarLayout collapser = v.findViewById(R.id.collapser);

        swipeRefresh = v.findViewById(R.id.refresh);
        swipeRefresh.setOnRefreshListener(this);

        progressBar = v.findViewById(R.id.progressBar);

        tvResultados = v.findViewById(R.id.tvResultados);

        rvRecomendadas = v.findViewById(R.id.rvRecomendadas);
        rvEmpresas = v.findViewById(R.id.rvEmpresas);

        return v;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);//Make su

        gs = (GlobalState) getActivity().getApplication();
        gs.setFragment(this);
        request = Volley.newRequestQueue(getActivity().getApplicationContext());
    }

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
                return buscar(query);
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return buscar(query);
            }
        });

        svBuscar.setIconifiedByDefault(false);

        svBuscar.setQuery(gs.getBusqueda(), false);

        verificarEmpresas();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_filtrar:
                dialogFiltroCategorias();
                break;

            case R.id.action_buscar:
                Toast.makeText(getContext(),"Buscar", Toast.LENGTH_SHORT).show();
                break;
            default:break;
        }
        return false;

    }

    private boolean buscar(String query){
        if(query != "" && listaEmpresas != null){
            query = query.toLowerCase();
            ArrayList<ListaEmpresas> listaFiltrada = new ArrayList<>();

            for(ListaEmpresas lista: listaEmpresas){
                String nombre = lista.getNombre().toLowerCase();
                if(nombre.contains(query)){
                    listaFiltrada.add(lista);
                }
            }
            adaptadorEmpresas.setFilter(listaFiltrada);
            gs.setBusqueda(query);
            return true;
        }
        else{
            return false;
        }
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

    private void verificarEmpresas(){
        if(gs.isActualizaEmpresas()){
            gs.setActualizaEmpresas(false);
            progressBar.setVisibility(View.VISIBLE);
            rvEmpresas.setVisibility(View.GONE);
            listarEmpresas();
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

        datosEmpresas();

    }

    private void datosEmpresas(){

        adaptadorEmpresas = new AdaptadorEmpresas(this, getContext(), listaEmpresas);
        rvEmpresas.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvEmpresas.setAdapter(adaptadorEmpresas);

        svBuscar.setQuery(gs.getBusqueda(), true);

        tvResultados.setText("Establecimientos: "+listaEmpresas.size());

        progressBar.setVisibility(View.GONE);
        rvEmpresas.setVisibility(View.VISIBLE);
    }

    private void dialogFiltroCategorias(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        viewFiltro = getLayoutInflater().inflate(R.layout.dialog_filtro_mapa, null);

        RecyclerView rvCategorias = viewFiltro.findViewById(R.id.rvCategorias);

        filtroCategorias = new ArrayList<>();

        Empresas.AdaptadorCategorias adaptadorCategorias = new Empresas.AdaptadorCategorias(getContext(), gs.getCategorias());
        rvCategorias.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvCategorias.setAdapter(adaptadorCategorias);

        Button btnAplicar = viewFiltro.findViewById(R.id.btnAplicar);
        Button btnCancelar = viewFiltro.findViewById(R.id.btnCancelar);

        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gs.setFiltroCategorias(filtroCategorias);
                generarEmpresas();
                dialogFiltro.hide();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFiltro.hide();
            }
        });

        builder.setView(viewFiltro);
        builder.setCancelable(false);
        dialogFiltro = builder.create();
        dialogFiltro.show();
    }

    private void listarEmpresasRecomendadas(){

        consulta = "empresa_recomendada";
        String url = "http://" + gs.getIp() + "/Empresa/listar_empresas_recomendadas.php";

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void listarEmpresas(){

        consulta = "empresa";
        String url = "http://" + gs.getIp() + "/Empresa/listar_empresas.php";

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    public void generarVisita(int idEmpresa){
        consulta = "visita";
        String url = "http://" + gs.getIp() + "/Empresa/generar_visita.php?idEmpresa="
                + idEmpresa + "&idPersona="+gs.getIdPersona()+"&busqueda="+svBuscar.getQuery().toString();

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    public void onResponse(JSONObject response) {
        boolean existeCategoria;

        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;
        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {
                if(consulta.compareTo("empresa") == 0){
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
                                jsonObject.optInt("sucursal"),
                                jsonObject.optString("nombre_sucursal"),
                                jsonObject.optString("banner"),
                                jsonObject.optString("descripcion"),
                                jsonObject.optString("direccion"),
                                jsonObject.optString("ubicacion"),
                                jsonObject.optString("telefono"),
                                jsonObject.optString("movil"),
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
                if(consulta.compareTo("empresa_recomendada") == 0){
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
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        if(consulta.compareTo("empresa") == 0){
            listarEmpresasRecomendadas();
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
            public void run() {
                Collections.shuffle(listaEmpresas);
                Collections.shuffle(listaEmpresasRecomendadas);
                datosEmpresas();
                datosRecomendadas();
                //listarEmpresas();
                swipeRefresh.setRefreshing(false);
            }
        }, 1000);
    }

    private class AdaptadorCategorias extends RecyclerView.Adapter<Empresas.AdaptadorCategorias.MyViewHolder> {
        Context context;
        ArrayList<String> categorias;

        public AdaptadorCategorias(Context context, ArrayList<String> categorias) {
            this.context = context;
            this.categorias = categorias;
        }

        @NonNull
        @Override
        public Empresas.AdaptadorCategorias.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v;
            v = LayoutInflater.from(context).inflate(R.layout.item_filtro_categoria,viewGroup,false);
            final Empresas.AdaptadorCategorias.MyViewHolder holder = new Empresas.AdaptadorCategorias.MyViewHolder(v);

            holder.cbCategoria.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    String categoria = categorias.get(holder.getAdapterPosition());

                    if(filtroCategorias.size() == 0){
                        filtroCategorias.add(categoria);
                    }
                    else{
                        if(holder.cbCategoria.isChecked()){
                            filtroCategorias.add(categoria);

                        }
                        else{
                            for(int i=0;i<filtroCategorias.size();i++){
                                if(filtroCategorias.get(i).compareTo(categoria) == 0){
                                    filtroCategorias.remove(i);
                                    break;
                                }
                            }
                        }
                    }
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull Empresas.AdaptadorCategorias.MyViewHolder myViewHolder, int i) {

            boolean marca = false;

            for(int j=0;j<gs.filtroCategorias.size();j++){
                if(gs.filtroCategorias.get(j).compareTo(categorias.get(i)) == 0){
                    marca = true;
                }
            }
            myViewHolder.cbCategoria.setChecked(marca);
            myViewHolder.cbCategoria.setText(categorias.get(i));
        }

        @Override
        public int getItemCount() {
            return categorias.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private RelativeLayout item_categoria;
            private CheckBox cbCategoria;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                item_categoria = itemView.findViewById(R.id.item_categoria);
                cbCategoria = itemView.findViewById(R.id.cbCategoria);
            }
        }
    }

}
