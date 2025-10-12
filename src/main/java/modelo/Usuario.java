package modelo;

// Modelo que representa un usuario en el sistema.
public class Usuario {
    private int idUsuario;
    private String correo;
    private String contrasena;
    private Datos datos;
    private Rol rol;

    public Usuario() {
        this.idUsuario = 0;
        this.correo = "";
        this.contrasena = "";
        this.datos = new Datos();
        this.rol = new Rol();
    }

    public Usuario(int idUsuario, String correo, String contrasena, Datos datos, Rol rol) {
        this.idUsuario = idUsuario;
        this.correo = correo;
        this.contrasena = contrasena;
        this.datos = datos;
        this.rol = rol;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public Datos getDatosPersonales() { return datos; }
    public void setDatosPersonales(Datos datos) { this.datos = datos; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    @Override
    public String toString() { return "Usuario"; }
}




