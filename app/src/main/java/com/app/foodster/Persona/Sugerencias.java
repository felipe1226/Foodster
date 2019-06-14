package com.app.foodster.Persona;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.app.foodster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Sugerencias extends AppCompatActivity {

    GlobalState gs;
    ProgressDialog carga;

    ImageButton btnConfirmar;
    ImageButton btnCancelar;

    EditText etTitulo;
    EditText etMensaje;


    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerencias);

        gs = (GlobalState) getApplication();
        request = Volley.newRequestQueue(getApplicationContext());

        carga = new ProgressDialog(this);
        carga.setMessage("Enviando...");
        carga.setCanceledOnTouchOutside(false);

        btnConfirmar = findViewById(R.id.btnAplicar);
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });
        btnCancelar = findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etTitulo = findViewById(R.id.etNombre);
        etMensaje = findViewById(R.id.etMensaje);

    }

    private void validarDatos(){
        String titulo = etTitulo.getText().toString().trim();
        String mensaje = etMensaje.getText().toString().trim();

        if(titulo.compareTo("") != 0 && mensaje.compareTo("") != 0){
            enviar(titulo, mensaje);
        }
        else{
            Toast.makeText(this, "Complete los campos, por favor", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviar(String titulo, String mensaje){
        String url = "http://" + gs.getIp() + "/Persona/registrar_sugerencia.php?idPersona="+gs.getIdPersona()
                +"&titulo="+titulo+"&mensaje="+mensaje;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("registro");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        Toast.makeText(getApplicationContext(), "Muchas gracias por tu mensaje!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error al registrar", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                carga.cancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                detectarError(error);
            }
        });
        request.add(jsonObjectRequest);
    }

    /*@RequiresApi(api = Build.VERSION_CODES.M)
    public void onResponse(JSONObject response) {

        JSONArray datos = response.optJSONArray("registro");
        JSONObject jsonObject = null;
        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {
                Toast.makeText(this, "Muchas gracias por tu mensaje!", Toast.LENGTH_LONG).show();
                finish();
            }
            else{
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        carga.cancel();
    }*/

    /*@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Error "+ error.toString(), Toast.LENGTH_SHORT).show();
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
