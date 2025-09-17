package modelo;

import java.util.List;

public class Cliente extends Usuario {
    private List<Compra> compras;

    public Cliente(int idUsuario, String nombre, String email, String contrasena, List<Compra> compras) {
        super(idUsuario, nombre, email, contrasena);
        this.compras = compras;
    }

    public List<Compra> getCompras() {
        return compras;
    }

    @Override
    public String toString() {
        return "Cliente";
    }
}
