package com.app.foodster.Persona;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class Pedido extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener{

    String consulta;

    GlobalState gs;

    ProgressBar progress;

    TextView tvMensaje;
    RecyclerView rvPedidos;

    ArrayList<ListaPedido> listaPedido;
    AdaptadorPedidos adaptadorPedidos;

    ArrayList<ListaProductosPedido> listaProductosPedido;

    HiloPedidos hiloPedidos = null;
    boolean listar;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_pedido, container, false);

        progress = v.findViewById(R.id.progressBar);

        tvMensaje = v.findViewById(R.id.tvMensaje);
        rvPedidos = v.findViewById(R.id.rvPedidos);

        verificarPedidos();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gs = (GlobalState)getActivity().getApplication();
        gs.setFragment(this);

        request = Volley.newRequestQueue(getActivity().getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        if(gs.getHiloPedidos() != null){
            hiloPedidos = gs.getHiloPedidos();
            FragmentManager fm = getFragmentManager();
            Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);
            hiloPedidos.setFragment(currentFragment);
        }
        if(gs.isActualizaPedido() && listar){
            listaPedido = gs.getDatosPedido();
            generarPedidos();
        }
    }

    private void verificarPedidos(){
        if(!gs.isActualizaPedido()){
            listar = false;
            if(gs.getDatosPedido() != null && gs.getDatosPedido().size() > 0){

                listaPedido = gs.getDatosPedido();
                listaProductosPedido = gs.getDatosProductoPedido();

                generarPedidos();
            }
            else{
                rvPedidos.setVisibility(View.GONE);
                tvMensaje.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        }
        else{
            if(!listar){
                gs.setActualizaPedido(false);
                listar = true;
                listarPedidos();
            }
        }
    }

    private void generarPedidos(){

        adaptadorPedidos = new AdaptadorPedidos(this,getContext(), listaPedido, listaProductosPedido);
        rvPedidos.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvPedidos.setAdapter(adaptadorPedidos);

        rvPedidos.setVisibility(View.VISIBLE);
        tvMensaje.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
    }

    public void listarPedidos(){
        consulta = "pedido";
        String url = "http://" + gs.getIp() + "/Persona/listar_pedidos.php?idPersona="+gs.getIdPersona();

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    public void confirmarRecibido(int idPedido){
        consulta = "recibido";
        String url = "http://" + gs.getIp() + "/Persona/confirmar_recibido.php?idPedido="+idPedido;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onResponse(JSONObject response) {

        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;

        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {
                if(consulta.compareTo("pedido") == 0){

                    listaPedido = new ArrayList<>();
                    listaProductosPedido = new ArrayList<>();

                    int idAnterior = 0;

                    for (int i = 0; i < datos.length(); i++) {
                        jsonObject = datos.getJSONObject(i);

                        int idPedido = jsonObject.optInt("id");

                        if(idPedido != idAnterior) {
                            idAnterior = idPedido;
                            listaPedido.add(new ListaPedido(idPedido,
                                                            jsonObject.optString("empresa"),
                                                            jsonObject.optString("estado"),
                                                            jsonObject.optInt("cola"),
                                                            jsonObject.optString("metodo_pago"),
                                                            jsonObject.optInt("costo")));
                        }

                        listaProductosPedido.add(new ListaProductosPedido(jsonObject.optInt("idProducto"),
                                                    jsonObject.optInt("id_pedido"),
                                                    jsonObject.optString("nombre"),
                                                    jsonObject.optInt("precio"),
                                                    jsonObject.optInt("promocion"),
                                                    jsonObject.optString("detalles")));
                    }
                    generarPedidos();

                    gs.setDatosPedido(listaPedido);
                    gs.setDatosProductoPedido(listaProductosPedido);


                }
                if(consulta.compareTo("recibido") == 0){
                    jsonObject = datos.getJSONObject(0);
                    for(int j=0;j<listaPedido.size();j++){
                        if( jsonObject.optInt("id")== listaPedido.get(j).getId()){
                            listaPedido.remove(j);
                            break;
                        }
                    }
                    gs.setActualizaHistorico(true);
                    adaptadorPedidos.actualizar(listaPedido);
                    if(listaPedido.size() == 0){
                        hiloPedidos.setExistePedido(false);
                        rvPedidos.setVisibility(View.GONE);
                        tvMensaje.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                    }
                }
            }
            else{
                if(consulta.compareTo("pedido") == 0){
                    gs.setExistePedidos(false);
                    progress.setVisibility(View.GONE);
                    rvPedidos.setVisibility(View.GONE);
                    tvMensaje.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onErrorResponse(VolleyError error) {
        if(error.toString().compareTo("com.android.volley.TimeoutError") == 0){
            Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(), "Error "+ error.toString(), Toast.LENGTH_SHORT).show();
        }
        Log.i("ERROR", error.toString());
    }

    @Override
    public void onStop() {
        super.onStop();
        if(gs.getHiloPedidos() != null){
            hiloPedidos.setFragment(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(gs.getHiloPedidos() != null){
            hiloPedidos.setFragment(null);
        }
    }
}
