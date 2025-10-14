package servicio;

import modelo.Solicitud;
import java.util.List;

public interface ISSolicitud {
    void crearSolicitud(Solicitud solicitud);
    List<Solicitud> listarSolicitudesPendientes();
    void aprobarSolicitud(int idSolicitud);
    void rechazarSolicitud(int idSolicitud);
}
