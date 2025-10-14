package repositorio;

import modelo.Solicitud;

import java.sql.Connection;
import java.util.List;

public interface IRSolicitud {
    void guardar(Solicitud solicitud);
    List<Solicitud> listarPendientes();
    void aprobar(int idSolicitud);
    void rechazar(int idSolicitud);
    Integer obtenerReclutador(Connection conexion);
}
