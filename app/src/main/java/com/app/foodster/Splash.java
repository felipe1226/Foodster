package com.app.foodster;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.foodster.Empresa.AdaptadorListaEmpresas;
import com.app.foodster.Empresa.DatosEmpresa;
import com.app.foodster.Empresa.ListaEmpresas;
import com.app.foodster.Persona.HiloPedidos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Splash extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener{

    GlobalState gs;


    String consulta;

    int idUsuario;
    String usuario;
    String password;
    String recordar;

    private ArrayList<DatosEmpresa> datosEmpresa;
    ArrayList<String> categorias;

    int cargar;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        gs = (GlobalState)getApplication();

        request = Volley.newRequestQueue(getApplicationContext());

    }

    public void verificarPreferencias(){
        SharedPreferences preferencesCuenta = getSharedPreferences("cuenta", Context.MODE_PRIVATE);
        usuario = preferencesCuenta.getString("usuario", "");
        password = preferencesCuenta.getString("password", "");
        recordar = preferencesCuenta.getString("recordar", "");
        final String session = preferencesCuenta.getString("session", "");

        if(session.compareTo("") == 0){
            cargar(1);
        }
        else{
            consultarUsuario(usuario);
        }
    }

    private void cargar(final int accion){
        new Handler().postDelayed(new Runnable() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                Transition transition = new Fade(Fade.OUT);
                transition.setDuration(1000);
                transition.setInterpolator(new DecelerateInterpolator());

                getWindow().setExitTransition(transition);
                Intent intent = null;
                if(accion == 1){
                    intent = new Intent(Splash.this, Login.class);

                    intent.putExtra("usuario", usuario);
                    intent.putExtra("password", password);
                    intent.putExtra("recordar", recordar);
                }
                else{
                    intent = new Intent(Splash.this, Principal.class);
                }

                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(Splash.this).toBundle());
            }
        }, 2000);
    }

    public void consultarUsuario(String usuario) {
        consulta = "usuario";
        String url = "http://" + gs.getIp() + "/Usuario/consultar_usuario.php?usuario=" + usuario;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void obtenerDatos() {

        consulta = "persona";
        String url = "http://" + gs.getIp() + "/Persona/consultar_persona.php?idUsuario="+idUsuario;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void consultarPedidos() {

        consulta = "pedido";
        String url = "http://" + gs.getIp() + "/Persona/consultar_pedidos.php?idPersona="+gs.getIdPersona();

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

    @Override
    protected void onPostResume() {
        super.onPostResume();

        verificarPreferencias();
    }

    @Override
    public void onResponse(JSONObject response) {

        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;

        boolean existePedidos = false;
        boolean existeCategoria;
        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {
                if(consulta.compareTo("usuario") == 0){
                    idUsuario = jsonObject.optInt("id");
                    if(idUsuario != 0){
                        String pass = jsonObject.optString("password");

                        if(pass.compareTo(password) == 0){
                            gs.setUsuario(jsonObject.optString("usuario"));
                            gs.setPassword(pass);
                        }
                    }
                }
                if(consulta.compareTo("persona") == 0){
                    gs.setIdPersona(jsonObject.optInt("id"));
                    gs.setNombre(jsonObject.optString("nombre"));
                    gs.setTelefono(jsonObject.optString("telefono"));
                    gs.setEmail(jsonObject.optString("email"));
                    gs.setidCiudad(jsonObject.optInt("id_ciudad"));
                }
                if(consulta.compareTo("pedido") == 0){
                    existePedidos = true;
                }
                if(consulta.compareTo("empresa") == 0){
                    datosEmpresa = new ArrayList<>();
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
                                jsonObject.optInt("metodo_pago"),
                                jsonObject.optString("direccion"),
                                jsonObject.optString("ubicacion"),
                                jsonObject.optString("telefono"),
                                jsonObject.optString("movil"),
                                jsonObject.optString("ciudad")));

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

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        gs.setActualizaEmpresas(true);
        if(consulta.compareTo("usuario") == 0){
            obtenerDatos();
        }
        else{
            if(consulta.compareTo("persona") == 0){
                consultarPedidos();
                //cargar(2);
            }
            else{
                if(consulta.compareTo("pedido") == 0){
                    if(existePedidos){
                        listarEmpresas();
                        gs.setExistePedidos(true);
                        HiloPedidos hiloPedidos = new HiloPedidos(getApplicationContext(), findViewById(android.R.id.content), gs);
                        hiloPedidos.execute();
                        gs.setHiloPedidos(hiloPedidos);
                    }
                    else{
                        cargar(2);
                    }
                }
                else{
                    if(consulta.compareTo("empresa") == 0){
                        cargar(2);
                    }
                }
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(error.toString().compareTo("com.android.volley.TimeoutError") == 0){
            Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Error de login "+ error.toString(), Toast.LENGTH_SHORT).show();
        }
        Log.i("ERROR", error.toString());
    }
}
