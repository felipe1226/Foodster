package com.app.foodster.Persona;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
    Button btnFavoritos;
    Button btnNotificaciones;
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

        btnNotificaciones = v.findViewById(R.id.btnNotificaciones);
        btnNotificaciones.setOnClickListener(new View.OnClickListener() {
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

                getActivity().finish();
            }
        });


        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gs = (GlobalState) getActivity().getApplication();
    }

    private void reemplazarFragment(){

        gs.setFragment(fragment);
        FragmentManager fm = getActivity().getSupportFragmentManager();

        getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, fragment, fragment.getClass().toString()) // add and tag the new fragment
                    .commit();

    }
}
