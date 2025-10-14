package repositorio;

import modelo.Usuario;

// Interfaz para operaciones CRUD en la entidad Usuario.
public interface IRUsuario {
    Usuario autenticar(String correo, String contrasena);
    Usuario buscarPorCorreo(String correo);
    boolean insertarCliente(Usuario usuario);
    boolean insertarEmprendedor(Usuario usuario, String mensajeSolicitud);
}

