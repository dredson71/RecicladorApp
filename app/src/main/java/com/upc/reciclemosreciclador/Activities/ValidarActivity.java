package com.upc.reciclemosreciclador.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.upc.reciclemosreciclador.Adapters.ViewValidarBolsasAdapter;
import com.upc.reciclemosreciclador.Fragments.CondominioValidarFragment;
import com.upc.reciclemosreciclador.Fragments.TotalBolsasValidarFragment;
import com.upc.reciclemosreciclador.Inicio.BolsaActivity;
import com.upc.reciclemosreciclador.Inicio.QrFragment;
import com.upc.reciclemosreciclador.Inicio.ValidacionActivity;
import com.upc.reciclemosreciclador.R;
import com.google.android.material.tabs.TabLayout;

public class ValidarActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewValidarBolsasAdapter adapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar);

        viewPager = findViewById(R.id.viewPagerTendencia);
        tabLayout = findViewById(R.id.tabLayoutTendencia);
        adapter = new ViewValidarBolsasAdapter(getSupportFragmentManager());
        adapter.AddFragment(new CondominioValidarFragment(),"Por Condominio");
        adapter.AddFragment(new TotalBolsasValidarFragment(),"General");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);



       //INICIALIZA APP BAR
        BottomNavigationView bottomNavigationView = findViewById(R.id.botton_navigation);

        //SELECCIÓN
        bottomNavigationView.setSelectedItemId(R.id.miaporte);

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
