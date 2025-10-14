package servicio;

import modelo.Solicitud;
import repositorio.IRSolicitud;
import java.util.List;

public class SSolicitud implements ISSolicitud {

    private final IRSolicitud repoS;

    public SSolicitud(IRSolicitud repoS) {
        this.repoS = repoS;
    }

    @Override
    public void crearSolicitud(Solicitud solicitud) {
        solicitud.setEstado("PENDIENTE");
        repoS.guardar(solicitud);
    }

    @Override
    public List<Solicitud> listarSolicitudesPendientes() {
        return repoS.listarPendientes();
    }

    @Override
    public void aprobarSolicitud(int idSolicitud) {
        repoS.aprobar(idSolicitud);
    }

    @Override
    public void rechazarSolicitud(int idSolicitud) {
        repoS.rechazar(idSolicitud);
    }
}

