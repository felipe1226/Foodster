package com.app.foodster.Producto;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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

public class InformacionProducto extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener{

    GlobalState gs;

    ArrayList<ListaProductos> listaProductos;

    String consulta;

    int idProducto;
    boolean favorito;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_producto);

        gs = (GlobalState)getApplication();

        request = Volley.newRequestQueue(getApplicationContext());

        Bundle datos = this.getIntent().getExtras();
        idProducto = datos.getInt("id");

        listaProductos = gs.getDatosProducto();

        favorito = false;

        btnRegresar = findViewById(R.id.btnRegresar);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvCarta = findViewById(R.id.tvCarta);
        tvNombre = findViewById(R.id.tvNombre);
        tvPrecio = findViewById(R.id.tvPrecio);
        tvDescProducto = findViewById(R.id.tvDescProducto);
        tvPromocion = findViewById(R.id.tvPromocion);

        tvDescuento = findViewById(R.id.tvDescuento);
        tvDescPromocion = findViewById(R.id.tvDescPromocion);
        tvFecha = findViewById(R.id.tvFecha);

        ivFavorito = findViewById(R.id.ivFavorito);
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

        ivCarrito = findViewById(R.id.ivCarrito);
        ivCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarCarrito();
            }
        });

        mostrarInformacion();
    }

    private void mostrarInformacion() {
        boolean existeProducto = false;

        if(listaProductos != null){
            for(int i=0;i<listaProductos.size();i++){
                if(listaProductos.get(i).getId() == idProducto){
                    existeProducto = true;


                    tvCarta.setText(listaProductos.get(i).getCarta());
                    tvNombre.setText(listaProductos.get(i).getNombre());
                    tvDescProducto.setText(listaProductos.get(i).getDescripcion());

                    int precio = listaProductos.get(i).getPrecio();
                    tvPrecio.setText("$" + precio);
                    if(listaProductos.get(i).getPromocion() != 0){
                        calcularPromocion(precio, listaProductos.get(i).getDescuento(), listaProductos.get(i).getDescPromocion(),
                                listaProductos.get(i).getFecha());
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

    private void calcularPromocion(int precio, int descuento, String descripcion, String fecha){
        tvPrecio.setPaintFlags( tvPrecio.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        int precioPromocion = (int)(precio - ( precio * ((double)descuento/100)));
        tvDescuento.setText(descuento + "%");
        tvPromocion.setText("$"+precioPromocion);

        tvDescPromocion.setText(descripcion);
        tvFecha.setText(fecha);
    }


    private void consultarProducto() {

        consulta = "producto";
        String url = "http://" + gs.getIp() + "/Empresa/consultar_producto.php?idProducto="+idProducto;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void consultarFavorito() {

        consulta = "favorito";
        String url = "http://" + gs.getIp() + "/Persona/consultar_favorito.php?idPersona="+gs.getIdPersona()+"&idProducto="+idProducto;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }


    private void agregarFavorito() {
        consulta = "agregar_favorito";
        String url = "http://" + gs.getIp() + "/Persona/agregar_producto_favorito.php?idPersona="+gs.getIdPersona()+"&idProducto="+idProducto;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void eliminarFavorito() {
        consulta = "eliminar_favorito";
        String url = "http://" + gs.getIp() + "/Persona/eliminar_producto_favorito.php?idPersona="+gs.getIdPersona()+"&idProducto="+idProducto;

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void agregarCarrito() {
        consulta = "carrito";
        String url = "http://" + gs.getIp() + "/Persona/agregar_carrito.php?idPersona="+gs.getIdPersona()+"&idProducto="+idProducto;

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
            if(jsonObject != null){
                if(jsonObject.optInt("id") != 0) {
                    if(consulta.compareTo("producto") == 0){
                        String carta = (jsonObject.optString("carta"));
                        String producto = (jsonObject.optString("producto"));
                        String descProducto = (jsonObject.optString("descProducto"));
                        int precio = (jsonObject.optInt("precio"));
                        int promocion = (jsonObject.optInt("promocion"));

                        tvCarta.setText(carta);
                        tvNombre.setText(producto);
                        tvDescProducto.setText(descProducto);
                        tvPrecio.setText("$"+precio);

                        if(promocion != 0){
                            tvPrecio.setPaintFlags( tvPrecio.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                            String descPromocion = (jsonObject.optString("descripcion"));
                            int descuento = (jsonObject.optInt("descuento"));
                            String fecha = jsonObject.optString("fecha_inicio") + " - " + jsonObject.optString("fecha_fin");

                            calcularPromocion(precio, descuento, descPromocion, fecha );
                        }
                    }
                    if(consulta.compareTo("favorito") == 0){
                        ivFavorito.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                        favorito = true;
                    }
                    if(consulta.compareTo("agregar_favorito") == 0){
                        ivFavorito.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                        Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show();
                        favorito = true;
                    }
                    if(consulta.compareTo("eliminar_favorito") == 0){
                        ivFavorito.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_favorite_border));
                        favorito = false;
                    }
                    if(consulta.compareTo("carrito") == 0){
                        gs.setActualizaCarrito(true);
                        Toast.makeText(this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    if(consulta.compareTo("favorito") == 0){
                        ivFavorito.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_favorite_border));
                        favorito = false;
                    }
                    if(consulta.compareTo("agregar_favorito") == 0 || consulta.compareTo("eliminar_favorito") == 0){
                        Toast.makeText(this, "Error en la acción", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else{
                Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(consulta.compareTo("producto") == 0){
            consultarFavorito();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "Error de consulta "+ error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("ERROR", error.toString());
    }
}
