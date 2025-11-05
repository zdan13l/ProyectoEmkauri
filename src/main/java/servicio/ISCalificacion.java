package servicio;

import modelo.Calificacion;
import java.util.List;

public interface ISCalificacion {
    boolean crearCalificacion(Calificacion calificacion);
    List<Calificacion> listarPorProducto(int idProducto);
    List<Calificacion> listarPorCliente(int idCliente);
}
