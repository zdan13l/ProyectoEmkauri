package servicio;

import modelo.Solicitud;
import repositorio.IRSolicitud;
import java.util.List;

// Servicio de negocio para la gestión de solicitudes.
public class SSolicitud implements ISSolicitud {
    private final IRSolicitud repoS;

    // Constructor con inyección de dependencia.
    public SSolicitud(IRSolicitud repoS) {
        this.repoS = repoS;
    }

    // Crea una nueva solicitud.
    public void crearSolicitud(Solicitud solicitud) {
        solicitud.setEstado("PENDIENTE");
        repoS.guardar(solicitud);
    }

    // Lista todas las solicitudes pendientes de un tipo específico (ej. "EMPRENDEDOR").
    public List<Solicitud> listarSolicitudesPendientes(String tipo) {
        return repoS.listarPendientesTipo(tipo);
    }

    // Lista todas las solicitudes de un tipo específico.
    public List<Solicitud> listarSolicitudes(String tipo) {
        return repoS.listarSolicitudes(tipo);
    }

    // Obtiene una solicitud por su ID.
    public boolean aprobarSolicitud(int idSolicitud) {
        return repoS.aprobar(idSolicitud);
    }

    // Rechaza una solicitud por su ID.
    public boolean rechazarSolicitud(int idSolicitud) {
        return repoS.rechazar(idSolicitud);
    }

    // Marca una solicitud como pendiente nuevamente.
    public void marcarPendiente(int idSolicitud) {
        repoS.marcarPendiente(idSolicitud);
    }
}

