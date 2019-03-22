package com.app.foodster.Persona;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

import static java.lang.Thread.sleep;

public class Pedido extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener{

    String consulta;

    GlobalState gs;

    ProgressBar progress;

    RecyclerView rvPedidos;

    ArrayList<ListaPedido> listaPedido;
    AdaptadorListaPedidos adaptadorListaPedidos;

    ArrayList<ListaProductoPedido> listaProductoPedido;
    AdaptadorProductoPedido adaptadorProductoPedido;

    HiloPedidos hiloPedidos = null;
    HiloEstados hiloEstados = null;


    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_pedido, container, false);

        progress = v.findViewById(R.id.progressBar);

        rvPedidos = v.findViewById(R.id.rvPedidos);

        listarPedidos();


        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gs = (GlobalState)getActivity().getApplication();

        request = Volley.newRequestQueue(getActivity().getApplicationContext());

        if(gs.getHiloPedidos() != null){
            hiloPedidos = gs.getHiloPedidos();
            hiloEstados = new HiloEstados();
            hiloEstados.execute();
        }
    }

    private void generarPedidos(){
        adaptadorListaPedidos = new AdaptadorListaPedidos(getContext(), listaPedido, listaProductoPedido);
        rvPedidos.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvPedidos.setAdapter(adaptadorListaPedidos);
    }



    public void listarPedidos(){
        consulta = "pedido";
        String url = "http://" + gs.getIp() + "/Persona/listar_pedidos.php?idPersona="+gs.getIdPersona();

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
                    listaProductoPedido = new ArrayList<>();

                    int idAnterior = 0;

                    for (int i = 0; i < datos.length(); i++) {
                        jsonObject = datos.getJSONObject(i);

                        int idPedido = jsonObject.optInt("id");

                        if(idPedido != idAnterior) {
                            idAnterior = idPedido;
                            listaPedido.add(new ListaPedido(idPedido,
                                                            jsonObject.optString("empresa"),
                                                            jsonObject.optString("estado"),
                                                            jsonObject.optString("cola"),
                                                            jsonObject.optString("metodo_pago"),
                                                            jsonObject.optInt("costo")));
                        }

                        listaProductoPedido.add(new ListaProductoPedido(jsonObject.optInt("idProducto"),
                                                    jsonObject.optInt("id_pedido"),
                                                    jsonObject.optString("nombre"),
                                                    jsonObject.optInt("precio"),
                                                    jsonObject.optString("detalles")));
                    }
                    generarPedidos();
                    progress.setVisibility(View.GONE);
                }
            }
            else{
                if(consulta.compareTo("pedido") == 0){
                    gs.setExistePedidos(false);
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
            Toast.makeText(getContext(), "Error de login "+ error.toString(), Toast.LENGTH_SHORT).show();
        }
        Log.i("ERROR", error.toString());
    }

    private class HiloEstados extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Toast.makeText(getContext(),"prueba", Toast.LENGTH_SHORT).show();
            try {
                while (gs.getHiloPedidos() != null) {
                    sleep(5000);
                    if (hiloPedidos.isCambioEstado()) {
                        listarPedidos();
                        Toast.makeText(getContext(), "Pedidos actualizados", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getContext(), "No actualiza", Toast.LENGTH_LONG).show();
                }
                onCancelled();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado){
                Toast.makeText(getContext(), "Hilo creado", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            hiloPedidos = null;
            hiloEstados = null;
        }
    }
}
