package repositorio;

import modelo.Usuario;
import java.util.List;

public interface IRUsuario {
    Usuario autenticar(String correo, String contrasena);
    Usuario buscarPorCorreo(String correo);
}
