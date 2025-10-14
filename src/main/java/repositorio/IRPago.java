package repositorio;

import modelo.Pago;
import java.util.List;

// Interfaz para operaciones CRUD en la entidad Pago.
public interface IRPago {
    boolean crearPago(Pago pago);
    Pago obtenerPagoPorId(int idPago);
    List<Pago> obtenerTodos();
    boolean actualizarPago(Pago pago);
    boolean eliminarPago(int idPago);
}
