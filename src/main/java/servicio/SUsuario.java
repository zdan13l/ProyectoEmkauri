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
        try {
            Usuario user = repoU.autenticar(correo, contrasena);
            return user != null;
        } catch (RuntimeException e) {
            switch (e.getMessage()) {
                case "PENDIENTE" ->
                        throw new RuntimeException("Tu solicitud de registro como emprendedor aún está pendiente de aprobación.");
                case "RECHAZADO" ->
                        throw new RuntimeException("Tu solicitud para ser emprendedor fue rechazada. Contacta al reclutador para más información.");
                case "SIN_SOLICITUD" ->
                        throw new RuntimeException("Tu cuenta de emprendedor no tiene una solicitud registrada. Comunícate con soporte para completar tu registro.");
                default -> throw e;
            }
        }
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
    // Registrar usuario (Cliente o Emprendedor).
    public boolean registrarUsuario(Usuario usuario, String mensaje) {
        try {
            if (usuario == null || usuario.getCorreo() == null || usuario.getContrasena() == null || usuario.getRol() == null) {
                return false;
            }
            String tipo = usuario.getRol().getNombre();

             // Si el rol es "Emprendedor", guarda también el mensaje asociado.
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
