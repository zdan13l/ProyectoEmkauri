package servicio;

import modelo.Solicitud;
import java.util.List;

// Interfaz para la gestión de solicitudes.
public interface ISSolicitud {
    void crearSolicitud(Solicitud solicitud);
    List<Solicitud> listarSolicitudesPendientes(String tipo);
    List<Solicitud> listarSolicitudes(String tipo);
    boolean aprobarSolicitud(int idSolicitud);
    boolean rechazarSolicitud(int idSolicitud);
    void marcarPendiente(int idSolicitud);
}
