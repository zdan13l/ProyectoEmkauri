package servicio;

import modelo.Usuario;

// Interfaz del servicio de usuario.
public interface ISUsuario {
    boolean autenticar(String correo, String contrasena);
    Usuario obtenerUsuario(String correo);
    String obtenerNombre(String correo);
    String obtenerApellido(String correo);
    String obtenerRol(String correo);
    boolean registrarUsuario(Usuario usuario, String mensaje);
}
