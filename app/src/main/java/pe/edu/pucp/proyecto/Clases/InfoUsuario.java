package pe.edu.pucp.proyecto.Clases;

public class InfoUsuario {



    private String usuario;
    private String nombre;

    private String contraseña;
    private String correo;
    private String numeroTelefonico;
    private String direccion;

    private String tipo;

    private String uid;


    private String montoTotal;

    private String cantDeudas;




    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCantDeudas() {
        return cantDeudas;
    }

    public void setCantDeudas(String cantDeudas) {
        this.cantDeudas = cantDeudas;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMontoTotal() {

            return montoTotal;

    }

    public void setMontoTotal(String montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }




    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }




    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }


    public String getNumeroTelefonico() {
        return numeroTelefonico;
    }

    public void setNumeroTelefonico(String numeroTelefonico) {
        this.numeroTelefonico = numeroTelefonico;
    }


}
