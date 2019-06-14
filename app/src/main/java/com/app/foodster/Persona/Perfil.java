package com.app.foodster.Persona;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class Perfil extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener{

    GlobalState gs;

    LinearLayout layout_datos;
    TextView tvDocumento;
    TextView tvNombre;
    TextView tvTelefono;
    TextView tvEmail;
    TextView tvLocalidad;


    Button btnEditar;

    String consulta;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gs = (GlobalState)getActivity().getApplication();

        request = Volley.newRequestQueue(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_perfil, container, false);

        layout_datos = v.findViewById(R.id.layout_datos);
        tvDocumento = v.findViewById(R.id.tvDocumento);
        tvNombre = v.findViewById(R.id.etNombre);
        tvTelefono = v.findViewById(R.id.tvTelefono);
        tvEmail = v.findViewById(R.id.tvEmail);
        tvLocalidad = v.findViewById(R.id.tvLocalidad);

        btnEditar = v.findViewById(R.id.btnEditar);
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditarPerfil.class);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        obtenerDatos();
    }

    private void obtenerDatos(){

        tvDocumento.setText(gs.getDocumento());
        tvNombre.setText(gs.getNombre());
        tvTelefono.setText(gs.getTelefono());
        tvEmail.setText(gs.getEmail());
        tvLocalidad.setText(gs.getCiudad() + ", "+ gs.getDepartamento());
    }

    public void onResponse(JSONObject response) {
        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;

        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject != null){
                if(jsonObject.optInt("id") != 0) {


                }
                else{

                }
            }
            else{
                Toast.makeText(getContext(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error de consulta "+ error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("ERROR", error.toString());
    }
}
