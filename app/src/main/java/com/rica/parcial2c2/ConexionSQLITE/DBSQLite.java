/*
@actor Ricardo Adalberto Iraheta Amaya
 */
package com.rica.parcial2c2.ConexionSQLITE;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBSQLite extends SQLiteOpenHelper {

    static String nameDBTienda = "db_tiendaProductos";
    static String tblProducto = "CREATE TABLE tiendaProductos(idProducto integer primary key autoincrement, codigo text, descrip text, marca text, presen text, precio text, url text)";

    public DBSQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, nameDBTienda, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tblProducto);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public Cursor MantenimientoProductos(String accion, String[] data){
        SQLiteDatabase sqLiteDatabaseReadable=getReadableDatabase();
        SQLiteDatabase sqLiteDatabaseWritable=getWritableDatabase();
        Cursor cursor = null;
        switch (accion){
            case "consultar":
                cursor=sqLiteDatabaseReadable.rawQuery("SELECT * FROM tiendaProductos ORDER BY codigo ASC", null);
                break;
            case "nuevo":
                sqLiteDatabaseWritable.execSQL("INSERT INTO tiendaProductos (codigo,descrip,marca,presen,precio,url) VALUES('"+ data[1] +"','"+data[2]+"','"+data[3]+"','"+data[4]+"','"+data[5]+"','"+data[6]+"')");

                break;
            case "modificar":
                sqLiteDatabaseWritable.execSQL("UPDATE tiendaProductos SET codigo='"+ data[1] +"',descrip='"+data[2]+"',marca='"+data[3]+"',presen='"+data[4]+"', precio='"+data[5]+"', url='"+data[6]+"' WHERE idProducto='"+data[0]+"'");
                break;
            case "eliminar":
                sqLiteDatabaseWritable.execSQL("DELETE FROM tiendaProductos WHERE idProducto='"+ data[0] +"'");
                break;
        }
        return cursor;
    }
}
