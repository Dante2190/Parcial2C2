/*
@actor Ricardo Adalberto Iraheta Amaya
 */
package com.rica.parcial2c2.ConexionSQLITE;

public class Productos {

    String id;
    String codigo;
    String descrip;
    String marca;
    String presen;
    String precio;
    String urlImg;

    public Productos( String id, String codigo, String descrip, String marca, String presen, String precio, String urlImg) {
        this.id = id;
        this.codigo = codigo;
        this.descrip = descrip;
        this.marca = marca;
        this.presen = presen;
        this.precio = precio;
        this.urlImg = urlImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPresen() {
        return presen;
    }

    public void setPresen(String presentacion) {
        this.presen = presentacion;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }
}
