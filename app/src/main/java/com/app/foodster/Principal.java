package com.app.foodster;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.foodster.Persona.Carrito;
import com.app.foodster.Persona.Cuenta;
import com.app.foodster.Empresa.Empresas;

public class Principal extends AppCompatActivity {

    Fragment fragment = null;

    GlobalState gs;

    BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        gs = (GlobalState) getApplication();

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(navListener);

        if(savedInstanceState == null){
            if(gs.getFragmentActual() == null){
                fragment = new Empresas();
                gs.setFragmentEmpresas(fragment);
                gs.setFragmentActualEmpresas("Empresas");
                addFragment();
            }
        }

        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Empresas()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            switch (item.getItemId()){
                case R.id.menu_empresas:
                    gs.setFragmentActual("Empresas");
                    break;

                case R.id.menu_eventos:
                    gs.setFragmentActual("Eventos");
                    break;

                case R.id.menu_mapa:
                    gs.setFragmentActual("Mapa");
                    break;

                case R.id.menu_carrito:
                    gs.setFragmentActual("Carrito");
                    break;

                case R.id.menu_cuenta:
                    gs.setFragmentActual("Cuenta");
                    break;
            }

            verificarFragmentMenu();
            reemplazarFragment();
            return true;
        }
    };

    private void verificarFragmentMenu(){
        switch(gs.getFragmentActual()){
            case "Empresas": fragment = new Empresas();
                break;

            case "Mapa": fragment = new Mapa();
                break;

            case "Carrito": fragment = new Carrito();
                break;

            case "Cuenta": fragment = new Cuenta();
                break;
        }
    }

    private void verificarFragmentBack(){


    }

    private void addFragment(){
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment, fragment.getClass().toString()).addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void reemplazarFragment(){

        FragmentManager fm = getSupportFragmentManager();
        Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);
        if(!fragment.getClass().toString().equals(currentFragment.getTag()))
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, fragment, fragment.getClass().toString()) // add and tag the new fragment
                    .commit();
        }
    }

    private void dialogSalir(){
        AlertDialog.Builder buider = new AlertDialog.Builder(Principal.this);
        View dView = getLayoutInflater().inflate(R.layout.dialog_salir, null);
        Button btnSalir = (Button)dView.findViewById(R.id.btnSalir);

        btnSalir.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                limpiarDatos();
                finish();
            }
        });
        buider.setView(dView);
        AlertDialog dialog = buider.create();
        dialog.show();
    }

    private void limpiarDatos(){
        gs.setUsuario("");
        gs.setPassword("");
    }

    @Override
    public void onBackPressed() {

        fragment = gs.getFragment();
        if(fragment instanceof Empresas || fragment instanceof Mapa || fragment instanceof Carrito || fragment instanceof Cuenta){
            Toast.makeText(this, "Salir", Toast.LENGTH_SHORT).show();
        }
        else{
            /*verificarFragmentBack();
            //reemplazarFragment();*/

            int count = getSupportFragmentManager().getBackStackEntryCount();
            Toast.makeText(this, "Count: "+count, Toast.LENGTH_SHORT).show();
            if (count == 0) {
                super.onBackPressed();

            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

}
