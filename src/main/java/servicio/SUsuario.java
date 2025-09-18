package servicio;

import modelo.Usuario;
import repositorio.RUsuario;

// Servicio de negocio para la gestión de usuarios.
public class SUsuario {
    private final RUsuario repoU = new RUsuario();

    // Verifica si las credenciales corresponden a un usuario válido.
    public boolean autenticar(String nombre, String contrasena) {
        Usuario user = repoU.autenticar(nombre, contrasena);
        return user != null;
    }

    // Obtiene el nombre del usuario a partir del email.
    public String obtenerNombre(String nombre) {
        Usuario user = repoU.buscarPorNombre(nombre);
        if (user != null) {
            return user.getNombre();
        } else {
            return null;
        }
    }

    // Obtiene el rol del usuario (Cliente, Emprendedor, Reclutador) a partir del nombre.
    public String obtenerRol(String nombre) {
        Usuario user = repoU.buscarPorNombre(nombre);
        if (user != null) {
            return repoU.obtenerRol(user.getIdUsuario());
        }
        return "Desconocido";
    }
}
