package servicio;

import modelo.Solicitud;
import repositorio.IRSolicitud;

import java.util.List;

public class SSolicitud implements ISSolicitud {

    private final IRSolicitud repo;

    public SSolicitud(IRSolicitud repo) { this.repo = repo; }

    @Override
    public int solicitarServicio(int idCliente, int idServicio, String mensajeLibre) {
        // Regla mínima: mensaje opcional -> default
        String msg = (mensajeLibre == null || mensajeLibre.isBlank())
                ? "Solicitud de servicio generada desde la UI."
                : mensajeLibre.trim();
        return repo.crearSolicitud(idCliente, idServicio, msg);
    }

    @Override
    public List<Solicitud> misSolicitudes(int idCliente) {
        return repo.listarPorSolicitante(idCliente);
    }
}
