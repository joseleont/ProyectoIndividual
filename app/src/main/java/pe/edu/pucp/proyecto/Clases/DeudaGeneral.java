package pe.edu.pucp.proyecto.Clases;

public class DeudaGeneral {

    String MontoDeuda;
    String Fecha;
    String identificador;

    //Para el pago
    String montoPagado;
    String estado;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(String montoPagado) {
        this.montoPagado = montoPagado;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getMontoDeuda() {
        return MontoDeuda;
    }

    public void setMontoDeuda(String montoDeuda) {
        MontoDeuda = montoDeuda;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        this.Fecha = fecha;
    }
}
