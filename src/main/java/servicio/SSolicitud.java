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
    public List<Solicitud> listarSolicitudesPendientes(String tipo) {
        return repoS.listarPendientesTipo(tipo);
    }

    @Override
    public boolean aprobarSolicitud(int idSolicitud) {
        return repoS.aprobar(idSolicitud);
    }

    @Override
    public boolean rechazarSolicitud(int idSolicitud) {
        return repoS.rechazar(idSolicitud);
    }
}

