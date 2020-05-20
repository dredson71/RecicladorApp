package com.upc.reciclemosreciclador.Inicio;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Entities.Validar;
import com.upc.reciclemosreciclador.R;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ValidarFragment extends Fragment {
    ArrayList<Validar> listValidar = new ArrayList<>();
    RecyclerView rclValidar;
    AdapterValidacion adapter;
    Retrofit retrofit;
    Button btnContinuar;
    int bolsa;

    public ValidarFragment(int bolsa) {
        this.bolsa = bolsa;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_validar, container, false);

        rclValidar = v.findViewById(R.id.rclDetalle);
        btnContinuar = v.findViewById(R.id.btnContinua);

        rclValidar.setLayoutManager(new LinearLayoutManager(getActivity()));

        retrofit = new Retrofit.Builder()
                .baseUrl("https://recyclerapiresttdp.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor fila = db.rawQuery("select producto, cantidad from Probolsa where bolsa = " + bolsa, null);

        fila.moveToFirst();

        do{
            Cursor f = db.rawQuery("select nombre, tipo_contenido, categoria, urlimagen, contenido from Producto where codigo = " + fila.getInt(0), null);

            f.moveToFirst();

            Cursor f2 = db.rawQuery("select nombre from Categoria where codigo = " + f.getInt(2), null);

            f2.moveToFirst();

            Validar ayuda = new Validar();
            ayuda.setCantidad(fila.getInt(1));
            ayuda.setNombre(f.getString(0));
            ayuda.setAbreviatura(f.getString(1));
            ayuda.setCategoria(f2.getString(0));
            ayuda.setContenido(f.getDouble(4));
            ayuda.setUtlimagen(f.getString(3));
            ayuda.setValidado(false);

            listValidar.add(ayuda);
        }while(fila.moveToNext());

        adapter = new AdapterValidacion(listValidar, getActivity());
        rclValidar.setAdapter(adapter);

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ValidacionActivity) getActivity()).observacion(bolsa, validando());
            }
        });

        db.close();
        return v;
    }

    public Boolean validando(){
        int s = listValidar.size();
        int c = 0;
        for(Validar v: listValidar){
            if(v.getValidado())
                c++;
        }
        if(c == s)
            return true;
        else
            return false;
    }
}
