package servicio;

import modelo.Pago;
import java.util.List;

public interface ISPago {
    boolean crearPago(Pago pago);
    Pago obtenerPagoPorId(int idPago);
    List<Pago> obtenerTodos();
    boolean actualizarPago(Pago pago);
    boolean eliminarPago(int idPago);
}
