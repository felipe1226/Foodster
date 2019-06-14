package com.app.foodster.Producto;


import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.app.foodster.Empresa.DatosEmpresa;
import com.app.foodster.GlobalState;
import com.app.foodster.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InformacionProducto extends Fragment {

    GlobalState gs;

    ArrayList<ListaProductos> listaProductos;

    int idEmpresa;
    int idProducto;

    boolean favorito;

    ImageView ivFoto1;
    ImageView ivFoto2;
    ImageView ivFoto3;

    TextView tvCarta;
    TextView tvNombre;
    TextView tvPrecio;
    TextView tvDescProducto;
    TextView tvPromocion;

    TextView tvDescuento;
    TextView tvDescPromocion;
    TextView tvFecha;

    ImageButton btnRegresar;
    ImageButton ivFavorito;
    ImageButton ivCarrito;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_informacion_producto, container, false);

        favorito = false;

        btnRegresar = v.findViewById(R.id.btnRegresar);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        ivFoto1 = v.findViewById(R.id.ivFoto1);
        ivFoto2 = v.findViewById(R.id.ivFoto2);
        ivFoto3 = v.findViewById(R.id.ivFoto3);

        tvCarta = v.findViewById(R.id.tvCarta);
        tvNombre = v.findViewById(R.id.etNombre);
        tvPrecio = v.findViewById(R.id.tvPrecio);
        tvDescProducto = v.findViewById(R.id.tvDescProducto);
        tvPromocion = v.findViewById(R.id.tvPromocion);

        tvDescuento = v.findViewById(R.id.tvDescuento);
        tvDescPromocion = v.findViewById(R.id.tvDescPromocion);
        tvFecha = v.findViewById(R.id.etMes);

        ivFavorito = v.findViewById(R.id.ivFavorito);
        ivFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!favorito){
                    agregarFavorito();
                }
                else{
                    eliminarFavorito();
                }
            }
        });

        ivCarrito = v.findViewById(R.id.ivCarrito);
        ivCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarCarrito();
            }
        });

        verificarDomicilio();
        consultarFavorito();
        mostrarInformacion();

        return v;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gs = (GlobalState) getActivity().getApplication();
        listaProductos = gs.getDatosProducto();

        request = Volley.newRequestQueue(getActivity().getApplicationContext());

        idEmpresa = getArguments().getInt("idEmpresa");
        idProducto = getArguments().getInt("idProducto");

    }

    private void verificarDomicilio(){
        ArrayList<DatosEmpresa> empresas = gs.getDatosEmpresa();
        for(int i=0;i<empresas.size();i++){
            if(empresas.get(i).getId() == idEmpresa && empresas.get(i).getDomicilio() == 0){
                    ivCarrito.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void mostrarInformacion() {
        boolean existeProducto = false;

        if(listaProductos != null){
            for(int i=0;i<listaProductos.size();i++){
                if(listaProductos.get(i).getId() == idProducto){
                    existeProducto = true;

                    if(listaProductos.get(i).getbFoto1() != null){
                        ivFoto1.setImageBitmap(listaProductos.get(i).getbFoto1());
                    }
                    else{
                        consultarImagen(listaProductos.get(i).getFoto1(), ivFoto1, i, 1);
                    }
                    if(listaProductos.get(i).getbFoto2() != null){
                        ivFoto2.setImageBitmap(listaProductos.get(i).getbFoto2());

                    }
                    else{
                        if(listaProductos.get(i).getFoto2() != null) {
                            consultarImagen(listaProductos.get(i).getFoto2(), ivFoto2, i, 2);
                            ivFoto2.setVisibility(View.VISIBLE);
                        }
                    }
                    if(listaProductos.get(i).getbFoto3() != null){
                        ivFoto3.setImageBitmap(listaProductos.get(i).getbFoto3());
                    }
                    else{
                        if(listaProductos.get(i).getFoto3() != null){
                            consultarImagen(listaProductos.get(i).getFoto3(), ivFoto3, i, 3);
                            ivFoto3.setVisibility(View.VISIBLE);
                        }

                    }
                    tvNombre.setText(listaProductos.get(i).getNombre());
                    tvDescProducto.setText(listaProductos.get(i).getDescripcion());

                    int precio = listaProductos.get(i).getPrecio();
                    tvPrecio.setText("$" + precio);
                    if(listaProductos.get(i).getPromocion() != 0){
                        tvPrecio.setPaintFlags( tvPrecio.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        tvDescuento.setText(listaProductos.get(i).getDescuento() + "%");
                        tvPromocion.setText("$"+listaProductos.get(i).getPromocion());

                        tvDescPromocion.setText(listaProductos.get(i).getDescripcion());
                        tvFecha.setText(listaProductos.get(i).getFecha());
                    }
                }
            }
        }

        if(!existeProducto){
            consultarProducto();
        }
        else{
            consultarFavorito();
        }
    }

    private void consultarImagen(String url, ImageView ivFoto, int i, int ind){
        Picasso.with(getContext())
                .load("http://foodster.com.co/back-end/"+url)
                .error(R.mipmap.ic_launcher)
                .fit()
                .centerInside()
                .into(ivFoto);
        switch (ind){
            case 1: listaProductos.get(i).setbFoto1(ivFoto.getDrawingCache());
            break;

            case 2: listaProductos.get(i).setbFoto2(ivFoto.getDrawingCache());
                break;

            case 3: listaProductos.get(i).setbFoto3(ivFoto.getDrawingCache());
                break;
        }

        gs.setDatosProducto(listaProductos);
    }

    private void calcularPromocion(int precio, int descuento, String descripcion, String fecha){
        tvPrecio.setPaintFlags( tvPrecio.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        int precioPromocion = (int)(precio - ( precio * ((double)descuento/100)));
        tvDescuento.setText(descuento + "%");
        tvPromocion.setText("$"+precioPromocion);

        tvDescPromocion.setText(descripcion);
        tvFecha.setText(fecha);
    }

    private void consultarProducto() {

        String url = "http://" + gs.getIp() + "/Empresa/consultar_producto.php?idProducto="+idProducto;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("producto");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaProducto(jsonObject, datos);
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

    private void consultarFavorito() {

        String url = "http://" + gs.getIp() + "/Persona/consultar_favorito.php?idPersona="+gs.getIdPersona()+"&idProducto="+idProducto;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("favorito");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        ivFavorito.setImageResource(R.drawable.ic_favorite);
                        favorito = true;
                    }
                    else{
                        ivFavorito.setImageResource(R.drawable.ic_favorite_border);
                        favorito = false;
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


    private void agregarFavorito() {
        String url = "http://" + gs.getIp() + "/Persona/agregar_producto_favorito.php?idPersona="+gs.getIdPersona()+"&idProducto="+idProducto;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("agregar_favorito");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        ivFavorito.setImageResource(R.drawable.ic_favorite);
                        gs.setActualizaProductosFavoritos(true);
                        favorito = true;

                        Toast.makeText(getContext(), R.string.mensaje_agregar_favorito, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "Error en la acción", Toast.LENGTH_SHORT).show();
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

    private void eliminarFavorito() {
        String url = "http://" + gs.getIp() + "/Persona/eliminar_producto_favorito.php?idPersona="+gs.getIdPersona()+"&idProducto="+idProducto;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("eliminar_favorito");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        ivFavorito.setImageResource(R.drawable.ic_favorite_border);
                        gs.setActualizaProductosFavoritos(true);
                        favorito = false;
                    }
                    else{
                        Toast.makeText(getContext(), "Error en la acción", Toast.LENGTH_SHORT).show();
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

    private void agregarCarrito() {
        String url = "http://" + gs.getIp() + "/Persona/agregar_carrito.php?idPersona="+gs.getIdPersona()+"&idProducto="+idProducto;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("carrito");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        gs.setActualizaCarrito(true);
                        Toast.makeText(getContext(), getString(R.string.mensaje_agregar_carrito), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "Error en la acción", Toast.LENGTH_SHORT).show();
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



    /*@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResponse(JSONObject response) {
        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;

        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject != null){
                if(jsonObject.optInt("id") != 0) {
                    if(consulta.compareTo("producto") == 0){
                        consultaProducto(jsonObject, datos);
                    }
                    if(consulta.compareTo("favorito") == 0){
                        ivFavorito.setImageResource(R.drawable.ic_favorite);
                        favorito = true;
                    }
                    if(consulta.compareTo("agregar_favorito") == 0){
                        ivFavorito.setImageResource(R.drawable.ic_favorite);
                        Toast.makeText(getContext(), R.string.mensaje_agregar_favorito, Toast.LENGTH_SHORT).show();
                        gs.setActualizaProductosFavoritos(true);
                        favorito = true;
                    }
                    if(consulta.compareTo("eliminar_favorito") == 0){
                        ivFavorito.setImageResource(R.drawable.ic_favorite_border);
                        gs.setActualizaProductosFavoritos(true);
                        favorito = false;
                    }
                    if(consulta.compareTo("carrito") == 0){
                        gs.setActualizaCarrito(true);
                        Toast.makeText(getContext(), getString(R.string.mensaje_agregar_carrito), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    if(consulta.compareTo("favorito") == 0){
                        ivFavorito.setImageResource(R.drawable.ic_favorite_border);
                        favorito = false;
                    }
                    if(consulta.compareTo("agregar_favorito") == 0 || consulta.compareTo("eliminar_favorito") == 0){
                        Toast.makeText(getContext(), "Error en la acción", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else{
                Toast.makeText(getContext(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(consulta.compareTo("producto") == 0){
            mostrarInformacion();
            consultarFavorito();
        }
    }*/

    private void consultaProducto(JSONObject jsonObject, JSONArray datos) throws JSONException {
        int id = (jsonObject.optInt("id"));
        String carta = (jsonObject.optString("carta"));
        String producto = (jsonObject.optString("producto"));
        String descProducto = (jsonObject.optString("descProducto"));
        int precio = (jsonObject.optInt("precio"));
        int promocion = (jsonObject.optInt("promocion"));

        if(promocion != 0){
            String descPromocion = (jsonObject.optString("descripcion"));
            int descuento = (jsonObject.optInt("descuento"));
            promocion = (int)(precio - ( precio * (descuento/100)));
            String fecha = jsonObject.optString("fecha_inicio") + " - " + jsonObject.optString("fecha_fin");
            listaProductos.add(new ListaProductos(idEmpresa, id, idProducto, producto, descProducto, precio,
                    promocion, descPromocion, descuento, fecha));
        }
        else{
            listaProductos.add(new ListaProductos(idEmpresa, id, idProducto, producto, descProducto, precio, promocion));
        }

        for (int i = 0; i < datos.length(); i++) {
            jsonObject = datos.getJSONObject(i);
            String imagen = (jsonObject.optString("imagen"));
            if(imagen != null){
                if(i == 0){
                    listaProductos.get(listaProductos.size()-1).setFoto1(imagen);
                }
                if(i == 1){
                    listaProductos.get(listaProductos.size()-1).setFoto2(imagen);
                }
                if(i == 2){
                    listaProductos.get(listaProductos.size()-1).setFoto3(imagen);
                }
            }
        }

        mostrarInformacion();
    }

    /*@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Error de consulta "+ error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("ERROR", error.toString());
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
