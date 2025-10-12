package servicio;

import modelo.Usuario;

public interface ISUsuario {
    boolean autenticar(String correo, String contrasena);
    String obtenerNombre(String correo);
    String obtenerApellido(String correo);
    String obtenerRol(String correo);

    boolean registrarUsuario(Usuario usuario, String tipo, String mensaje);
}
