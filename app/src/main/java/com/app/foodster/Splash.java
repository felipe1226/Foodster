package com.app.foodster;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
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
import com.app.foodster.Empresa.DatosEmpresa;
import com.app.foodster.Persona.Documentos;
import com.app.foodster.Persona.HiloPedidos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Splash extends AppCompatActivity {

    GlobalState gs;

    String tipoConexion;

    int idUsuario;
    String usuario;
    String password;
    String recordar;

    private ArrayList<DatosEmpresa> datosEmpresa;
    ArrayList<String> categorias;

    View view;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        view = findViewById(R.layout.activity_splash);

        gs = (GlobalState)getApplication();
        request = Volley.newRequestQueue(getApplicationContext());

    }

    private boolean verificarConexion(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable()) {
            if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                tipoConexion = "mobile";
            }
            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                tipoConexion = "wifi";
            }
            return true;
        } else {
            return false;
        }
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
            if (verificarConexion()){
                GenerarDatos generarDatos = new GenerarDatos(gs, request);
                gs.setActualizaEmpresas(false);

                consultarPersona(usuario);
            }
            else{
                cargar(1);
                Toast.makeText(getApplicationContext(), "No tienes conexion.", Toast.LENGTH_SHORT).show();
            }
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

    public void consultarPersona(String usuario) {

            /*try {
                String passwdMd5 = this.toMd5(password);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }*/

        String url = "http://" + gs.getIp() + "/Usuario/consultar_persona.php?email=" + usuario;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("persona");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaPersona(jsonObject);
                        consultarPedidos();
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

    /*public void consultarUsuario(String usuario) {
        consulta = "usuario";
        String url = "http://" + gs.getIp() + "/Usuario/consultar_usuario.php?usuario=" + usuario;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }*/

    /*private void obtenerDatos() {

        consulta = "datos";
        String url = "http://" + gs.getIp() + "/Persona/consultar_persona.php?idPersona="+gs.getIdPersona();

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }*/

    private void consultarPedidos() {

        String url = "http://" + gs.getIp() + "/Persona/consultar_pedidos.php?idPersona="+gs.getIdPersona();

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("pedido");
                JSONObject jsonObject = null;
                try {
                    boolean existePedidos = false;
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        existePedidos = true;
                    }

                    consultaPedidos(existePedidos);
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
            verificarPreferencias();
    }

    /*@Override
    public void onResponse(JSONObject response) {

        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;

        boolean existePedidos = false;
        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {
                switch (consulta){

                    case "persona": consultaPersona(jsonObject);
                        break;

                    case "usuario": consultaUsuario(jsonObject);
                        break;

                    case "datos": consultaDatos(jsonObject); break;

                    case "pedido": existePedidos = true; break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        accionConsulta(existePedidos);
    }*/

    /*private void accionConsulta(boolean existePedidos){
        switch (consulta){

            case "persona" : consultarPedidos(); break;

            case "usuario" : break;

            case "pedido" : if(existePedidos){

                                gs.setExistePedidos(true);
                                HiloPedidos hiloPedidos = new HiloPedidos(getApplicationContext(), gs);
                                hiloPedidos.execute();
                                gs.setHiloPedidos(hiloPedidos);
                            }
                            else{
                                gs.setActualizaEmpresas(true);
                            }
                            cargar(2);
                            break;
        }
    }*/


    private void consultaPersona(JSONObject jsonObject){
        idUsuario = jsonObject.optInt("id");
        if(idUsuario != 0){
            String pass = jsonObject.optString("password");

            if(pass.compareTo(password) == 0){
                gs.setIdPersona(idUsuario);
                gs.setEmail(jsonObject.optString("email"));
                gs.setPassword(pass);

                gs.setNombre(jsonObject.optString("nombre"));
                gs.setTelefono(jsonObject.optString("telefono"));
                gs.setDepartamento(jsonObject.optString("departamento"));
                gs.setCiudad(jsonObject.optString("ciudad"));

                int tipo = jsonObject.optInt("tipo_documento");
                String numero = jsonObject.optString("numero_documento");

                ArrayList<Documentos> datos = gs.getDocumentos();
                for(int i=0;i<datos.size();i++){
                    if(datos.get(i).getId() == tipo){
                        gs.setDocumento(datos.get(i).getAbreviacion()+" "+numero);
                        break;
                    }
                }
            }
        }
    }

    private void consultaPedidos(boolean existePedidos){
        if(existePedidos){
            gs.setExistePedidos(true);
            HiloPedidos hiloPedidos = new HiloPedidos(getApplicationContext(), gs);
            hiloPedidos.execute();
            gs.setHiloPedidos(hiloPedidos);
        }
        else{
            gs.setActualizaEmpresas(true);
        }
        cargar(2);
    }

    /*private void consultaUsuario(JSONObject jsonObject){
        idUsuario = jsonObject.optInt("id");
        if(idUsuario != 0){
            String pass = jsonObject.optString("password");

            if(pass.compareTo(password) == 0){
                gs.setIdUsuario(idUsuario);
                gs.setUsuario(jsonObject.optString("usuario"));
                gs.setPassword(pass);
            }
        }
    }*/

    /*private void consultaDatos(JSONObject jsonObject){
        gs.setIdPersona(jsonObject.optInt("id"));
        gs.setNombre(jsonObject.optString("nombre"));
        gs.setTelefono(jsonObject.optString("telefono"));
        gs.setEmail(jsonObject.optString("email"));
        gs.setDepartamento(jsonObject.optString("departamento"));
        gs.setCiudad(jsonObject.optString("ciudad"));
    }*/

    /*@Override
    public void onErrorResponse(VolleyError error) {
        if(error.toString().compareTo("com.android.volley.TimeoutError") == 0){
            Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Error de login "+ error.toString(), Toast.LENGTH_SHORT).show();
        }
        Log.i("ERROR", error.toString());
    }*/
}
