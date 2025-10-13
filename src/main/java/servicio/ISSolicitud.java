package servicio;

import modelo.Solicitud;
import java.util.List;

public interface ISSolicitud {
    int solicitarServicio(int idCliente, int idServicio, String mensajeLibre);
    List<Solicitud> misSolicitudes(int idCliente);
}
