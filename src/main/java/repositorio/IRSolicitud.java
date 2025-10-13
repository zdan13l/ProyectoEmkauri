package repositorio;

import modelo.Solicitud;
import java.util.List;

public interface IRSolicitud {
    int crearSolicitud(int idCliente, int idProducto, String mensaje);
    List<Solicitud> listarPorSolicitante(int idCliente);
    List<Solicitud> listarPendientes(); // útil para el moderador (otro CU)
}
