package com.app.foodster;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.app.foodster.Empresa.fragInformacionEmpresa;
import com.app.foodster.Persona.Carrito;
import com.app.foodster.Persona.Cuenta;
import com.app.foodster.Empresa.Empresas;
import com.app.foodster.Persona.HistoricoPedidos;
import com.app.foodster.Persona.Pedido;
import com.app.foodster.Persona.Perfil;
import com.app.foodster.Persona.ProductosFavoritos;
import com.app.foodster.Producto.fragInformacionProducto;

public class Principal extends AppCompatActivity {

    Fragment fragment = null;

    GlobalState gs;
    boolean verificarSeleccion;

    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        gs = (GlobalState) getApplication();
        gs.setPrincipal(this);

        navigation = findViewById(R.id.navigation);

        if(savedInstanceState == null){
            if(gs.getFragmentActual() == null){
                if(gs.isExistePedidos()){
                    fragment = new Pedido();
                }
                else{
                    fragment = new Empresas();
                }
                //gs.setFragment(fragment);
            }
            else{
                fragment = new Pedido();
            }
            addFragment();
            verificarSeleccion = true;
        }

        navigation.setOnNavigationItemSelectedListener(navListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if(verificarSeleccion){
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
            }
            verificarSeleccion = true;
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

    private void seleccionarIcono(){
        verificarSeleccion = false;
        if(fragment instanceof Empresas
                || fragment instanceof fragInformacionEmpresa
                || fragment instanceof fragInformacionProducto){
            navigation.setSelectedItemId(R.id.menu_empresas);
        }
        if(fragment instanceof Mapa){
            navigation.setSelectedItemId(R.id.menu_mapa);
        }
        if(fragment instanceof Carrito || fragment instanceof Pedido){
            navigation.setSelectedItemId(R.id.menu_carrito);
        }
        if(fragment instanceof Cuenta
                || fragment instanceof Perfil
                || fragment instanceof ProductosFavoritos
                || fragment instanceof HistoricoPedidos){
            navigation.setSelectedItemId(R.id.menu_cuenta);
        }
    }

    private void addFragment(){

        seleccionarIcono();

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment, fragment.getClass().toString()).addToBackStack(null);
        fragmentTransaction.commit();
    }



    public void reemplazarFragment(){

        FragmentManager fm = getSupportFragmentManager();
        Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);
        if(!fragment.getClass().toString().equals(currentFragment.getTag()))
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, fragment, fragment.getClass().toString()).addToBackStack(null) // add and tag the new fragment
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
                finish();
            }
        });
        buider.setView(dView);
        AlertDialog dialog = buider.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {

        fragment = gs.getFragment();

        int count = getSupportFragmentManager().getBackStackEntryCount();
        Toast.makeText(this, "Count: "+count, Toast.LENGTH_SHORT).show();
        if (count == 1) {
            if(fragment instanceof Empresas){
                Toast.makeText(this, "Salir", Toast.LENGTH_SHORT).show();
            }
            else{
                if(fragment instanceof Pedido){
                    fragment = new Empresas();
                    seleccionarIcono();
                    reemplazarFragment();
                }
            }
            //
        }
        else{
            //super.onBackPressed();
            getSupportFragmentManager().popBackStack();
            FragmentManager fm = getSupportFragmentManager();
            Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);
            fragment = currentFragment;
            gs.setFragment(fragment);
            seleccionarIcono();
            //getSupportFragmentManager().popBackStack();
        }
    }
}
