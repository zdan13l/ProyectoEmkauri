package repositorio;

import modelo.Compra;
import java.util.List;

// Interfaz para operaciones CRUD en la entidad Compra.
public interface IRCompra {
    boolean insertar(Compra compra);
    Compra obtenerPorId(int id);
    List<Compra> listar();
    boolean actualizar(Compra compra);
    boolean eliminar(int id);
}
