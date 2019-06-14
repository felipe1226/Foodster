package com.app.foodster.Persona;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.foodster.Empresa.DatosEmpresa;
import com.app.foodster.Empresa.InformacionEmpresa;
import com.app.foodster.GlobalState;
import com.app.foodster.Producto.InformacionProducto;
import com.app.foodster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Carrito extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {

    GlobalState gs;

    String consulta;

    private AdaptadorListaEmpresasCarrito adaptadorListaEmpresasCarrito;
    private ArrayList<ListaEmpresaCarrito> listaEmpresaCarrito;

    private AdaptadorListaCarrito adaptadorListaCarrito;
    private ArrayList<ListaCarrito> listaCarritos;

    private AdaptadorListaCarrito.MyViewHolder auxHolder = null;

    private ArrayList<ListaDireccion> listaDireccion;
    private Carrito.AdaptadorListaDireccion adaptadorListaDireccion;

    ProgressBar progressBar;
    ProgressDialog progressEliminar;

    ConstraintLayout layout_carrito;

    Button btnPedidos;

    TextView tvMensaje;
    TextView tvTotal;

    RecyclerView rvEmpresas;

    AlertDialog dialogPedido;
    ProgressDialog progress;

    int idCarrito;
    int total;
    int idPedido;
    int contProductos;
    String nombreProducto;
    int precioProducto;
    String detallesProducto;
    int promocionProducto;
    ArrayList<ListaCarrito> productos;
    int contEliminarCarritos;

    int empresaPedido;
    String direccionPedido;
    String ubicacionPedido;
    int costoPedido;
    int efectivoPedido;
    String pagoPedido;

    String auxDetalles;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_carrito, container, false);

        progressBar = v.findViewById(R.id.progressBar);

        progressEliminar = new ProgressDialog(getContext());
        progressEliminar.setMessage(getString(R.string.text_eliminando));
        progressEliminar.setCanceledOnTouchOutside(false);

        layout_carrito = v.findViewById(R.id.layout_carrito);

        btnPedidos = v.findViewById(R.id.btnPedidos);
        btnPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new Pedido();
                gs.setFragment(fragment);
                FragmentManager fm = getActivity().getSupportFragmentManager();

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container, fragment, fragment.getClass().toString()) // add and tag the new fragment
                        .commit();
            }
        });

        tvMensaje = v.findViewById(R.id.tvMensaje);
        tvTotal = v.findViewById(R.id.tvTotal);

        rvEmpresas = v.findViewById(R.id.rvEmpresas);

        verificarCarritos();

        return v;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gs = (GlobalState) getActivity().getApplication();
        gs.setFragment(this);
        request = Volley.newRequestQueue(getActivity().getApplicationContext());
    }

    private void verificarCarritos(){
        if(!gs.isActualizaCarrito()){
            if(gs.getDatosEmpresaCarrito().size() > 0){
                listaEmpresaCarrito = gs.getDatosEmpresaCarrito();
                listaCarritos = gs.getDatosCarrito();
                layout_carrito.setVisibility(View.VISIBLE);
                tvMensaje.setVisibility(View.GONE);
                total = 0;
                generarEmpresasCarritos();
            }
            else{
                layout_carrito.setVisibility(View.GONE);
                tvMensaje.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    private void generarEmpresasCarritos(){
        if(total == 0){
            for(int i=0;i<listaEmpresaCarrito.size();i++){
                total += listaEmpresaCarrito.get(i).getTotal();
            }
        }

        tvTotal.setText("$" + total);

        adaptadorListaEmpresasCarrito = new AdaptadorListaEmpresasCarrito(getContext(), listaEmpresaCarrito);
        rvEmpresas.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvEmpresas.setAdapter(adaptadorListaEmpresasCarrito);
    }

    private void verificarEmpresasCarritos(){

        int totalPedido = 0;
        total = 0;

        boolean existeEmpresa = false;
        if(listaCarritos.size() > 0){
            for(int i=0;i<listaEmpresaCarrito.size();i++){
                totalPedido = 0;
                existeEmpresa = false;
                for(int j=0;j<listaCarritos.size();j++) {
                    if (listaEmpresaCarrito.get(i).getId() == listaCarritos.get(j).getIdEmpresa()) {
                        totalPedido = calcularTotalEmpresa(listaCarritos.get(j).getPrecio(), listaCarritos.get(j).getPromocion(), listaCarritos.get(j).getDescuento(), totalPedido, i);
                        existeEmpresa = true;
                    }
                }
                if(!existeEmpresa){
                    listaEmpresaCarrito.remove(i);
                    break;
                }
            }
        }

        gs.setDatosEmpresaCarrito(listaEmpresaCarrito);
        gs.setDatosCarrito(listaCarritos);

        tvTotal.setText("$" + total);
        generarEmpresasCarritos();
    }

    private int calcularTotalEmpresa(int precio, int promocion, int descuento, int totalEmpresa, int ind){
        if(promocion == 0){
            total += precio;
            totalEmpresa += precio;
        }
        else{
            promocion = (int)(precio - ( precio * ((double)descuento/100)));
            total += promocion;
            totalEmpresa += promocion;
        }

        listaEmpresaCarrito.get(ind).setTotal(totalEmpresa);
        return totalEmpresa;
    }

    private void listarDirecciones() {

        consulta = "direccion";
        String url = "http://" + gs.getIp() + "/Persona/listar_direcciones.php?idPersona="+gs.getIdPersona();

        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void dialogPedido(){
        AlertDialog.Builder buider = new AlertDialog.Builder(getContext());
        buider.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_pedido, null);

        progress = new ProgressDialog(getContext());
        progress.setMessage("Enviando pedido");
        progress.setCanceledOnTouchOutside(false);

        final LinearLayout layout_direcciones = view.findViewById(R.id.layout_direcciones);
        RecyclerView rvDirecciones = view.findViewById(R.id.rvDirecciones);

        adaptadorListaDireccion = new Carrito.AdaptadorListaDireccion(getContext(), listaDireccion);
        rvDirecciones.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        rvDirecciones.setAdapter(adaptadorListaDireccion);


        final LinearLayout layout_metodo_pago = view.findViewById(R.id.layout_metodo_pago);
        TextView tvTotalPago = view.findViewById(R.id.tvTotal);
        tvTotalPago.setText(String.valueOf(costoPedido));

        final LinearLayout layout_efectivo = view.findViewById(R.id.layout_efectivo);
        final LinearLayout layout_tarjeta = view.findViewById(R.id.layout_tarjeta);

        final RadioButton rbEfectivo = view.findViewById(R.id.rbEfectivo);
        final RadioButton rbTarjeta = view.findViewById(R.id.rbTarjeta);

        ArrayList<DatosEmpresa> empresas = gs.getDatosEmpresa();
        for(int i=0;i<empresas.size();i++){
            if(empresas.get(i).getId() == empresaPedido && empresas.get(i).getPago() == 0){
                rbTarjeta.setVisibility(View.GONE);
                break;
            }
        }

        rbEfectivo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rbEfectivo.isChecked()){
                    pagoPedido = "Efectivo";
                    layout_efectivo.setVisibility(View.VISIBLE);
                    layout_tarjeta.setVisibility(View.GONE);
                }
            }
        });

        rbTarjeta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(rbTarjeta.isChecked()){
                    pagoPedido = "Tarjeta";
                    layout_efectivo.setVisibility(View.GONE);
                    layout_tarjeta.setVisibility(View.VISIBLE);
                }
            }
        });

        final EditText etEfectivo = view.findViewById(R.id.etEfectivo);
        final TextView tvDevuelta = view.findViewById(R.id.tvDevuelta);
        tvDevuelta.setText("Devuelta: $0");
        final TextView tvMensaje = view.findViewById(R.id.tvMensaje);

        etEfectivo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvMensaje.setVisibility(View.GONE);
                if(etEfectivo.getText().toString().compareTo("") != 0){
                    efectivoPedido = Integer.parseInt(etEfectivo.getText().toString());
                    int devuelta = efectivoPedido - costoPedido;
                    if(devuelta < 0){
                        devuelta = 0;
                    }
                    tvDevuelta.setText("Devuelta: $" + devuelta);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        final Button btnCancelar = view.findViewById(R.id.btnCancelar);
        final Button btnConfirmar = view.findViewById(R.id.btnAplicar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnCancelar.getText().toString().compareTo("Cancelar") == 0){
                    dialogPedido.cancel();
                }
                else{
                    layout_direcciones.setVisibility(View.VISIBLE);
                    layout_metodo_pago.setVisibility(View.GONE);
                    btnCancelar.setText("Cancelar");
                    btnConfirmar.setText("Siguiente");
                }
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnConfirmar.getText().toString().compareTo("Siguiente") == 0){
                    layout_direcciones.setVisibility(View.GONE);
                    layout_metodo_pago.setVisibility(View.VISIBLE);
                    btnCancelar.setText("Regresar");
                    btnConfirmar.setText("Confirmar");
                }
                else{
                    if( (efectivoPedido - costoPedido) >= 0){
                        progress.show();
                        InputMethodManager inputMethodManager = (InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(etEfectivo.getWindowToken(), 0);
                        realizarPedido();
                    }
                    else{
                        tvMensaje.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        buider.setView(view);
        dialogPedido = buider.create();
        dialogPedido.show();
    }

    public void listarCarrito(){
        String url = "http://" + gs.getIp() + "/Persona/listar_carrito.php?idPersona="+gs.getIdPersona();
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("carrito");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaCarritos(jsonObject, datos);
                    }
                    else{
                        progressBar.setVisibility(View.GONE);
                        layout_carrito.setVisibility(View.GONE);
                        tvMensaje.setVisibility(View.VISIBLE);
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

    private void actualizarDetalles(String detalles){
        String url = "http://" + gs.getIp() + "/Persona/actualizar_detalles.php?idCarrito="+idCarrito+"&detalles="+detalles;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("actualizar_detalles");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaActualizar();
                    }
                    else {
                        Toast.makeText(getContext(), "No se pudo actualizar los detalles", Toast.LENGTH_SHORT).show();
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

    private void eliminarCarrito(){
        String url = "http://" + gs.getIp() + "/Persona/eliminar_carrito.php?idCarrito="+idCarrito;
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray datos = response.optJSONArray("eliminar_carrito");
                JSONObject jsonObject = null;
                try {
                    jsonObject = datos.getJSONObject(0);
                    if (jsonObject.optString("id").compareTo("0") != 0) {
                        consultaEliminar();
                    }
                    else{
                        Toast.makeText(getContext(), "No se pudo eliminar del carrito", Toast.LENGTH_SHORT).show();
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

    private void realizarPedido(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + gs.getIp() + "/Persona/realizar_pedido.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.compareTo("0") != 0){
                            String datos[] = response.split(":");
                            String id[] = datos[2].split(",");
                            idPedido = Integer.parseInt(id[0].substring(1, id[0].length()-1));
                            Toast.makeText(getContext(), "Pedido solicitado", Toast.LENGTH_SHORT).show();
                            obtenerProductos();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), "Error "+ error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();

                params.put("idEmpresa", String.valueOf(empresaPedido));
                params.put("idPersona", String.valueOf(gs.getIdPersona()));
                params.put("costo", String.valueOf(costoPedido));
                params.put("direccion", direccionPedido);
                params.put("ubicacion", ubicacionPedido);
                params.put("efectivo", String.valueOf(efectivoPedido));
                params.put("pago", pagoPedido);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void obtenerProductos(){
        productos = new ArrayList<>();

        for(int i=0;i<listaEmpresaCarrito.size();i++){
            if(listaEmpresaCarrito.get(i).getId() == empresaPedido){
                for(int j=0;j<listaCarritos.size();j++){
                    if(listaEmpresaCarrito.get(i).getId() == listaCarritos.get(j).getIdEmpresa()){
                        productos.add(listaCarritos.get(j));
                    }
                }
            }
        }

        contProductos = 0;

        asignarProducto();
        registrarProducto();
    }

    private void asignarProducto(){
        nombreProducto = productos.get(contProductos).getNombre();
        precioProducto = productos.get(contProductos).getPrecio();
        detallesProducto = productos.get(contProductos).getDetalles();
        promocionProducto = productos.get(contProductos).getPromocion();
    }

    private void registrarProducto(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + gs.getIp() + "/Persona/registrar_producto_pedido.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.compareTo("0") != 0){
                            contProductos++;

                        }
                        if(contProductos < productos.size()){
                            asignarProducto();
                            registrarProducto();
                        }
                        else{
                            Toast.makeText(getContext(), "Pedido solicitado", Toast.LENGTH_SHORT).show();
                            contEliminarCarritos = productos.size();
                            idCarrito = productos.get((contEliminarCarritos)-1).getIdCarrito();
                            eliminarCarrito();
                            progress.hide();
                            dialogPedido.cancel();
                            HiloPedidos hiloPedidos = new HiloPedidos(getContext(), gs);
                            FragmentManager fm = getFragmentManager();
                            Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);
                            hiloPedidos.setFragment(currentFragment);
                            hiloPedidos.execute();
                            gs.setHiloPedidos(hiloPedidos);

                            gs.setActualizaPedido(true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getContext(), "Error "+ error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();

                params.put("idPedido", String.valueOf(idPedido));
                params.put("nombre", nombreProducto);
                params.put("precio", String.valueOf(precioProducto));
                params.put("detalles", detallesProducto);
                params.put("promocion", String.valueOf(promocionProducto));

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void verInformacion(int idEmpresa, int idProducto, Fragment fragment, View v){
        Bundle args = new Bundle();
        args.putInt("idEmpresa", idEmpresa);
        if(idProducto != 0){
            args.putInt("idProducto", idProducto);
        }

        GlobalState gs = (GlobalState)getActivity().getApplicationContext();

        AppCompatActivity activity = (AppCompatActivity) v.getContext();

        fragment.setArguments(args);
        FragmentManager fm = activity.getSupportFragmentManager();
        Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);
        if(!fragment.getClass().toString().equals(currentFragment.getTag()))
        {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, fragment, fragment.getClass().toString()).addToBackStack(null) // add and tag the new fragment
                    .commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onResponse(JSONObject response) {

        JSONArray datos = response.optJSONArray(consulta);
        JSONObject jsonObject = null;
        try {
            jsonObject = datos.getJSONObject(0);
            if(jsonObject.optString("id").compareTo("0") != 0) {
                if(consulta.compareTo("carrito") == 0){
                    consultaCarritos(jsonObject, datos);

                }
                if(consulta.compareTo("actualizar_detalles") == 0){
                    consultaActualizar();
                }
                if(consulta.compareTo("eliminar_carrito") == 0){
                    consultaEliminar();
                }

                if(consulta.compareTo("direccion") == 0){
                    consultaDireccion(jsonObject, datos);
                }
            }
            else{
                if(consulta.compareTo("carrito") == 0){
                    progressBar.setVisibility(View.GONE);
                    layout_carrito.setVisibility(View.GONE);
                    tvMensaje.setVisibility(View.VISIBLE);
                }

                if(consulta.compareTo("eliminar_carrito") == 0){
                    Toast.makeText(getContext(), "No se pudo eliminar del carrito", Toast.LENGTH_SHORT).show();
                }
                if(consulta.compareTo("actualizar_detalles") == 0){
                    Toast.makeText(getContext(), "No se pudo actualizar los detalles", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        accionConsulta();
    }

    private void accionConsulta(){

        switch (consulta){
            case "direccion" : dialogPedido(); break;

            case "eliminar_carrito" : if(contEliminarCarritos == 0){
                progressEliminar.cancel();
                if(listaCarritos.size() > 0){
                    verificarEmpresasCarritos();
                }
                else{
                    listaEmpresaCarrito = new ArrayList<>();
                    gs.setDatosEmpresaCarrito(listaEmpresaCarrito);

                    progressBar.setVisibility(View.GONE);
                    layout_carrito.setVisibility(View.GONE);
                    tvMensaje.setVisibility(View.VISIBLE);
                }
            }
            else{
                idCarrito = productos.get((contEliminarCarritos)-1).getIdCarrito();
                eliminarCarrito();
            }
                break;
        }
    }

    private void consultaCarritos(JSONObject jsonObject, JSONArray datos) throws JSONException {
        listaEmpresaCarrito = new ArrayList<>();
        listaCarritos = new ArrayList<>();

        int idAnterior = 0;

        total = 0;
        int totalEmpresa = 0;
        for (int i = 0; i < datos.length(); i++) {
            jsonObject = datos.getJSONObject(i);

            int idEmpresa = jsonObject.optInt("idEmpresa");
            String empresa = jsonObject.optString("empresa");
            int cantidad = jsonObject.optInt("cantidad");
            int promocion = jsonObject.optInt("id_promocion");
            int precio = cantidad * jsonObject.optInt("precio");
            int descuento = jsonObject.optInt("descuento");
            String detalles = jsonObject.optString("detalles");

            if(idEmpresa != idAnterior) {
                idAnterior = idEmpresa;
                listaEmpresaCarrito.add(new ListaEmpresaCarrito(idEmpresa, empresa));
                totalEmpresa = 0;
            }

            totalEmpresa = calcularTotalEmpresa(precio, promocion, descuento, totalEmpresa, listaEmpresaCarrito.size()-1);

            if(promocion != 0){
                promocion = (int)(precio - ( precio * ((double)descuento/100)));
            }

            listaCarritos.add(new ListaCarrito(jsonObject.optInt("idCarrito"),
                    jsonObject.optInt("idProducto"),
                    idEmpresa,
                    empresa,
                    cantidad,
                    jsonObject.optString("nombre"),
                    precio,
                    promocion,
                    descuento,
                    detalles));
        }
        generarEmpresasCarritos();

        gs.setDatosEmpresaCarrito(listaEmpresaCarrito);
        gs.setDatosCarrito(listaCarritos);
        gs.setActualizaCarrito(false);

        progressBar.setVisibility(View.GONE);
        tvMensaje.setVisibility(View.GONE);
        layout_carrito.setVisibility(View.VISIBLE);
    }

    private void consultaActualizar(){
        auxHolder.tvDetalles.setText(auxDetalles);

        for(int i=0;i<listaCarritos.size();i++){
            if(listaCarritos.get(i).getIdCarrito() == idCarrito){
                listaCarritos.get(i).setDetalles(auxDetalles);
                break;
            }
        }

        gs.setDatosCarrito(listaCarritos);

        adaptadorListaCarrito.carrito = listaCarritos;
    }

    private void consultaEliminar(){
        progressEliminar.cancel();

        for(int i=0;i<listaCarritos.size();i++){
            if(listaCarritos.get(i).getIdCarrito() == idCarrito){
                listaCarritos.remove(i);
                break;
            }
        }

        gs.setDatosCarrito(listaCarritos);
        contEliminarCarritos--;

        if(contEliminarCarritos == 0){
            progressEliminar.cancel();
            if(listaCarritos.size() > 0){
                verificarEmpresasCarritos();
            }
            else{
                listaEmpresaCarrito = new ArrayList<>();
                gs.setDatosEmpresaCarrito(listaEmpresaCarrito);

                progressBar.setVisibility(View.GONE);
                layout_carrito.setVisibility(View.GONE);
                tvMensaje.setVisibility(View.VISIBLE);
            }
        }
        else{
            idCarrito = productos.get((contEliminarCarritos)-1).getIdCarrito();
            eliminarCarrito();
        }
    }

    private void consultaDireccion(JSONObject jsonObject, JSONArray datos) throws JSONException {
        listaDireccion = new ArrayList<>();
        for(int i=0; i<datos.length();i++) {
            jsonObject = datos.getJSONObject(i);

            int id = jsonObject.optInt("id");
            String titulo = jsonObject.optString("titulo");
            String direccion = jsonObject.optString("direccion");
            String ubicacion = jsonObject.optString("ubicacion");
            int predeterminada = jsonObject.optInt("predeterminada");

            listaDireccion.add(new ListaDireccion(id, titulo, direccion, ubicacion, predeterminada));
        }

        gs.setDatosDireccion(listaDireccion);
        gs.setActualizaDirecciones(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onErrorResponse(VolleyError error) {
        progressEliminar.cancel();
        Toast.makeText(getContext(), "Error "+ error.toString(), Toast.LENGTH_SHORT).show();
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

    public class AdaptadorListaEmpresasCarrito extends RecyclerView.Adapter<Carrito.AdaptadorListaEmpresasCarrito.MyViewHolder> {
        Context context;
        ArrayList<ListaEmpresaCarrito> empresa;
        ArrayList<ListaCarrito> carritos;

        public AdaptadorListaEmpresasCarrito(Context context, ArrayList<ListaEmpresaCarrito> empresa) {
            this.context = context;
            this.empresa = empresa;
        }

        public void actualizar(ArrayList<ListaEmpresaCarrito> lista){
            empresa = new ArrayList<>();
            empresa.addAll(lista);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Carrito.AdaptadorListaEmpresasCarrito.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            final View v;
            v = LayoutInflater.from(context).inflate(R.layout.item_empresa_carrito,viewGroup,false);
            final Carrito.AdaptadorListaEmpresasCarrito.MyViewHolder holder = new Carrito.AdaptadorListaEmpresasCarrito.MyViewHolder(v);

            holder.tvEmpresa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = empresa.get(holder.getAdapterPosition()).getId();

                    InformacionEmpresa fragment = new InformacionEmpresa();
                    gs.setFragment(fragment);
                    gs.setFragmentActual("InformacionEmpresa");
                    verInformacion(id,0, fragment, v);
                }
            });

            holder.btnPedido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    empresaPedido = empresa.get(holder.getAdapterPosition()).getId();
                    costoPedido = empresa.get(holder.getAdapterPosition()).getTotal();

                    if(!gs.isActualizaDirecciones()){
                        if(gs.getDatosDireccion().size() > 0){
                            listaDireccion = gs.getDatosDireccion();

                            dialogPedido();
                        }
                    }
                    else{
                        listarDirecciones();
                    }
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull Carrito.AdaptadorListaEmpresasCarrito.MyViewHolder myViewHolder, int i)  {
            int id = empresa.get(i).getId();
            myViewHolder.tvEmpresa.setText(empresa.get(i).getEmpresa());
            myViewHolder.tvTotalPedido.setText("$" + empresa.get(i).getTotal());

            carritos = new ArrayList<>();

            for(int j=0;j<listaCarritos.size();j++){
                if(listaCarritos.get(j).getIdEmpresa() == id) {
                        carritos.add(listaCarritos.get(j));
                }
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            adaptadorListaCarrito = new AdaptadorListaCarrito(context, carritos);
            myViewHolder.rvCarritos.setLayoutManager(layoutManager);
            myViewHolder.rvCarritos.setAdapter(adaptadorListaCarrito);
        }

        public int getItemCount() {
            return empresa.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder  {

            private TextView tvEmpresa;
            private RecyclerView rvCarritos;
            private Button btnPedido;
            private TextView tvTotalPedido;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                tvEmpresa = itemView.findViewById(R.id.tvEmpresa);
                rvCarritos = itemView.findViewById(R.id.rvEmpresas);
                btnPedido = itemView.findViewById(R.id.btnPedido);
                tvTotalPedido = itemView.findViewById(R.id.tvTotalPedido);
            }
        }
    }


    public class AdaptadorListaCarrito extends RecyclerView.Adapter<AdaptadorListaCarrito.MyViewHolder>{

        Context context;
        ArrayList<ListaCarrito> carrito;

        public AdaptadorListaCarrito(Context context, ArrayList<ListaCarrito> carrito) {
            this.context = context;
            this.carrito = carrito;
        }

        public void actualizar(ArrayList<ListaCarrito> lista){
            carrito = new ArrayList<>();
            carrito.addAll(lista);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AdaptadorListaCarrito.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            final View v;
            v = LayoutInflater.from(context).inflate(R.layout.item_carrito,viewGroup,false);
            final AdaptadorListaCarrito.MyViewHolder holder = new AdaptadorListaCarrito.MyViewHolder(v);

            holder.etCant.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(holder.etCant.getText().toString().compareTo("") != 0){
                        calcularPrecio(holder);
                    }
                    else{
                        if(holder.etCant.getText().toString().compareTo("0") == 0){
                            holder.etCant.setText("1");
                            calcularPrecio(holder);
                        }
                    }

                    //actualizar(carrito);

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            holder.etCant.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(holder.etCant.getText().toString().compareTo("") == 0
                            || holder.etCant.getText().toString().compareTo("0") == 0 ){
                        holder.etCant.setText("1");
                        calcularPrecio(holder);
                    }
                }
            });

            holder.tvNombre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int idEmpresa = carrito.get(holder.getAdapterPosition()).getIdEmpresa();
                    int idProducto = carrito.get(holder.getAdapterPosition()).getIdProducto();

                    InformacionProducto fragment = new InformacionProducto();
                    gs.setFragment(fragment);
                    gs.setFragmentActual("InformacionProducto");
                    verInformacion(idEmpresa, idProducto, fragment, v);
                }
            });

            holder.btnDetalles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_detalles(carrito.get(holder.getAdapterPosition()).getDetalles(), holder);
                }
            });

            holder.btnBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_borrar(holder);
                }
            });
            return holder;
        }

        private void calcularPrecio(AdaptadorListaCarrito.MyViewHolder holder){
            int precio = 0;
            int cant = Integer.parseInt(holder.etCant.getText().toString());
            if(carrito.get(holder.getAdapterPosition()).getPromocion() == 0){
                precio = cant * carrito.get(holder.getAdapterPosition()).getPrecio();
                carrito.get(holder.getAdapterPosition()).setCantPrecio(precio);

            }
            else{
                precio = cant * carrito.get(holder.getAdapterPosition()).getPromocion();
                carrito.get(holder.getAdapterPosition()).setCantPromocion(precio);
            }
            holder.tvPrecio.setText("$"+precio);
        }

        private void dialog_detalles(final String detalles, final AdaptadorListaCarrito.MyViewHolder holder){
            android.app.AlertDialog.Builder dialogo1 = new android.app.AlertDialog.Builder(context);

            dialogo1.setTitle("Detalles de "+ holder.tvNombre.getText().toString());
            dialogo1.setMessage("Describa los detalles de este producto para su pedido");

            final EditText etDetalles = new EditText(dialogo1.getContext());

            dialogo1.setView(etDetalles);

            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton(R.string.text_confirmar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    idCarrito = carrito.get(holder.getAdapterPosition()).getIdCarrito();
                    auxDetalles = etDetalles.getText().toString().trim();
                    auxHolder = holder;
                    actualizarDetalles(auxDetalles);
                }
            });
            dialogo1.setNegativeButton(R.string.text_cancelar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                }
            });
            dialogo1.show();
        }

        private void dialog_borrar(final AdaptadorListaCarrito.MyViewHolder holder){
            android.app.AlertDialog.Builder dialogo1 = new android.app.AlertDialog.Builder(context);
            dialogo1.setTitle("");
            dialogo1.setMessage(R.string.eliminar_carrito);
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton(R.string.text_confirmar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    idCarrito = carrito.get(holder.getAdapterPosition()).getIdCarrito();
                    progressEliminar.show();
                    contEliminarCarritos = 1;
                    eliminarCarrito();
                }
            });
            dialogo1.setNegativeButton(R.string.text_cancelar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                }
            });
            dialogo1.show();
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorListaCarrito.MyViewHolder myViewHolder, int i) {

            myViewHolder.etCant.setText(String.valueOf(carrito.get(i).getCant()));
            myViewHolder.tvNombre.setText(carrito.get(i).getNombre());

            int precio = carrito.get(i).getPrecio();
            myViewHolder.tvPrecio.setText("$"+ precio);
            if(carrito.get(i).getPromocion() != 0){
                myViewHolder.tvPrecio.setPaintFlags( myViewHolder.tvPrecio.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                myViewHolder.tvPromocion.setText("$"+carrito.get(i).getPromocion());
                myViewHolder.tvPromocion.setVisibility(View.VISIBLE);
            }

            myViewHolder.tvDetalles.setText(carrito.get(i).getDetalles());
        }

        @Override
        public int getItemCount() {
            return carrito.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            private EditText etCant;
            private TextView tvNombre;
            private TextView tvPrecio;
            private TextView tvPromocion;
            private TextView tvDetalles;
            private ImageButton btnDetalles;
            private ImageButton btnBorrar;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                etCant = itemView.findViewById(R.id.etCant);
                tvNombre = itemView.findViewById(R.id.etNombre);
                tvPrecio = itemView.findViewById(R.id.tvPrecio);
                tvPromocion = itemView.findViewById(R.id.tvPromocion);
                tvDetalles = itemView.findViewById(R.id.tvDescProducto);
                btnDetalles = itemView.findViewById(R.id.btnDetalles);
                btnBorrar = itemView.findViewById(R.id.btnBorrar);
            }
        }
    }


    public class AdaptadorListaDireccion extends RecyclerView.Adapter<Carrito.AdaptadorListaDireccion.MyViewHolder> {
        Context context;
        ArrayList<ListaDireccion> direccion;
        public int mSelectedItem = -1;

        public AdaptadorListaDireccion(Context context, ArrayList<ListaDireccion> direccion) {
            this.context = context;
            this.direccion = direccion;
        }


        @NonNull
        @Override
        public Carrito.AdaptadorListaDireccion.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v;
            v = LayoutInflater.from(context).inflate(R.layout.item_direccion_pedido,viewGroup,false);
            final Carrito.AdaptadorListaDireccion.MyViewHolder holder = new Carrito.AdaptadorListaDireccion.MyViewHolder(v);


            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull Carrito.AdaptadorListaDireccion.MyViewHolder myViewHolder, int i)  {
            myViewHolder.tvTitulo.setText(direccion.get(i).getTitulo());
            myViewHolder.tvDireccion.setText(direccion.get(i).getDireccion());
            String u = direccion.get(i).getUbicacion();

            myViewHolder.rbDireccion.setChecked(i == mSelectedItem);

            int predet = direccion.get(i).getPredeterminada();
            if(predet == 1 && mSelectedItem == -1){
                myViewHolder.rbDireccion.setChecked(true);
                direccionPedido = direccion.get(i).getDireccion();
                ubicacionPedido = direccion.get(i).getUbicacion();
            }
            if(mSelectedItem == i){
                direccionPedido = direccion.get(i).getDireccion();
                ubicacionPedido = direccion.get(i).getUbicacion();
            }
        }


        public int getItemCount() {
            return direccion.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder  {

            private ConstraintLayout item_direccion_pedido;
            private RadioButton rbDireccion;
            private TextView tvTitulo;
            private TextView tvDireccion;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                item_direccion_pedido = itemView.findViewById(R.id.item_direccion_pedido);
                rbDireccion = itemView.findViewById(R.id.rbDireccion);
                tvTitulo = itemView.findViewById(R.id.tvTitulo);
                tvDireccion = itemView.findViewById(R.id.tvDireccion);

                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedItem = getAdapterPosition();
                        notifyDataSetChanged();
                    }
                };
                itemView.setOnClickListener(clickListener);
                rbDireccion.setOnClickListener(clickListener);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(gs.isActualizaCarrito()){
            listarCarrito();
        }
    }
}
