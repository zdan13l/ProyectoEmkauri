package servicio;

import modelo.Solicitud;
import java.util.List;

public interface ISSolicitud {
    void crearSolicitud(Solicitud solicitud);
    List<Solicitud> listarSolicitudesPendientes(String tipo);
    boolean aprobarSolicitud(int idSolicitud);
    boolean rechazarSolicitud(int idSolicitud);
}
