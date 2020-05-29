package com.upc.reciclemosreciclador.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.upc.reciclemosreciclador.Adapters.ViewValidarBolsasAdapter;
import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Fragments.CondominioValidarFragment;
import com.upc.reciclemosreciclador.Fragments.TotalBolsasValidarFragment;
import com.upc.reciclemosreciclador.Inicio.BolsaActivity;
import com.upc.reciclemosreciclador.Inicio.QrFragment;
import com.upc.reciclemosreciclador.Inicio.ValidacionActivity;
import com.upc.reciclemosreciclador.R;
import com.google.android.material.tabs.TabLayout;

public class ValidarActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ViewPager viewPager;
    private ViewValidarBolsasAdapter adapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar);

        //Menu Desplegable
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();


        viewPager = findViewById(R.id.viewPagerTendencia);
        tabLayout = findViewById(R.id.tabLayoutTendencia);
        adapter = new ViewValidarBolsasAdapter(getSupportFragmentManager());
        adapter.AddFragment(new CondominioValidarFragment(),"Por Condominio");
        adapter.AddFragment(new TotalBolsasValidarFragment(),"General");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        dbHelper helper = new dbHelper(this,"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor fila2 = db.rawQuery("select codigo, nombre from Reciclador", null);

        fila2.moveToFirst();
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.txtUser);
        navUsername.setText(fila2.getString(1));
        TextView navViewProfile = headerView.findViewById(R.id.txtMiPerfil);
        navViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfileActity.class));
            }
        });



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


    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_condominio:
                startActivity(new Intent(getApplicationContext(), CondominioActivity.class));
                break;
            case R.id.nav_info:
                Toast.makeText(this, "Proximamente 11", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_message:
                Toast.makeText(this, "Proximamente 2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                break;

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


}
