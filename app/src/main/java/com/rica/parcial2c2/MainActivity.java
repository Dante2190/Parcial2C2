/*
@actor Ricardo Adalberto Iraheta Amaya
 */
package com.rica.parcial2c2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rica.parcial2c2.ConexionSQLITE.DBSQLite;
import com.rica.parcial2c2.ConexionSQLITE.Productos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DBSQLite miDB;
    Cursor misProductos;
    Productos producto;
    ArrayList<Productos> stringArrayList = new ArrayList<Productos>();
    ArrayList<Productos> copyStringArrayList = new ArrayList<Productos>();
    ListView listProduc;
    //couchDB
    JSONArray datosJSON;
    JSONObject jsonObject;
    int posicion = 0;
    ArrayList<String> arrayList =new ArrayList<String>();
    ArrayList<String> copyStringArrayLista = new ArrayList<String>();
    ArrayAdapter<String> stringArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DetactarInternet di = new DetactarInternet(getApplicationContext());
      //  ObtenerDatosCouchDb myAsync = new ObtenerDatosCouchDb();
      //  myAsync.execute();
      /*  if (di.hayConexionInternet()){

            myAsync.execute();
            //ObtenerProductos();
        }else {
            ObtenerProductos();
        }*/



        FloatingActionButton btnAgregar = (FloatingActionButton)findViewById(R.id.btnGuardarProductos);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarAmigo("nuevo", new String[]{});
            }
        });
        ObtenerProductos();
        BuscarProductos();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_productos, menu);

        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo)menuInfo;
        misProductos.moveToPosition(adapterContextMenuInfo.position);
        menu.setHeaderTitle(misProductos.getString(1));


    }
    void BuscarProductos(){
        final TextView tempVal = (TextView)findViewById(R.id.edtBuscar);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    stringArrayList.clear();
                    if (tempVal.getText().toString().trim().length() < 1) {
                        stringArrayList.addAll(copyStringArrayList);
                    } else {
                        for (Productos am : copyStringArrayList) {
                            String codigo = am.getCodigo();
                            String marca = am.getMarca();
                            String descrip = am.getDescrip();
                            if (codigo.toLowerCase().contains(tempVal.getText().toString().trim().toLowerCase()) ||
                                    marca.toLowerCase().contains(tempVal.getText().toString().trim().toLowerCase()) ||
                                    descrip.toLowerCase().contains(tempVal.getText().toString().trim().toLowerCase())) {
                                stringArrayList.add(am);
                            }
                        }
                    }
                    AdaptadorImagenes adaptadorImg = new AdaptadorImagenes(getApplicationContext(), stringArrayList);
                    listProduc.setAdapter(adaptadorImg);
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnxAgregar:
                agregarAmigo("nuevo", new String[]{});
                return true;

            case R.id.mnxModificar:
                String[] dataProducto = {
                        misProductos.getString(0),//idProducto
                        misProductos.getString(1),//codigo
                        misProductos.getString(2),//descripcion
                        misProductos.getString(3),//marca
                        misProductos.getString(4), //presentacion
                        misProductos.getString(5),  //precio
                        misProductos.getString(6)  //urlImg
                };
                agregarAmigo("modificar",dataProducto);
                return true;

            case R.id.mnxEliminar:
                AlertDialog eliminarFriend =  eliminarAmigo();
                eliminarFriend.show();
               // new EliminarDatosCouchDB();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
    AlertDialog eliminarAmigo(){
        AlertDialog.Builder confirmacion = new AlertDialog.Builder(MainActivity.this);
        confirmacion.setTitle(misProductos.getString(1));
        confirmacion.setMessage("Esta seguro de eliminar el producto?");
        confirmacion.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                miDB.MantenimientoProductos("eliminar",new String[]{misProductos.getString(0)});
                ObtenerProductos();
                Toast.makeText(getApplicationContext(), "Producto eliminado con exito.",Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        confirmacion.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "Eliminacion cancelada por el usuario.",Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        return confirmacion.create();
    }
    void ObtenerProductos(){
        miDB = new DBSQLite(getApplicationContext(), "", null, 1);
        misProductos = miDB.MantenimientoProductos("consultar", null);
        if( misProductos.moveToFirst() ){ //hay registro en la BD que mostrar
            mostrarDatosProductos();
        } else{ //No tengo registro que mostrar.
            Toast.makeText(getApplicationContext(), "No hay registros de amigos que mostrar",Toast.LENGTH_LONG).show();
            agregarAmigo("nuevo", new String[]{});
        }
    }
    void agregarAmigo(String accion, String[] dataAmigo){
        Bundle enviarParametros = new Bundle();
        enviarParametros.putString("accion",accion);
        enviarParametros.putStringArray("dataAmigo",dataAmigo);
        Intent agregarAmigos = new Intent(MainActivity.this, ProductDetalle.class);
        agregarAmigos.putExtras(enviarParametros);
        startActivity(agregarAmigos);
    }
    void mostrarDatosProductos(){
        stringArrayList.clear();
        listProduc = (ListView)findViewById(R.id.listProductos);
        do {
            producto = new Productos(misProductos.getString(0),misProductos.getString(1), misProductos.getString(2), misProductos.getString(3), misProductos.getString(4), misProductos.getString(5), misProductos.getString(6));
            stringArrayList.add(producto);
        }while(misProductos.moveToNext());
        AdaptadorImagenes adaptadorImg = new AdaptadorImagenes(getApplicationContext(), stringArrayList);
        listProduc.setAdapter(adaptadorImg);

        copyStringArrayList.clear();
        copyStringArrayList.addAll(stringArrayList);
        registerForContextMenu(listProduc);
    }
    //couchDB

    private class ObtenerDatosCouchDb extends AsyncTask<Void, Void, String> {
        HttpURLConnection URLConnection;
        @Override
        protected String doInBackground(Void... params) {
            StringBuilder result = new StringBuilder();
            try{
                //oneccion al servidor
                URL url = new URL("http://192.168.1.15:5984/db_tienda/_design/tienda/_view/mi-tienda");
                URLConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(URLConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null){
                    result.append(line);
                }
            }catch (Exception ex){
                Log.e("Mi Error", "Error", ex);
                ex.printStackTrace();
            }finally {
                URLConnection.disconnect();
            }
            return result.toString();
        }
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            try {
                jsonObject = new JSONObject(s);
                datosJSON= jsonObject.getJSONArray("rows");
                mostrarProductos();
            }catch (Exception ex){
                Toast.makeText(MainActivity.this, "Error: q paso aqui  " + ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void mostrarProductos(){
        ListView ltsAmigos = findViewById(R.id.listProductos);
        try {
            arrayList.clear();
            stringArrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
            ltsAmigos.setAdapter(stringArrayAdapter);

            for (int i = 0; i < datosJSON.length(); i++) {
                stringArrayAdapter.add(datosJSON.getJSONObject(i).getJSONObject("value").getString("marca"));
            }
            copyStringArrayLista.clear();
            copyStringArrayLista.addAll(arrayList);

            stringArrayAdapter.notifyDataSetChanged();
            registerForContextMenu(ltsAmigos);
        }catch (Exception ex){
            Toast.makeText(MainActivity.this, "Error al mostrar los datos: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private class EliminarDatosCouchDB  extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();

            String JsonResponce = null;
            String JsonData = params[0];
            BufferedReader reader = null;

            try {
                //conexion al servidor
                String uri = "http://192.168.1.15:5984/db_tienda/"+
                        datosJSON.getJSONObject(posicion).getJSONObject("value").getString("_id")+"?rev="+
                        datosJSON.getJSONObject(posicion).getJSONObject("value").getString("_rev");
                URL url = new URL(uri);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null){
                    result.append(line);
                }
            }catch (Exception ex){
                Log.e("Mi Error", "Error", ex);
                ex.printStackTrace();
            }finally {
                urlConnection.disconnect();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("ok")){
                    Toast.makeText(MainActivity.this, "registro de eliminacion con exito", Toast.LENGTH_SHORT).show();
                    Intent regresar = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(regresar);
                }else {
                    Toast.makeText(MainActivity.this, "error al intentar eliminar el registro ", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception ex){
                Toast.makeText(MainActivity.this, "error al enviar a la red"+ ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}