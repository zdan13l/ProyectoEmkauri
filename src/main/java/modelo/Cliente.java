package modelo;

import java.util.List;

public class Cliente extends Usuario {
    private List<Compra> compras;

    public Cliente(int idUsuario, String nombre, String email, String contrasena) {
        super(idUsuario, nombre, email, contrasena);
    }

}
