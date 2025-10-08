package modelo;

// Modelo que representa un usuario en el sistema.
public class Usuario {
    private int idUsuario;
    private String nombre;
    private String email;
    private String contrasena;

    public Usuario(int idUsuario, String nombre, String email, String contrasena) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getContrasena() {
        return contrasena;
    }

    @Override
    public String toString() { return "Usuario"; }
}

