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
    //Registrar usuario (Cliente o Emprendedor)
    public boolean registrarUsuario(Usuario usuario, String tipo, String mensaje) {
        try {
            if ("Emprendedor".equalsIgnoreCase(tipo)) {
                return repoU.insertarEmprendedor(usuario, mensaje);
            } else if ("Cliente".equalsIgnoreCase(tipo)) {
                return repoU.insertarCliente(usuario);
            } else {
                System.err.println("Tipo de usuario no válido: " + tipo);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error en registro de usuario: " + e.getMessage());
            return false;
        }
    }
}
