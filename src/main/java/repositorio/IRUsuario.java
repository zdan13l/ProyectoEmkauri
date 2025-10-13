package repositorio;

import modelo.Usuario;

public interface IRUsuario {
    Usuario autenticar(String correo, String contrasena);
    Usuario buscarPorCorreo(String correo);
}
