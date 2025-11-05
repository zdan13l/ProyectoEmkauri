package repositorio;

import modelo.Solicitud;
import java.sql.Connection;
import java.util.List;

// Interfaz para operaciones CRUD en la entidad Solicitud.
public interface IRSolicitud {
    void guardar(Solicitud solicitud);
    List<Solicitud> listarPendientesTipo(String tipo);
    boolean aprobar(int idSolicitud);
    boolean rechazar(int idSolicitud);
    Integer obtenerReclutador(Connection conexion);
}
