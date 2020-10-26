/*
@actor Ricardo Adalberto Iraheta Amaya
 */
package com.rica.parcial2c2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rica.parcial2c2.ConexionSQLITE.DBSQLite;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class ProductDetalle extends AppCompatActivity {
    utilidadesComunes uc;
    String  resp, id, rev;
    DBSQLite miDB;
    String accion = "nuevo";
    String idProducto = "0";
    ImageView imgProducto;
    String urlCompletaImg;
    Button btnProductos,btnProductos1;
    Intent takePictureIntent;
    private ImageView mUploadPicture;
    private ImageView mImageView;
    private Bitmap mCaptureOrUploadBitmap=null;
    private String mProductImagePath=null;
    String productImagePath;
    Cursor cr;

    EditText edt1,edt2,edt3,edt4,edt5;


    private static final int UPLOAD_PICTURE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detalle);

        edt1 = (EditText)findViewById(R.id.edtCodigo);
        edt2 = (EditText)findViewById(R.id.edtDescrip);
        edt3 = (EditText)findViewById(R.id.edtMarca);
        edt4 = (EditText)findViewById(R.id.edtPresen);
        edt5 = (EditText)findViewById(R.id.edtPrecio);

        mImageView = (ImageView) findViewById(R.id.imgProducto);
        imgProducto = findViewById(R.id.imgProducto);

        btnProductos = findViewById(R.id.btnMostrar);
        btnProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MostrarListaProductos();
            }
        });

        btnProductos1 = findViewById(R.id.btnProducto);
        btnProductos1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DetactarInternet di = new DetactarInternet(getApplicationContext());
                if (di.hayConexionInternet()){
                    GuardarProductosCoucDB();
                    GuardarProductosSQL();
                }else {
                    GuardarProductosSQL();
                }
                // Toast.makeText(getApplicationContext(),"Registro de productos insertado con exito", Toast.LENGTH_LONG).show();
                MostrarListaProductos();
            }
        });

        //GuardarDatosPrductos();
        //EnviarDatosCouchDB();
        MostrarDatosProductos();
        TomarFotoProducto();


        mUploadPicture = (ImageView) findViewById(R.id.suburImage);
        mUploadPicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT > 19) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                } else {
                    intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }

                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), UPLOAD_PICTURE);
            }
        });

    }

    void TomarFotoProducto(){
        imgProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    }catch (Exception ex){}
                    if (photoFile != null) {
                        try {
                            Uri photoURI = FileProvider.getUriForFile(ProductDetalle.this, "com.rica.parcial2c2.fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, 1);
                        }catch (Exception ex){
                            Toast.makeText(getApplicationContext(), "Error Toma Foto: "+ ex.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == RESULT_OK) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(urlCompletaImg);
                imgProducto.setImageBitmap(imageBitmap);
            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (resultCode == RESULT_OK&&requestCode==UPLOAD_PICTURE) {

            Uri targetURI = data.getData();
            Bitmap bitmap=null;

            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetURI));
                mImageView.setImageBitmap(bitmap);
                mCaptureOrUploadBitmap = bitmap;

            } catch (FileNotFoundException e) {

                e.printStackTrace();

            }

        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "imagen_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if( storageDir.exists()==false ){
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        urlCompletaImg = image.getAbsolutePath();
        return image;
    }

    public void GuardarProductosSQL(){
        if(mImageView.getDrawable() !=null){
            createProductImageFile();
            productImagePath = mProductImagePath;
        }else {
            productImagePath =  urlCompletaImg;
        }

        if (edt1.getText().toString().isEmpty() || edt2.getText().toString().isEmpty()  ||
                edt3.getText().toString().isEmpty()  || edt4.getText().toString().isEmpty()  ||
                edt5.getText().toString().isEmpty() ){
            Toast.makeText(ProductDetalle.this, "Rellene los campos por favor : ", Toast.LENGTH_LONG).show();
        }else {

            TextView tempVal = findViewById(R.id.edtCodigo);
            String codigo = tempVal.getText().toString();

            tempVal = findViewById(R.id.edtDescrip);
            String descrip = tempVal.getText().toString();

            tempVal = findViewById(R.id.edtMarca);
            String marca = tempVal.getText().toString();

            tempVal = findViewById(R.id.edtPresen);
            String presen = tempVal.getText().toString();

            tempVal = findViewById(R.id.edtPrecio);
            String precio = tempVal.getText().toString();

            String[] data = {idProducto, codigo, descrip, marca, presen, precio, productImagePath};

            miDB = new DBSQLite(getApplicationContext(), "", null, 1);
            miDB.MantenimientoProductos(accion, data);
            Toast.makeText(getApplicationContext(), "exito con sql", Toast.LENGTH_LONG).show();

        }
    }

       void MostrarListaProductos(){
        Intent mostrarProductos = new Intent(ProductDetalle.this, MainActivity.class);
        startActivity(mostrarProductos);
    }
    void MostrarDatosProductos(){
        try {
            Bundle recibirParametros = getIntent().getExtras();
            accion = recibirParametros.getString("accion");
            if (accion.equals("modificar")){
                String[] dataAmigo = recibirParametros.getStringArray("dataAmigo");

                idProducto = dataAmigo[0];

                TextView tempVal = (TextView)findViewById(R.id.edtCodigo);
                tempVal.setText(dataAmigo[1]);

                tempVal = (TextView)findViewById(R.id.edtDescrip);
                tempVal.setText(dataAmigo[2]);

                tempVal = (TextView)findViewById(R.id.edtMarca);
                tempVal.setText(dataAmigo[3]);

                tempVal = (TextView)findViewById(R.id.edtPresen);
                tempVal.setText(dataAmigo[4]);

                tempVal = (TextView)findViewById(R.id.edtPrecio);
                tempVal.setText(dataAmigo[5]);

                productImagePath= dataAmigo[6];
                Bitmap imageBitmap = BitmapFactory.decodeFile(productImagePath);
                imgProducto.setImageBitmap(imageBitmap);
            }
        }catch (Exception ex){
            ///
        }
    }

    private void createProductImageFile(){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("Pictures", Context.MODE_PRIVATE);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File myPath = new File(directory, imageFileName + ".jpg");

        mProductImagePath = myPath.toString();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            mCaptureOrUploadBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //aqui empieza couchDB
    public void GuardarProductosCoucDB(){

        if (edt1.getText().toString().isEmpty() || edt2.getText().toString().isEmpty()  ||
                edt3.getText().toString().isEmpty()  || edt4.getText().toString().isEmpty()  ||
                edt5.getText().toString().isEmpty() ){
            Toast.makeText(ProductDetalle.this, "Rellene los campos por favor : ", Toast.LENGTH_LONG).show();
        }else {
            TextView tempVal = findViewById(R.id.edtCodigo);
            String codigo = tempVal.getText().toString();

            tempVal = findViewById(R.id.edtDescrip);
            String descripcion = tempVal.getText().toString();

            tempVal = findViewById(R.id.edtMarca);
            String marca = tempVal.getText().toString();

            tempVal = findViewById(R.id.edtPresen);
            String presentacion = tempVal.getText().toString();

            tempVal = findViewById(R.id.edtPrecio);
            String precio = tempVal.getText().toString();


            JSONObject miData = new JSONObject();

            try {
                if (accion.equals("modificar")) {
                    miData.put("_id", id);
                    miData.put("_rev", rev);
                }
                miData.put("codigo", codigo);
                miData.put("descripcion", descripcion);
                miData.put("marca", marca);
                miData.put("presentacion", presentacion);
                miData.put("precio", precio);

                EnviarDatos objEnviar = new EnviarDatos();
                objEnviar.execute(miData.toString());


            } catch (Exception ex) {
                Toast.makeText(ProductDetalle.this, "Error al guardar: " + ex.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private class EnviarDatos extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();

            String JsonResponse = null;
            String JsonDATA = params[0];
            BufferedReader reader = null;

            try {
                URL url = new URL("http://192.168.1.15:5984/db_tienda/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(),"UTF-8"));
                writer.write(JsonDATA);
                writer.close();

                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                resp = reader.toString();
                String inputLine;
                StringBuffer buffer = new StringBuffer();

                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");

                if (buffer.length() == 0){
                    return null;
                }

                JsonResponse = buffer.toString();
                Log.i(TAG, JsonResponse);
                return JsonResponse;

            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                if (jsonObject.getBoolean("ok")){
                    Toast.makeText(ProductDetalle.this, "registro almacenado con exito: ", Toast.LENGTH_LONG).show();
                    Intent regresar = new Intent(ProductDetalle.this, MainActivity.class);
                    startActivity(regresar);
                }else {
                    Toast.makeText(ProductDetalle.this, "error al intentar almacenar el registro: ", Toast.LENGTH_LONG).show();
                }
            }catch (Exception ex){
                Toast.makeText(ProductDetalle.this, "error al enviar ala red: "+ ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}