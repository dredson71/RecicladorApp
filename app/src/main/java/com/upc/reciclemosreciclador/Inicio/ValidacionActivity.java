package com.upc.reciclemosreciclador.Inicio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.upc.reciclemosreciclador.Activities.CondominioActivity;
import com.upc.reciclemosreciclador.Activities.ValidarActivity;
import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Entities.Bolsa;
import com.upc.reciclemosreciclador.Entities.JsonPlaceHolderApi;
import com.upc.reciclemosreciclador.Entities.Validar;
import com.upc.reciclemosreciclador.R;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class ValidacionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private APIToSQLite sqlitedb;
    FragmentManager fragmentManager;
    FragmentTransaction transaction, transaction2;
    Fragment valQrFragment = ValQrFragment.newInstance();
    Fragment detallesFragment, validarFragment, observacionFragment, finalizadoFragment;
    private DrawerLayout mDrawerLayout;
    int bolsa;

    void detallito(int bolsa){
        detalles(bolsa);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validacion);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        dbHelper helper = new dbHelper(this, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor filaUsuario = db.rawQuery("select codigo, nombre from Reciclador", null);

        filaUsuario.moveToFirst();

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.txtUser);
        navUsername.setText(filaUsuario.getString(1));
        TextView navViewProfile = headerView.findViewById(R.id.txtMiPerfil);

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, valQrFragment);
        transaction.commit();

        //INICIALIZA APP BAR
        BottomNavigationView bottomNavigationView = findViewById(R.id.botton_navigation);

        //SELECCIÓN
        bottomNavigationView.setSelectedItemId(R.id.mibolsa);

        //CAMBIO DE SELECCIÓN
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.miaporte:
                        ((ValQrFragment) valQrFragment).pauseScan();
                        startActivity(new Intent(getApplicationContext(), ValidarActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.escaner:
                        ((ValQrFragment) valQrFragment).pauseScan();
                        startActivity(new Intent(getApplicationContext(), BolsaActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.mibolsa:
                        ((ValQrFragment) valQrFragment).pauseScan();
                        startActivity(new Intent(getApplicationContext(), ValidacionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.catalogo:
                        /*((QrFragment) qrFragment).pauseScan();
                        startActivity(new Intent(getApplicationContext(), CatalogoActivity.class));
                        overridePendingTransition(0,0);*/
                        return true;
                }
                return false;
            }
        });
        Bundle b = getIntent().getExtras();
        if(b != null) {
            bolsa = b.getInt("bolsa");
            detalles(bolsa);
        }

    }
/*
    public void creatingUpdate()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando información...");
        progressDialog.show();
        progressDialog.setCancelable(false);


        sqlitedb = new APIToSQLite(this,"actualizar");

        new BackgroundJob().execute();
    }

    private class BackgroundJob extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                sqlitedb.InsertBolsas();
                sqlitedb.InsertProbolsa();
                sqlitedb.insertBolsasByMonthOrWeek("bolsasWeek/");
                sqlitedb.obtenerBolsasByYear("bolsasYear/");
                sqlitedb.obtenerBolsasByDay();
                sqlitedb.obtenerUltimasBolsas();
               // sqlitedb.obtenerDatosProductByBolsa();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid){
            Message msg = new Message();
            msg.what = 2;
            mHandler.sendMessage(msg);
        }
    }
    */

    public void detalles(int bolsa){
        detallesFragment = new DetallesFragment(bolsa);
        fragmentManager = getSupportFragmentManager();
        transaction2 = fragmentManager.beginTransaction();
        transaction2.replace(R.id.fragment, detallesFragment);
        transaction2.commit();
    }

    public void validar(int bolsa){
        validarFragment = new ValidarFragment(bolsa);
        fragmentManager = getSupportFragmentManager();
        transaction2 = fragmentManager.beginTransaction();
        transaction2.replace(R.id.fragment, validarFragment);
        transaction2.commit();
    }

    public void observacion(int bolsa, Boolean validado){
        observacionFragment = new ObservacionFragment(bolsa, validado);
        fragmentManager = getSupportFragmentManager();
        transaction2 = fragmentManager.beginTransaction();
        transaction2.replace(R.id.fragment, observacionFragment);
        transaction2.commit();
    }

    public void finalizado() {
        finalizadoFragment = new FinalizadoFragment();
        fragmentManager = getSupportFragmentManager();
        transaction2 = fragmentManager.beginTransaction();
        transaction2.replace(R.id.fragment, finalizadoFragment);
        transaction2.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_condominio:
                startActivity(new Intent(getApplicationContext(), CondominioActivity.class));
                break;
            case R.id.nav_info:
                Toast.makeText(this, "Proximamente...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_message:
                Toast.makeText(this, "Proximamente...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Proximamente...", Toast.LENGTH_SHORT).show();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
