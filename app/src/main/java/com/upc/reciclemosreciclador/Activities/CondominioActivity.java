package com.upc.reciclemosreciclador.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.upc.reciclemosreciclador.Adapters.ListaCondominioAdapter;
import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Entities.Condominio;
import com.upc.reciclemosreciclador.Entities.Distrito;
import com.upc.reciclemosreciclador.Fragments.CondominioListaFragment;
import com.upc.reciclemosreciclador.Inicio.BolsaActivity;
import com.upc.reciclemosreciclador.Inicio.ValQrFragment;
import com.upc.reciclemosreciclador.Inicio.ValidacionActivity;
import com.upc.reciclemosreciclador.R;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class CondominioActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    Fragment condominioListaFragment = CondominioListaFragment.newInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condominio);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, condominioListaFragment);
        transaction.commit();


        //INICIALIZA APP BAR
        BottomNavigationView bottomNavigationView = findViewById(R.id.botton_navigation);

        //SELECCIÓN
        bottomNavigationView.setSelectedItemId(R.id.condominio);

        //CAMBIO DE SELECCIÓN
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.miaporte:
                        startActivity(new Intent(getApplicationContext(), ValidarActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.condominio:
                        startActivity(new Intent(getApplicationContext(), CondominioActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.escaner:
                        startActivity(new Intent(getApplicationContext(), BolsaActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.mibolsa:
                        startActivity(new Intent(getApplicationContext(), ValidacionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.catalogo:
                        return true;
                }
                return false;
            }
        });
    }



}
