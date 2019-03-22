package com.app.foodster.Persona;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
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

public class HiloPedidos extends AsyncTask<Void, Integer, Boolean> implements Response.Listener<JSONObject>, Response.ErrorListener  {

    Context context;
    View v;
    boolean existePedido;
    boolean cambioEstado;
    GlobalState gs;

    ArrayList<ListaPedido> listaPedidos;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public HiloPedidos(Context context,View v, GlobalState gs) {
        this.v = v;
        this.context = context;
        this.gs = gs;
    }

    public boolean isExistePedido() {
        return existePedido;
    }

    public void setExistePedido(boolean existePedido) {
        this.existePedido = existePedido;
    }

    public boolean isCambioEstado() {
        return cambioEstado;
    }

    public void setCambioEstado(boolean cambioEstado) {
        this.cambioEstado = cambioEstado;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        gs = (GlobalState)context.getApplicationContext();

        request = Volley.newRequestQueue(context);
        existePedido = true;
        cambioEstado = false;

        listaPedidos = new ArrayList<>();
    }

    public void consultarPedidos(){
        String url = "http://" + gs.getIp() + "/Persona/consultar_pedidos.php?idPersona="+gs.getIdPersona();

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try{
            while(existePedido){
                sleep(10000);
                consultarPedidos();
                //publishProgress(tiempo[0], tiempo[1], tiempo[2]);
            }
            onCancelled();
        }
        catch (InterruptedException e){
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
        if(!existePedido){
            onCancelled();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        gs.setHiloPedidos(null);
    }

    @Override
    public void onResponse(JSONObject response) {

        JSONArray datos = response.optJSONArray("pedido");
        JSONObject jsonObject = null;

        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {

                cambioEstado = false;
                int id = 0;
                String estado = "";
                int cont = 0;
                String empresa = "";
                String estadoCambio = "";
                if(listaPedidos.size() > 0){
                    for (int j=0;j<listaPedidos.size();j++){
                        for (int i = 0; i < datos.length(); i++) {
                            jsonObject = datos.getJSONObject(i);
                            id = jsonObject.optInt("id");
                            if(listaPedidos.get(j).getId() == id){
                                estado = jsonObject.optString("estado");
                                if(listaPedidos.get(j).getEstado().compareTo(estado) != 0){
                                    cambioEstado = true;
                                    empresa = jsonObject.optString("empresa");
                                    estadoCambio = estado;
                                    cont++;
                                    listaPedidos.get(j).setEstado(estado);
                                }
                            }
                        }
                    }

                }
                else{
                    for (int i = 0; i < datos.length(); i++) {
                        jsonObject = datos.getJSONObject(i);

                        listaPedidos.add(new ListaPedido(jsonObject.optInt("id"),
                                jsonObject.optString("empresa"),
                                jsonObject.optString("estado"),
                                jsonObject.optString("cola"),
                                jsonObject.optString("metodo_pago"),
                                jsonObject.optInt("costo")));

                    }
                }

                existePedido = true;


                if(cambioEstado){
                    if(cont == 1){
                        Snackbar.make(v, empresa+": Pedido en estado "+estadoCambio, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    else{
                        Snackbar.make(v, "Cambio de estado en tus pedidos"+estadoCambio, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    if(gs.getFragment().getClass().toString().equals("Pedido")) {

                    }
                }
            }
            else{
                gs.setExistePedidos(false);
                existePedido = false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onErrorResponse(VolleyError error) {
        if(error.toString().compareTo("com.android.volley.TimeoutError") == 0){
            Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "Error "+ error.toString(), Toast.LENGTH_SHORT).show();
        }
        Log.i("ERROR", error.toString());
    }

}
