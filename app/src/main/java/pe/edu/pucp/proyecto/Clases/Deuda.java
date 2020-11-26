package pe.edu.pucp.proyecto.Clases;

public class Deuda {

    private String producto;
    private int cantidad;
    private float precio;
    private String descripcion;

    public Deuda(String producto, int cantidad, float precio, String descripcion) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }




}
