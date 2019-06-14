package com.app.foodster.Persona;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.foodster.Empresa.SugerirEmpresa;
import com.app.foodster.GlobalState;
import com.app.foodster.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Cuenta extends Fragment {

    GlobalState gs;

    Fragment fragment = null;

    Button btnPerfil;
    Button btnDirecciones;
    Button btnTarjetas;
    Button btnFavoritos;
    Button btnPedidos;
    Button btnSugerir;
    Button btnComentario;
    Button btnAjustes;
    Button btnSession;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cuenta, container, false);

        btnPerfil = v.findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new Perfil();
                reemplazarFragment();
            }
        });

        btnDirecciones = v.findViewById(R.id.btnDirecciones);
        btnDirecciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new MisDirecciones();
                reemplazarFragment();
            }
        });

        btnTarjetas = v.findViewById(R.id.btnTarjetas);
        btnTarjetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new MisTarjetas();
                reemplazarFragment();
            }
        });

        btnFavoritos = v.findViewById(R.id.btnFavoritos);
        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new ProductosFavoritos();
                reemplazarFragment();
            }
        });

        btnPedidos = v.findViewById(R.id.btnPedidos);
        btnPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new HistoricoPedidos();
                reemplazarFragment();
            }
        });

        btnSugerir = v.findViewById(R.id.btnSugerir);
        btnSugerir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SugerirEmpresa.class);
                startActivity(intent);
            }
        });

        btnComentario = v.findViewById(R.id.btnComentario);
        btnComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Sugerencias.class);
                startActivity(intent);
            }
        });

        btnAjustes = v.findViewById(R.id.btnAjustes);
        btnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnSession = v.findViewById(R.id.btnSession);
        btnSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gs.setFragmentActual(null);
                SharedPreferences preferencesCuenta = getActivity().getSharedPreferences("cuenta", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorCuenta = preferencesCuenta.edit();
                editorCuenta.putString("session", "");
                editorCuenta.apply();

                getActivity().finish();

            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gs = (GlobalState) getActivity().getApplication();
        gs.setFragment(this);
    }

    private void reemplazarFragment(){

        gs.setFragment(fragment);

        getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, fragment, fragment.getClass().toString()).addToBackStack(null) // add and tag the new fragment
                    .commit();
    }
}
