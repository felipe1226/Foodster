package com.app.foodster.Persona;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.RequiresApi;
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


public class HistoricoPedidos extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {

    GlobalState gs;

    String consulta;

    private AdaptadorHistoricoPedidos adaptadorHistorico;
    private ArrayList<ListaHistoricoPedidos> listaHistorico;

    private ArrayList<ListaProductosHistorico> listaProductosHistorico;


    ProgressBar progress;

    TextView tvMensaje;
    RecyclerView rvPedidos;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gs = (GlobalState) getActivity().getApplicationContext();
        request = Volley.newRequestQueue(getActivity().getApplicationContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_historico_pedidos, container, false);

        progress = v.findViewById(R.id.progressBar);

        tvMensaje = v.findViewById(R.id.tvMensaje);

        rvPedidos = v.findViewById(R.id.rvPedidos);

        verificarPedidos();

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void verificarPedidos(){
        if(!gs.isActualizaHistorico()){
            if(gs.getDatosHistorico().size() > 0){
                listaHistorico = gs.getDatosHistorico();
                listaProductosHistorico = gs.getDatosProductoHistorico();
                tvMensaje.setVisibility(View.GONE);
                generarPedidos();
            }
            else{
                tvMensaje.setVisibility(View.VISIBLE);
            }
            progress.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generarPedidos(){

        adaptadorHistorico = new AdaptadorHistoricoPedidos(getContext(), listaHistorico, listaProductosHistorico);
        rvPedidos.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvPedidos.setAdapter(adaptadorHistorico);

        rvPedidos.setVisibility(View.VISIBLE);
        tvMensaje.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
    }

    public void listarPedidos(){
        consulta = "pedido";
        String url = "http://" + gs.getIp() + "/Persona/listar_historico_pedidos.php?idPersona="+gs.getIdPersona();

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResponse(JSONObject response) {
        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;

        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {
                if(consulta.compareTo("pedido") == 0){

                    listaHistorico = new ArrayList<>();
                    listaProductosHistorico = new ArrayList<>();

                    int idAnterior = 0;

                    for (int i = 0; i < datos.length(); i++) {
                        jsonObject = datos.getJSONObject(i);

                        int idPedido = jsonObject.optInt("id");

                        if(idPedido != idAnterior) {
                            idAnterior = idPedido;
                            String fecha[] = jsonObject.optString("fecha").split(" ");
                            listaHistorico.add(new ListaHistoricoPedidos(idPedido,
                                    jsonObject.optString("empresa"),
                                    jsonObject.optString("metodo_pago"),
                                    jsonObject.optInt("costo"),
                                    fecha[0]));
                        }

                        listaProductosHistorico.add(new ListaProductosHistorico(jsonObject.optInt("idProducto"),
                                jsonObject.optInt("id_pedido"),
                                jsonObject.optString("nombre"),
                                jsonObject.optInt("precio"),
                                jsonObject.optInt("promocion"),
                                jsonObject.optString("detalles")));
                    }
                    generarPedidos();

                    gs.setActualizaHistorico(false);
                    gs.setDatosHistorico(listaHistorico);
                    gs.setDatosProductoHistorico(listaProductosHistorico);
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
        Toast.makeText(getContext(), "Error "+ error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("ERROR", error.toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        if(gs.isActualizaHistorico()){
            listarPedidos();
        }
    }
}
