package com.upc.reciclemosreciclador.Inicio;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Entities.Bolsa;
import com.upc.reciclemosreciclador.Entities.Condominio;
import com.upc.reciclemosreciclador.Entities.ContenedorTemp;
import com.upc.reciclemosreciclador.Entities.JsonPlaceHolderApi;
import com.upc.reciclemosreciclador.Entities.Probolsa;
import com.upc.reciclemosreciclador.Entities.Producto;
import com.upc.reciclemosreciclador.Entities.Reciclador;
import com.upc.reciclemosreciclador.Entities.Usuario;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIToSQLite {

    dbHelper helper;
    SQLiteDatabase db;
    Retrofit retrofit;
    Context context;
    private int [] xDataMonthOrWeek= {0,0,0,0,0,0,0};
    private int [] xDataYear= {0,0,0,0,0,0,0,0,0,0,0,0};
    private int [] xDataMonthOrWeekBolsas= {0,0,0,0,0,0,0};
    private int [] xDataYearBolsas= {0,0,0,0,0,0,0,0,0,0,0,0};
    private int [] xDataMonthOrWeekPuntos= {0,0,0,0,0,0,0};
    private int [] xDataYearPuntos= {0,0,0,0,0,0,0,0,0,0,0,0};
    private double countPlastico, pesoPlastico, puntosPlastico;
    private double countPapelCarton, pesoPapelCarton, puntosPapelCarton;
    private double countMetal, pesoMetal, puntosMetal;
    private double countVidrio, pesoVidrio, puntosVidrio;


    public APIToSQLite(Context context, String tipo){
        helper = new dbHelper(context,"Usuario.sqlite", null, 1);
        db = helper.getWritableDatabase();
        if(!tipo.equals("actualizar")) {
            helper.DropCreate(db);
        }
        else{
            helper.UpdateTable(db);
        }
        this.context = context;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://recyclerapiresttdp.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void InsertReciclador(String email, String password) throws IOException {
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<Reciclador> call=jsonPlaceHolderApi.getRecicladorByEmailPassword("/reciclador/correo/" + email + "/" + password);
        Response<Reciclador> response = call.execute();
        String[] ayuda = new String[12];
        ayuda[0] = response.body().getNombre();
        ayuda[1] = response.body().getApellido();
        ayuda[2] = response.body().getDireccion();
        ayuda[3] = response.body().getDni();
        ayuda[4] = response.body().getEmail();
        ayuda[5] = response.body().getFecha_Nacimiento();
        ayuda[6] = response.body().getCelular();
        ayuda[7] = response.body().getCodigo().toString();
        ayuda[10] = response.body().getAsociacion().getNombre();
        ayuda[11] = response.body().getCodFormalizado();
        String query = "insert into Reciclador (nombre, apellido, direccion, dni, email, fecha_Nacimiento, celular, codigo, asociacion_name, codFormalizado) " +
                "values ('" + ayuda[0] + "', '" + ayuda[1] + "', '" + ayuda[2] + "', '" + ayuda[3] + "', '" + ayuda[4] + "', '" + ayuda[5] + "', '"
                + ayuda[6] + "', '" + ayuda[7] + "', '" + ayuda[8] + "', '" + ayuda[9] + "')";
        db.execSQL(query);
        System.out.println(query);


        Log.e("TAG","onResponse:" + response.toString());
        /*call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()) {
                    String[] ayuda = new String[10];
                    ayuda[0] = response.body().getNombre();
                    ayuda[1] = response.body().getApellido();
                    ayuda[2] = response.body().getCondominio().getNombre();
                    ayuda[3] = response.body().getDireccion();
                    ayuda[4] = response.body().getDni();
                    ayuda[5] = response.body().getEmail();
                    ayuda[6] = response.body().getFechanacimiento();
                    ayuda[7] = response.body().getSexo().getNombre();
                    ayuda[8] = response.body().getTelefono();
                    ayuda[9] = response.body().getCodigo().toString();
                    String query = "insert into Usuario (nombre, apellido, condominio, direccion, dni, email, fecha_Nacimiento, sexo, telefono, codigo) " +
                            "values ('" + ayuda[0] + "', '" + ayuda[1] + "', '" + ayuda[2] + "', '" + ayuda[3] + "', '" + ayuda[4] + "', '" + ayuda[5] + "', '"
                            + ayuda[6] + "', '" + ayuda[7] + "', '" + ayuda[8] + "', " + ayuda[9] + ")";
                    db.execSQL(query);
                    System.out.println(query);
                    Log.e("TAG","onResponse:" + response.toString());
                }else{
                    Log.e("TAG","onResponse:" + response.toString());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("TAG","onFailure:" + t.getMessage());
            }
        });*/
    }



    public void getCondominiosByReciclador()throws IOException, InterruptedException{
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor fila = db.rawQuery("select codigo from Reciclador", null);
        fila.moveToFirst();
        String codigo = fila.getString(fila.getColumnIndex("codigo"));
        JsonPlaceHolderApi jsonPlaceHolderApi =retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Condominio>> call=jsonPlaceHolderApi.getCondominiosByReciclador("condominio/reciclador/"+codigo);
        Response<List<Condominio>> response = call.execute();
        for(Condominio p: response.body()) {
            String query = "insert into Condominio (codigo,direccion , distrito , nombre , urbanizacion , nombrecontacto , numerocontacto , reciclador ) " +
                    "values ("+p.getCodigo()+",'" + p.getDireccion() + "', '" + p.getDistrito().getNombre() + "', '" + p.getNombre() + "', '" + p.getUrbanizacion() + "', '" + p.getNombreContacto()
                    + "', '" + p.getNumeroContacto() + "', " + p.getReciclador().getCodigo() + ")";
            db.execSQL(query);
            System.out.println(query);
        }

    }

    public void getResiduosByWeekOrMonth(String urlDate) throws IOException, InterruptedException{

        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor fila = db.rawQuery("select codigo from Reciclador", null);
        fila.moveToFirst();
        String idReciclador = fila.getString(fila.getColumnIndex("codigo"));
        Cursor condominiosDB = db.rawQuery("select codigo, nombre from Condominio",null);
        if(condominiosDB.moveToFirst()) {
            do {
                initialCounter();
                int valor = 0;
                int codigo = condominiosDB.getInt(0);
                String nombre = condominiosDB.getString(1);
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                Call<List<Probolsa>> call = jsonPlaceHolderApi.getProbolsasByDate("probolsa/reciclador/" + urlDate +Integer.toString(codigo)+"/"+idReciclador);
                Response<List<Probolsa>> response = call.execute();
                for (Probolsa bolsasbydate : response.body()) {
                    valor++;
                    if (bolsasbydate.getBolsa().getRecojoFecha() != null) {
                        if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Plastico") || bolsasbydate.getProducto().getCategoria().getNombre().equals("Papel/Carton")) {
                            if (urlDate.equals("bolsasWeek/") || urlDate.equals("bolsasMonth/")) {
                                Date dia = bolsasbydate.getBolsa().getRecojoFecha();
                                SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                                if (simpleDateformat.format(dia).equals("Monday")) {
                                    xDataMonthOrWeek[0] += bolsasbydate.getCantidad();
                                    xDataMonthOrWeekPuntos[0] += bolsasbydate.getPuntuacion();
                                } else if (simpleDateformat.format(dia).equals("Tuesday")) {
                                    xDataMonthOrWeek[1] += bolsasbydate.getCantidad();
                                    xDataMonthOrWeekPuntos[1] += bolsasbydate.getPuntuacion();
                                } else if (simpleDateformat.format(dia).equals("Wednesday")) {
                                    xDataMonthOrWeek[2] += bolsasbydate.getCantidad();
                                    xDataMonthOrWeekPuntos[2] += bolsasbydate.getPuntuacion();
                                } else if (simpleDateformat.format(dia).equals("Thursday")) {
                                    xDataMonthOrWeek[3] += bolsasbydate.getCantidad();
                                    xDataMonthOrWeekPuntos[3] += bolsasbydate.getPuntuacion();
                                } else if (simpleDateformat.format(dia).equals("Friday")) {
                                    xDataMonthOrWeek[4] += bolsasbydate.getCantidad();
                                    xDataMonthOrWeekPuntos[4] += bolsasbydate.getPuntuacion();
                                } else if (simpleDateformat.format(dia).equals("Saturday")) {
                                    xDataMonthOrWeek[5] += bolsasbydate.getCantidad();
                                    xDataMonthOrWeekPuntos[5] += bolsasbydate.getPuntuacion();
                                } else {
                                    xDataMonthOrWeek[6] += bolsasbydate.getCantidad();
                                    xDataMonthOrWeekPuntos[6] += bolsasbydate.getPuntuacion();
                                }
                                addingValues(bolsasbydate);
                            }
                        }
                    }
                }
                if (valor > 0) {
                    generateQuery(urlDate, "Plastico", codigo, countPlastico, pesoPlastico, puntosPlastico);
                    generateQuery(urlDate, "Vidrio", codigo, countVidrio, pesoVidrio, puntosVidrio);
                    generateQuery(urlDate, "Papel", codigo, countPapelCarton, pesoPapelCarton, puntosPapelCarton);
                    generateQuery(urlDate, "Metal", codigo, countMetal, pesoMetal, puntosMetal);

                    String query = "insert into DatosDiarios(tipo,frecuenciatipo,tipocondominio,lunes,martes,miercoles,jueves,viernes,sabado,domingo) " +
                            "values ('residuos','"+urlDate+"',"+codigo+", " + xDataMonthOrWeek[0] + ", " + xDataMonthOrWeek[1] + ", "
                            + xDataMonthOrWeek[2] + ", " + xDataMonthOrWeek[3] + ", " + xDataMonthOrWeek[4] + "," + xDataMonthOrWeek[5] + "," + xDataMonthOrWeek[6] + ")";
                    db.execSQL(query);
                    System.out.println(query);

                    String query2 = "insert into DatosDiarios(tipo,frecuenciatipo,tipocondominio,lunes,martes,miercoles,jueves,viernes,sabado,domingo) " +
                            "values ('puntos','"+urlDate+"',"+codigo+", " + xDataMonthOrWeekPuntos[0] + ", " + xDataMonthOrWeekPuntos[1] + ", "
                            + xDataMonthOrWeekPuntos[2] + ", " + xDataMonthOrWeekPuntos[3] + ", " + xDataMonthOrWeekPuntos[4] + "," + xDataMonthOrWeekPuntos[5] + "," + xDataMonthOrWeekPuntos[6] + ")";
                    db.execSQL(query2);
                    System.out.println(query2);

                }

                Log.e("TAG", "onResponse:" + response.toString());

                try {
                    getBolsasByWeekorMonth(urlDate,codigo,idReciclador);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (condominiosDB.moveToNext());
        }
    }

    public void getBolsasByWeekorMonth(String urlDate, int codigo,String idReciclador) throws IOException, InterruptedException, ParseException {
        initialCounter();
        int valor = 0;
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Bolsa>> call = jsonPlaceHolderApi.getBolsasByDate("bolsa/reciclador/" + urlDate + Integer.toString(codigo)+"/"+idReciclador);
        Response<List<Bolsa>> response = call.execute();
        for (Bolsa bolsasWeekMonth : response.body()) {
            valor++;
            if (bolsasWeekMonth.getRecojoFecha() != null) {
                Date dia = bolsasWeekMonth.getRecojoFecha();
                SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                if (simpleDateformat.format(dia).equals("Monday")) {
                    xDataMonthOrWeekBolsas[0] += 1;
                } else if (simpleDateformat.format(dia).equals("Tuesday"))
                    xDataMonthOrWeekBolsas[1] += 1;
                else if (simpleDateformat.format(dia).equals("Wednesday"))
                    xDataMonthOrWeekBolsas[2] += 1;
                else if (simpleDateformat.format(dia).equals("Thursday")) {
                    xDataMonthOrWeekBolsas[3] += 1;
                } else if (simpleDateformat.format(dia).equals("Friday"))
                    xDataMonthOrWeekBolsas[4] += 1;
                else if (simpleDateformat.format(dia).equals("Saturday"))
                    xDataMonthOrWeekBolsas[5] += 1;
                else
                    xDataMonthOrWeekBolsas[6] += 1;
            }

        }
        if(valor > 0) {
            String query = "insert into DatosDiarios(tipo,frecuenciatipo,tipocondominio,lunes,martes,miercoles,jueves,viernes,sabado,domingo) " +
                    "values ('bolsas','"+urlDate+"',"+codigo+", " + xDataMonthOrWeekBolsas[0] + ", " + xDataMonthOrWeekBolsas[1] + ", "
                    + xDataMonthOrWeekBolsas[2] + ", " + xDataMonthOrWeekBolsas[3] + ", " + xDataMonthOrWeekBolsas[4] + "," + xDataMonthOrWeekBolsas[5] + "," + xDataMonthOrWeekBolsas[6] + ")";
            db.execSQL(query);
            System.out.println(query);
        }
    }

    public void obtenerBolsasByYear(String urlDate )throws IOException, InterruptedException {
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor fila = db.rawQuery("select codigo from Reciclador", null);
        fila.moveToFirst();
        String idReciclador = fila.getString(fila.getColumnIndex("codigo"));
        Cursor condominiosDB = db.rawQuery("select codigo, nombre from Condominio", null);
        if (condominiosDB.moveToFirst()) {
            do {
                initialCounter();
                int valor = 0;
                int codigo = condominiosDB.getInt(0);
                String nombre = condominiosDB.getString(1);
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                Call<List<Probolsa>> call = jsonPlaceHolderApi.getProbolsasByDate("probolsa/reciclador/" + urlDate +Integer.toString(codigo)+"/"+idReciclador);
                Response<List<Probolsa>> response = call.execute();
                for (Probolsa bolsasbydate : response.body()) {
                    valor++;
                    if (bolsasbydate.getBolsa().getRecojoFecha() != null) {
                        if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Plastico") || bolsasbydate.getProducto().getCategoria().getNombre().equals("Papel/Carton")) {
                            Date dia = bolsasbydate.getBolsa().getRecojoFecha();
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(dia);
                            if (cal.get(Calendar.MONTH) == 0) {
                                xDataYear[0] += bolsasbydate.getCantidad();
                                xDataYearPuntos[0] += bolsasbydate.getPuntuacion();
                            } else if (cal.get(Calendar.MONTH) == 1) {
                                xDataYear[1] += bolsasbydate.getCantidad();
                                xDataYearPuntos[1] += bolsasbydate.getPuntuacion();
                            } else if (cal.get(Calendar.MONTH) == 2) {
                                xDataYear[2] += bolsasbydate.getCantidad();
                                xDataYearPuntos[2] += bolsasbydate.getPuntuacion();
                            } else if (cal.get(Calendar.MONTH) == 3) {
                                xDataYear[3] += bolsasbydate.getCantidad();
                                xDataYearPuntos[3] += bolsasbydate.getPuntuacion();
                            } else if (cal.get(Calendar.MONTH) == 4) {
                                xDataYear[4] += bolsasbydate.getCantidad();
                                xDataYearPuntos[4] += bolsasbydate.getPuntuacion();
                            } else if (cal.get(Calendar.MONTH) == 5) {
                                xDataYear[5] += bolsasbydate.getCantidad();
                                xDataYearPuntos[5] += bolsasbydate.getPuntuacion();
                            } else if (cal.get(Calendar.MONTH) == 6) {
                                xDataYear[6] += bolsasbydate.getCantidad();
                                xDataYearPuntos[6] += bolsasbydate.getPuntuacion();
                            } else if (cal.get(Calendar.MONTH) == 7) {
                                xDataYear[7] += bolsasbydate.getCantidad();
                                xDataYearPuntos[7] += bolsasbydate.getPuntuacion();
                            } else if (cal.get(Calendar.MONTH) == 8) {
                                xDataYear[8] += bolsasbydate.getCantidad();
                                xDataYearPuntos[8] += bolsasbydate.getPuntuacion();
                            } else if (cal.get(Calendar.MONTH) == 9) {
                                xDataYear[9] += bolsasbydate.getCantidad();
                                xDataYearPuntos[9] += bolsasbydate.getPuntuacion();
                            } else if (cal.get(Calendar.MONTH) == 10) {
                                xDataYear[10] += bolsasbydate.getCantidad();
                                xDataYearPuntos[10] += bolsasbydate.getPuntuacion();
                            } else if (cal.get(Calendar.MONTH) == 11) {
                                xDataYear[11] += bolsasbydate.getCantidad();
                                xDataYearPuntos[11] += bolsasbydate.getPuntuacion();
                            }
                            addingValues(bolsasbydate);
                        }
                    }

                }
                if (valor > 0) {
                    generateQuery("Year", "Plastico", codigo, countPlastico, pesoPlastico, puntosPlastico);
                    generateQuery("Year", "Vidrio", codigo, countVidrio, pesoVidrio, puntosVidrio);
                    generateQuery("Year", "Papel", codigo, countPapelCarton, pesoPapelCarton, puntosPapelCarton);
                    generateQuery("Year", "Metal", codigo, countMetal, pesoMetal, puntosMetal);
                    String query = "insert into DatosAnuales(tipocondominio,enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre,tipo) " +
                            "values ( "+codigo+"," + xDataYear[0] + ", " + xDataYear[1] + ", "
                            + xDataYear[2] + ", " + xDataYear[3] + ", " + xDataYear[4] + "," + xDataYear[5] + ", "
                            + xDataYear[6] + ", " + xDataYear[7] + "," + +xDataYear[8] + "," + xDataYear[9] + ", " + xDataYear[10] + "," + xDataYear[11] + ",'probolsa')";
                    db.execSQL(query);
                    System.out.println(query);

                    String query2 = "insert into DatosAnuales(tipocondominio,enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre,tipo) " +
                            "values ( "+codigo+"," + xDataYearPuntos[0] + ", " + xDataYearPuntos[1] + ", "
                            + xDataYearPuntos[2] + ", " + xDataYearPuntos[3] + ", " + xDataYearPuntos[4] + "," + xDataYearPuntos[5] + ", "
                            + xDataYearPuntos[6] + ", " + xDataYearPuntos[7] + "," + +xDataYearPuntos[8] + "," + xDataYearPuntos[9] + ", " + xDataYearPuntos[10] + "," + xDataYearPuntos[11] + ",'puntos')";
                    db.execSQL(query2);
                    System.out.println(query2);

                }

                Log.e("TAG", "onResponse:" + response.toString());
                try {
                    obtenerLasBolsasMonth(urlDate,codigo,idReciclador);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (condominiosDB.moveToNext());
        }
    }

    public void obtenerLasBolsasMonth(String urlDate, int codigo,String idReciclador) throws IOException, InterruptedException, ParseException {
        initialCounter();
        int valor = 0;
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Bolsa>> call = jsonPlaceHolderApi.getBolsasByDate("bolsa/reciclador/" + urlDate + Integer.toString(codigo)+"/"+idReciclador);
        Response<List<Bolsa>> response = call.execute();
        for (Bolsa bolsasYear : response.body()) {
            valor++;
            if (bolsasYear.getRecojoFecha() != null) {
                Date dia = bolsasYear.getRecojoFecha();
                Calendar cal = Calendar.getInstance();
                cal.setTime(dia);
                if (cal.get(Calendar.MONTH) == 0)
                    xDataYearBolsas[0] += 1;
                else if (cal.get(Calendar.MONTH) == 1)
                    xDataYearBolsas[1] += 1;
                else if (cal.get(Calendar.MONTH) == 2)
                    xDataYearBolsas[2] += 1;
                else if (cal.get(Calendar.MONTH) == 3)
                    xDataYearBolsas[3] += 1;
                else if (cal.get(Calendar.MONTH) == 4)
                    xDataYearBolsas[4] += 1;
                else if (cal.get(Calendar.MONTH) == 5)
                    xDataYearBolsas[5] += 1;
                else if (cal.get(Calendar.MONTH) == 6)
                    xDataYearBolsas[6] += 1;
                else if (cal.get(Calendar.MONTH) == 7)
                    xDataYearBolsas[7] += 1;
                else if (cal.get(Calendar.MONTH) == 8)
                    xDataYearBolsas[8] += 1;
                else if (cal.get(Calendar.MONTH) == 9)
                    xDataYearBolsas[9] += 1;
                else if (cal.get(Calendar.MONTH) == 10)
                    xDataYearBolsas[10] += 1;
                else if (cal.get(Calendar.MONTH) == 11)
                    xDataYearBolsas[11] += 1;
            }

        }
        if(valor>0) {
            String query = "insert into DatosAnuales(tipocondominio,enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre,tipo) " +
                    "values ( "+codigo+"," + xDataYearBolsas[0] + ", " + xDataYearBolsas[1] + ", "
                    + xDataYearBolsas[2] + ", " + xDataYearBolsas[3] + ", " + xDataYearBolsas[4] + "," + xDataYearBolsas[5]  + ", "
                    + xDataYearBolsas[6] + ", " + xDataYearBolsas[7] + ","+ + xDataYearBolsas[8] + ","+xDataYearBolsas[9] + ", " + xDataYearBolsas[10] +","+ xDataYearBolsas[11] +  ",'bolsa')";
            db.execSQL(query);
            System.out.println(query);

        }
    }

    public void initialCounter(){
        countPlastico=0;
        pesoPlastico=0;
        puntosPlastico=0;
        countVidrio=0;
        pesoVidrio=0;
        puntosVidrio=0;
        countPapelCarton=0;
        pesoPapelCarton=0;
        puntosPapelCarton=0;
        countMetal=0;
        pesoMetal=0;
        puntosMetal=0;
        for(int i=0;i< 11 ; i++)
        {
            xDataYear[i] = 0;
            xDataYearBolsas[i] = 0;
            xDataYearPuntos[i] = 0;
        }
        for(int i=0;i< 7 ; i++)
        {
            xDataMonthOrWeek[i] = 0;
            xDataMonthOrWeekBolsas[i] = 0;
            xDataMonthOrWeekPuntos[i] = 0;
        }
    }

    public void addingValues(Probolsa bolsasbydate ){
        if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Plastico")) {
            pesoPlastico += bolsasbydate.getProducto().getPeso();
            puntosPlastico += bolsasbydate.getPuntuacion();
            countPlastico += bolsasbydate.getCantidad();
        }else if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Vidrio")) {
            pesoVidrio += bolsasbydate.getProducto().getPeso();
            puntosVidrio += bolsasbydate.getPuntuacion();
            countVidrio+= bolsasbydate.getCantidad();
        } else if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Papel/Carton")) {
            pesoPapelCarton += bolsasbydate.getProducto().getPeso();
            puntosPapelCarton += bolsasbydate.getPuntuacion();
            countPapelCarton+=bolsasbydate.getCantidad();
        } else if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Metal")) {
            pesoMetal += bolsasbydate.getProducto().getPeso();
            puntosMetal += bolsasbydate.getPuntuacion();
            countMetal+=bolsasbydate.getCantidad();
        }
    }

    public void generateQuery(String tipo,String producto,int nombreCondominio,double cantidad,double peso,double puntuacion){
        String query = "insert into Contador (tendenciaTipo,productoTipo,nombrecondominio,cantidad,peso,puntuacion) " +
                "values ('" + tipo + "', '" + producto + "',"+nombreCondominio+", " + cantidad + ", "
                + peso + ", " + puntuacion + ")";
        db.execSQL(query);
        System.out.println(query);
    }

    public void InsertProductos() throws IOException {
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Producto>> call=jsonPlaceHolderApi.getProductos("/producto");
        Response<List<Producto>> response = call.execute();
        for (Producto p: response.body()) {
            String query = "insert into Producto (barcode , categoria , codigo , contenido , descripcion , nombre , peso , tipo_contenido , urlimagen) " +
                    "values ('" + p.getBarcode() + "', " + p.getCategoria().getCodigo() + ", " + p.getCodigo() + ", " + p.getContenido() + ", " + p.getDescripcion()
                    + ", '" + p.getNombre() + "', " + p.getPeso() + ", '" + p.getTipo_Contenido().getAbreviatura() + "', '" + p.getUrlImage() + "')";
            db.execSQL(query);
            System.out.println(query);
        }
        Log.e("TAG","onResponse:" + response.toString());
        /*call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if(response.isSuccessful()) {
                    for (Producto p: response.body()) {
                        String query = "insert into Producto (barcode , categoria , codigo , contenido , descripcion , nombre , peso , tipo_contenido , urlimagen) " +
                                "values ('" + p.getBarcode() + "', " + p.getCategoria().getCodigo() + ", " + p.getCodigo() + ", " + p.getContenido() + ", " + p.getDescripcion()
                                + ", '" + p.getNombre() + "', " + p.getPeso() + ", '" + p.getTipo_Contenido().getAbreviatura() + "', " + p.getUrlimagen() + ")";
                        db.execSQL(query);
                        System.out.println(query);
                    }
                    Log.e("TAG","onResponse:" + response.toString());
                }else{
                    Log.e("TAG","onResponse:" + response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Log.e("TAG","onFailure:" + t.getMessage());
            }
        });*/
    }

    public void InsertBolsas() throws IOException {
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor fila = db.rawQuery("select codigo from Reciclador", null);

        System.out.println(fila.getColumnNames().toString());

        fila.moveToFirst();

        String codigo = fila.getString(0);

        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Bolsa>> call=jsonPlaceHolderApi.getBolsaByReciclador("/bolsa/reciclador/" + codigo);
        Response<List<Bolsa>> response = call.execute();

        for (Bolsa p: response.body()) {
            if(p.getValidado()) {
                String query = "insert into Bolsa (codigo, usuario, condominio, qrcode, fechaCreado, validado) " +
                        "values (" + p.getCodigo() + ", " + p.getUsuario().getCodigo() + ", " + p.getUsuario().getCondominio().getCodigo() + ", '" + p.getQrCode().getQrCode() + "', '" + p.getCreadoFecha().toString() + "', 'true')";
                db.execSQL(query);
                System.out.println(query);
            }else{
                String query = "insert into Bolsa (codigo, usuario, condominio, qrcode, fechaCreado, validado) " +
                        "values (" + p.getCodigo() + ", " + p.getUsuario().getCodigo() + ", " + p.getUsuario().getCondominio().getCodigo() + ", '" + p.getQrCode().getQrCode() + "', '" + p.getCreadoFecha().toString() + "', 'false')";
                db.execSQL(query);
                System.out.println(query);
            }
        }
        Log.e("TAG","onResponse:" + response.toString());
    }

    public void InsertProbolsa() throws IOException {
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor fila = db.rawQuery("select codigo from Reciclador", null);

        fila.moveToFirst();

        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Probolsa>> call=jsonPlaceHolderApi.getProbolsaByReciclador("/probolsa/reciclador/" + fila.getInt(0));
        Response<List<Probolsa>> response = call.execute();
        for (Probolsa p: response.body()) {
            String query = "insert into Probolsa (bolsa, cantidad, codigo, peso, producto, puntuacion) " +
                    "values (" + p.getBolsa().getCodigo() + ", " + p.getCantidad() + ", " + p.getCodigo() + ", "
                    + p.getPeso() + ", " + p.getProducto().getCodigo() + ", " + p.getPuntuacion() + ")";
            db.execSQL(query);
            System.out.println(query);
        }
        Log.e("TAG","onResponse:" + response.toString());
    }

    public void InsertUsuarios() throws IOException {
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);

        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor fila = db.rawQuery("select usuario from Bolsa", null);

        if(fila.moveToFirst()){
                do{

                    Cursor fila2 = db.rawQuery("select codigo from Usuario where codigo = " + fila.getInt(0), null);

                    if(!fila2.moveToFirst()) {
                        Call<Usuario> call = jsonPlaceHolderApi.getUsuario("/usuario/" + fila.getInt(0));
                        Response<Usuario> response = call.execute();
                        String[] ayuda = new String[4];
                        ayuda[0] = response.body().getNombre();
                        ayuda[1] = response.body().getApellido();
                        ayuda[2] = response.body().getCondominio().getNombre();
                        ayuda[3] = response.body().getCodigo().toString();
                        String query = "insert into Usuario (nombre, apellido, condominio_name, codigo) " +
                                "values ('" + ayuda[0] + "', '" + ayuda[1] + "', '" + ayuda[2] + "', " + ayuda[3] + ")";
                        db.execSQL(query);
                        System.out.println(query);

                        Log.e("TAG", "onResponse:" + response.toString());
                    }
                }while(fila.moveToNext());
        }
    }

    public void getContenedorByCondominio()throws IOException{
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor condominioData = db.rawQuery("select codigo from Condominio",null);
        while (condominioData.moveToNext()){
            JsonPlaceHolderApi jsonPlaceHolderApi =retrofit.create(JsonPlaceHolderApi.class);
            Call<ContenedorTemp> call=jsonPlaceHolderApi.getContenedorByCondominio("bolsa/activas/condominio/"+Integer.toString(condominioData.getInt(0)));
            Response<ContenedorTemp> response = call.execute();
            System.out.println(Integer.toString(response.body().getCantBolsas()));
            String query = "insert into Contenedor (cantidad,condominiocodigo,pesoTotalBolsas) " +
                    "values ("+response.body().getCantBolsas()+"," + condominioData.getInt(0) + ", " + response.body().getCantidadTotal() + ")";
            db.execSQL(query);
            System.out.println(query);
        }
    }

  /*  public void obtenerDatosProductByBolsa() throws IOException, InterruptedException{
        initialCounter();
        dbHelper helper = new dbHelper(context,"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor f1 = db.rawQuery("select codigo from LastBolsas ",null);
        int codigoBolsa=0;
        if(f1.moveToFirst()){
            do {
                codigoBolsa = f1.getInt(0);
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                Call<List<Probolsa>> call = jsonPlaceHolderApi.getProductoByIdBolsa("probolsa/bolsa/" + codigoBolsa);
                Response<List<Probolsa>> response = call.execute();
                for (Probolsa probolsas : response.body()) {
                    if (probolsas.getProducto().getCategoria().getNombre().equals("Plastico")) {
                        plasticoCount += probolsas.getCantidad();
                        puntosPlastico += probolsas.getPuntuacion();
                        pesoPlastico += probolsas.getPeso();
                    } /*else if (probolsas.getProducto().getCategoria().getNombre().equals("Vidrio")) {
                        vidrioCount += probolsas.getCantidad();
                        puntosVidrio += probolsas.getPuntuacion();
                        pesoVidrio += probolsas.getPeso();
                    } else if (probolsas.getProducto().getCategoria().getNombre().equals("Metal")) {
                        metalCount += probolsas.getCantidad();
                        puntosMetal += probolsas.getPuntuacion();
                        pesoMetal += probolsas.getPeso();
                    } else {
                        papelCartonCount += probolsas.getCantidad();
                        puntosPapelCarton += probolsas.getPuntuacion();
                        pesoPapelCarton += probolsas.getPeso();
                    }
                    String query = "insert into LastProbolsas (codigo,bolsa) " +
                            "values (" + probolsas.getCodigo() + ","+probolsas.getBolsa().getCodigo()+")";
                    db.execSQL(query);
                    System.out.println(query);
                }
                generateQuery("LastBolsas", "Plastico", plasticoCount, pesoPlastico, puntosPlastico, codigoBolsa);
                generateQuery("LastBolsas", "Vidrio", vidrioCount, pesoVidrio, puntosVidrio, codigoBolsa);
                generateQuery("LastBolsas", "Papel", papelCartonCount, pesoPapelCarton, puntosPapelCarton,codigoBolsa);
                generateQuery("LastBolsas", "Metal", metalCount, pesoMetal, puntosMetal, codigoBolsa);
                initialCounter();

            }while(f1.moveToNext()); }
    }*/

    /*public void insertBolsasByMonthOrWeek(String urlDate) throws IOException, InterruptedException{
        initialCounter();
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor fila = db.rawQuery("select codigo from Usuario", null);

        fila.moveToFirst();
        int valor =0;
        String codigo = fila.getString(fila.getColumnIndex("codigo"));
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Probolsa>> call=jsonPlaceHolderApi.getBolsasByDate("probolsa/"+urlDate+codigo);
        Response<List<Probolsa>> response = call.execute();
        for(Probolsa bolsasbydate: response.body()){
            valor++;
            if (bolsasbydate.getBolsa().getRecojoFecha() != null) {
                if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Plastico") || bolsasbydate.getProducto().getCategoria().getNombre().equals("Papel/Carton")) {
                    bolsasWeek.add(bolsasbydate.getBolsa().getCodigo());
                    if (urlDate.equals("bolsasWeek/") || urlDate.equals("bolsasMonth/")) {
                        Date dia = bolsasbydate.getBolsa().getRecojoFecha();
                        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                        if (simpleDateformat.format(dia).equals("Monday")) {
                            yAxisDataMonth[0] += 1;
                            xAxisDataMonthPuntos[0] += bolsasbydate.getPuntuacion();
                        } else if (simpleDateformat.format(dia).equals("Tuesday")) {
                            yAxisDataMonth[1] += 1;
                            xAxisDataMonthPuntos[1] += bolsasbydate.getPuntuacion();
                        } else if (simpleDateformat.format(dia).equals("Wednesday")) {
                            yAxisDataMonth[2] += 1;
                            xAxisDataMonthPuntos[2] += bolsasbydate.getPuntuacion();
                        } else if (simpleDateformat.format(dia).equals("Thursday")) {
                            yAxisDataMonth[3] += 1;
                            xAxisDataMonthPuntos[3] += bolsasbydate.getPuntuacion();
                        } else if (simpleDateformat.format(dia).equals("Friday")) {
                            yAxisDataMonth[4] += 1;
                            xAxisDataMonthPuntos[4] += bolsasbydate.getPuntuacion();
                        } else if (simpleDateformat.format(dia).equals("Saturday")) {
                            yAxisDataMonth[5] += 1;
                            xAxisDataMonthPuntos[5] += bolsasbydate.getPuntuacion();
                        } else {
                            yAxisDataMonth[6] += 1;
                            xAxisDataMonthPuntos[6] += bolsasbydate.getPuntuacion();
                        }
                        addingValuestoText(bolsasbydate);
                    }
                }
            }
        }
        if(valor>0) {
            generateQuery("Semana", "Plastico", plasticoCount, pesoPlastico, puntosPlastico,0);
            generateQuery("Semana", "Vidrio", vidrioCount, pesoVidrio, puntosVidrio,0);
            generateQuery("Semana", "Papel", papelCartonCount, pesoPapelCarton, puntosPapelCarton,0);
            generateQuery("Semana", "Metal", metalCount, pesoMetal, puntosMetal,0);
            String query = "insert into DatosDiarios(tipo,lunes,martes,miercoles,jueves,viernes,sabado,domingo) " +
                    "values ('Semana', " + yAxisDataMonth[0] + ", " + yAxisDataMonth[1] + ", "
                    + yAxisDataMonth[2] + ", " + yAxisDataMonth[3] + ", " + yAxisDataMonth[4] + "," + yAxisDataMonth[5] + "," + yAxisDataMonth[6] + ")";
            db.execSQL(query);
            System.out.println(query);

            String query2 = "insert into DatosDiarios(tipo,lunes,martes,miercoles,jueves,viernes,sabado,domingo) " +
                    "values ('puntos', " + xAxisDataMonthPuntos[0] + ", " + xAxisDataMonthPuntos[1] + ", "
                    + xAxisDataMonthPuntos[2] + ", " + xAxisDataMonthPuntos[3] + ", " + xAxisDataMonthPuntos[4] + "," + xAxisDataMonthPuntos[5] + "," + xAxisDataMonthPuntos[6] + ")";
            db.execSQL(query2);
            System.out.println(query2);

        }

        Log.e("TAG","onResponse:" + response.toString());

        try {
            obtenerNumberBolsasByWeek();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void obtenerNumberBolsasByWeek() throws IOException, InterruptedException, ParseException {
        initialCounter();
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        int valor = 0;
        for (Integer bolsasWeek : bolsasWeek) {
            Cursor f1 = db.rawQuery("select recojoFecha from Bolsa where codigo = "+ bolsasWeek,null);
            System.out.println(bolsasWeek);
            if(f1.moveToFirst()) {
                do {
                    valor++;
                    System.out.println("Entro");
                    if(!f1.getString(0).equals("null") || !f1.getString(0).equals(null)) {
                        String sDate1 = f1.getString(0);
                        Date dia =new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(sDate1);
                        System.out.println(dia.toString());
                        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE", Locale.ENGLISH);

                        if (simpleDateformat.format(dia).equals("Monday")) {
                            yAxisDataMonthBoslsas[0] += 1;
                        } else if (simpleDateformat.format(dia).equals("Tuesday"))
                            yAxisDataMonthBoslsas[1] += 1;
                        else if (simpleDateformat.format(dia).equals("Wednesday"))
                            yAxisDataMonthBoslsas[2] += 1;
                        else if (simpleDateformat.format(dia).equals("Thursday")) {
                            yAxisDataMonthBoslsas[3] += 1;
                        } else if (simpleDateformat.format(dia).equals("Friday"))
                            yAxisDataMonthBoslsas[4] += 1;
                        else if (simpleDateformat.format(dia).equals("Saturday"))
                            yAxisDataMonthBoslsas[5] += 1;
                        else
                            yAxisDataMonthBoslsas[6] += 1;
                    }

                } while (f1.moveToNext()) ;
            }
        }

        if(valor>0) {
            String query = "insert into DatosDiarios(tipo,lunes,martes,miercoles,jueves,viernes,sabado,domingo) " +
                    "values ('bolsas', " + yAxisDataMonthBoslsas[0] + ", " + yAxisDataMonthBoslsas[1] + ", "
                    + yAxisDataMonthBoslsas[2] + ", " + yAxisDataMonthBoslsas[3] + ", " + yAxisDataMonthBoslsas[4] + "," + yAxisDataMonthBoslsas[5] + "," + yAxisDataMonthBoslsas[6] + ")";
            db.execSQL(query);
            System.out.println(query);

        }

    }

    public void obtenerBolsasByDay()throws IOException, InterruptedException{
        pesoCount=0;
        bolsasCount=0;
        puntosCount=0;
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor fila = db.rawQuery("select codigo from Usuario", null);
        fila.moveToFirst();
        String codigo = fila.getString(fila.getColumnIndex("codigo"));
        Set<Integer> bolsas = new HashSet<>();
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Probolsa>> call=jsonPlaceHolderApi.getBolsasByDate("probolsa/bolsasDay/"+codigo);
        Response<List<Probolsa>> response = call.execute();
        for(Probolsa probolsas : response.body()){
            bolsas.add(probolsas.getBolsa().getCodigo());
            pesoCount+=probolsas.getPeso();
            puntosCount+=probolsas.getPuntuacion();
        }
        String query = "insert into Contador (tendenciaTipo,cantidad ,peso ,puntuacion ) " +
                "values ('Dia', " +  bolsas.size()+ ", " + pesoCount + ", "
                + puntosCount + ")";
        db.execSQL(query);
        System.out.println(query);


        Log.e("TAG","onResponse:" + response.toString());


    }

    public void obtenerUltimasBolsas()throws IOException, InterruptedException{
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor fila = db.rawQuery("select codigo from Usuario", null);
        fila.moveToFirst();
        int valor =0;
        String codigo = fila.getString(fila.getColumnIndex("codigo"));
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Bolsa>> call=jsonPlaceHolderApi.getBolsasByUsuario("bolsa/last/"+codigo);
        Response<List<Bolsa>> response = call.execute();
        for(Bolsa lastBolsas : response.body()){
            String query = "insert into LastBolsas (codigo) " +
                    "values (" + lastBolsas.getCodigo() + ")";
            db.execSQL(query);
            System.out.println(query);
        }

        Log.e("TAG","onResponse:" + response.toString());
    }

    public void obtenerBolsasByYear(String urlDate)throws IOException, InterruptedException{
        initialCounter();
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Set<Integer> bolsas = new HashSet<>();
        Cursor fila = db.rawQuery("select codigo from Usuario", null);

        fila.moveToFirst();
        int valor =0;
        String codigo = fila.getString(fila.getColumnIndex("codigo"));
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Probolsa>> call=jsonPlaceHolderApi.getBolsasByDate("probolsa/"+urlDate+codigo);
        Response<List<Probolsa>> response = call.execute();
        for (Probolsa bolsasbydate : response.body()) {
            valor++;
            if (bolsasbydate.getBolsa().getRecojoFecha() != null) {
                if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Plastico") || bolsasbydate.getProducto().getCategoria().getNombre().equals("Papel/Carton")) {
                    System.out.println(bolsasbydate.getProducto().getCategoria().getNombre());
                    bolsasLast.add(bolsasbydate.getBolsa().getCodigo());
                    Date dia = bolsasbydate.getBolsa().getRecojoFecha();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dia);
                    if (cal.get(Calendar.MONTH) == 0) {
                        yAxisDataYear[0] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[0] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 1) {
                        yAxisDataYear[1] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[1] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 2) {
                        yAxisDataYear[2] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[2] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 3) {
                        yAxisDataYear[3] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[3] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 4) {
                        yAxisDataYear[4] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[4] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 5) {
                        yAxisDataYear[5] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[5] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 6) {
                        yAxisDataYear[6] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[6] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 7) {
                        yAxisDataYear[7] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[7] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 8) {
                        yAxisDataYear[8] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[8] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 9) {
                        yAxisDataYear[9] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[9] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 10) {
                        yAxisDataYear[10] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[10] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 11) {
                        yAxisDataYear[11] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[11] += bolsasbydate.getPuntuacion();
                    }
                    bolsas.add(bolsasbydate.getBolsa().getCodigo());
                    addingValuestoText(bolsasbydate);
                }
            }

        }
        if(valor>0) {
            generateQuery("Year", "Plastico", plasticoCount, pesoPlastico, puntosPlastico,bolsas.size());
            generateQuery("Year", "Vidrio", vidrioCount, pesoVidrio, puntosVidrio,bolsas.size());
            generateQuery("Year", "Papel", papelCartonCount, pesoPapelCarton, puntosPapelCarton,bolsas.size());
            generateQuery("Year", "Metal", metalCount, pesoMetal, puntosMetal,bolsas.size());
            String query = "insert into DatosAnuales(enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre,tipo) " +
                    "values ( " + yAxisDataYear[0] + ", " + yAxisDataYear[1] + ", "
                    + yAxisDataYear[2] + ", " + yAxisDataYear[3] + ", " + yAxisDataYear[4] + "," + yAxisDataYear[5]  + ", "
                    + yAxisDataYear[6] + ", " + yAxisDataYear[7] + ","+ + yAxisDataYear[8] + ","+yAxisDataYear[9] + ", " + yAxisDataYear[10] +","+ yAxisDataYear[11] +  ",'probolsa')";
            db.execSQL(query);
            System.out.println(query);

            String query2 = "insert into DatosAnuales(enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre,tipo) " +
                    "values ( " + yAxisDataYearPunto[0] + ", " + yAxisDataYearPunto[1] + ", "
                    + yAxisDataYearPunto[2] + ", " + yAxisDataYearPunto[3] + ", " + yAxisDataYearPunto[4] + "," + yAxisDataYearPunto[5]  + ", "
                    + yAxisDataYearPunto[6] + ", " + yAxisDataYearPunto[7] + ","+ + yAxisDataYearPunto[8] + ","+yAxisDataYearPunto[9] + ", " + yAxisDataYearPunto[10] +","+ yAxisDataYearPunto[11] +  ",'puntos')";
            db.execSQL(query2);
            System.out.println(query2);

        }

        Log.e("TAG","onResponse:" + response.toString());
        System.out.println(bolsasLast.size());
        try {
            obtenerLasBolsasMonth("bolsasYear/");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void obtenerLasBolsasMonth(String urlDate) throws ParseException {
        initialCounter();
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        int valor = 0;
        for (Integer bolsasLast : bolsasLast) {
            Cursor f1 = db.rawQuery("select recojoFecha from Bolsa where codigo = "+ bolsasLast,null);
            if(f1.moveToFirst()){
                do {
                    valor++;
                    if(!f1.getString(0).equals("null") || !f1.getString(0).equals(null)) {
                        String sDate1 = f1.getString(0);
                        Date dia=new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(sDate1);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(dia);
                        if (cal.get(Calendar.MONTH) == 0)
                            yAxisDataYearBolsa[0] += 1;
                        else if (cal.get(Calendar.MONTH) == 1)
                            yAxisDataYearBolsa[1] += 1;
                        else if (cal.get(Calendar.MONTH) == 2)
                            yAxisDataYearBolsa[2] += 1;
                        else if (cal.get(Calendar.MONTH) == 3)
                            yAxisDataYearBolsa[3] += 1;
                        else if (cal.get(Calendar.MONTH) == 4)
                            yAxisDataYearBolsa[4] += 1;
                        else if (cal.get(Calendar.MONTH) == 5)
                            yAxisDataYearBolsa[5] += 1;
                        else if (cal.get(Calendar.MONTH) == 6)
                            yAxisDataYearBolsa[6] += 1;
                        else if (cal.get(Calendar.MONTH) == 7)
                            yAxisDataYearBolsa[7] += 1;
                        else if (cal.get(Calendar.MONTH) == 8)
                            yAxisDataYearBolsa[8] += 1;
                        else if (cal.get(Calendar.MONTH) == 9)
                            yAxisDataYearBolsa[9] += 1;
                        else if (cal.get(Calendar.MONTH) == 10)
                            yAxisDataYearBolsa[10] += 1;
                        else if (cal.get(Calendar.MONTH) == 11)
                            yAxisDataYearBolsa[11] += 1;
                    }

                }while(f1.moveToNext());
            }

        }
        if(valor>0) {
            String query = "insert into DatosAnuales(enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre,tipo) " +
                    "values ( " + yAxisDataYearBolsa[0] + ", " + yAxisDataYearBolsa[1] + ", "
                    + yAxisDataYearBolsa[2] + ", " + yAxisDataYearBolsa[3] + ", " + yAxisDataYearBolsa[4] + "," + yAxisDataYearBolsa[5]  + ", "
                    + yAxisDataYearBolsa[6] + ", " + yAxisDataYearBolsa[7] + ","+ + yAxisDataYearBolsa[8] + ","+yAxisDataYearBolsa[9] + ", " + yAxisDataYearBolsa[10] +","+ yAxisDataYearBolsa[11] +  ",'bolsa')";
            db.execSQL(query);
            System.out.println(query);

        }
    }

    public void initialCounter(){
        plasticoCount=0;
        pesoPlastico=0;
        puntosPlastico=0;
        vidrioCount=0;
        pesoVidrio=0;
        puntosVidrio=0;
        papelCartonCount=0;
        pesoPapelCarton=0;
        puntosPapelCarton=0;
        metalCount=0;
        pesoMetal=0;
        puntosMetal=0;
        residuosTotal=0;
    }

    public void addingValuestoText(Probolsa bolsasbydate ){
        if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Plastico")) {
            pesoPlastico += bolsasbydate.getProducto().getPeso();
            puntosPlastico += bolsasbydate.getPuntuacion();
            plasticoCount += bolsasbydate.getCantidad();
        }else if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Vidrio")) {
            pesoVidrio += bolsasbydate.getProducto().getPeso();
            puntosVidrio += bolsasbydate.getPuntuacion();
            vidrioCount+= bolsasbydate.getCantidad();
        } else if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Papel/Carton")) {
            pesoPapelCarton += bolsasbydate.getProducto().getPeso();
            puntosPapelCarton += bolsasbydate.getPuntuacion();
            papelCartonCount+=bolsasbydate.getCantidad();
        } else if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Metal")) {
            pesoMetal += bolsasbydate.getProducto().getPeso();
            puntosMetal += bolsasbydate.getPuntuacion();
            metalCount+=bolsasbydate.getCantidad();
        }
    }*/

    public void generateQuery(String tipo,String producto,double cantidad,double peso,double puntuacion,int bolsaID){
        String query = "insert into Contador (tendenciaTipo,productoTipo,cantidad,peso,puntuacion,bolsa) " +
                "values ('" + tipo + "', '" + producto + "', " + cantidad + ", "
                + peso + ", " + puntuacion + ","+bolsaID+")";
        db.execSQL(query);
        System.out.println(query);
    }

}
