package servicio;

import modelo.Usuario;
import repositorio.IRUsuario;

// Servicio de negocio para la gestión de usuarios.
public class SUsuario implements ISUsuario {
    private final IRUsuario repoU;

    // Constructor con inyección de dependencia.
    public SUsuario(IRUsuario repoU) {
        this.repoU = repoU;
    }

    // Verifica si las credenciales corresponden a un usuario válido.
    public boolean autenticar(String correo, String contrasena) {
        Usuario user = repoU.autenticar(correo, contrasena);
        return user != null;
    }

    // Obtiene el nombre del usuario a partir del email.
    public String obtenerNombre(String correo) {
        Usuario user = repoU.buscarPorCorreo(correo);
        if (user != null) {
            return user.getDatosPersonales().getNombre();
        } else {
            return null;
        }
    }

    // Obtiene el apellido del usuario a partir del email.
    public String obtenerApellido(String correo) {
        Usuario user = repoU.buscarPorCorreo(correo);
        if (user != null) {
            return user.getDatosPersonales().getApellido();
        } else {
            return null;
        }
    }

    // Obtiene el rol del usuario (Cliente, Emprendedor, Reclutador) a partir del nombre.
    public String obtenerRol(String correo) {
        Usuario user = repoU.buscarPorCorreo(correo);
        if (user != null) {
            return user.getRol().getNombre();
        } else {
            return null;
        }
    }
}
