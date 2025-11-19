package servicio;

import modelo.Calificacion;
import repositorio.IRCalificacion;
import java.util.*;

// Servicio que implementa la lógica de negocio para las calificaciones.
public class SCalificacion implements ISCalificacion {
    private final IRCalificacion repoC;

    // Constructor con inyección de dependencia del repositorio.
    public SCalificacion(IRCalificacion repoC) {
        this.repoC = repoC;
    }

    // Crea una nueva calificación.
    public boolean crearCalificacion(Calificacion calificacion) {
        if (calificacion == null ||
                calificacion.getCliente() == null ||
                calificacion.getProducto() == null ||
                calificacion.getPuntaje() < 1 ||
                calificacion.getPuntaje() > 5) {
            return false;
        }
        return repoC.guardar(calificacion);
    }

    // Obtiene calificaciones por ID de producto.
    public List<Calificacion> listarPorProducto(int idProducto) {
        if (idProducto <= 0) {
            return Collections.emptyList();
        }
        return repoC.obtenerPorProducto(idProducto);
    }

    // Obtiene calificaciones por ID de cliente.
    public List<Calificacion> listarPorCliente(int idCliente) {
        if (idCliente <= 0) {
            return Collections.emptyList();
        }
        return repoC.obtenerPorCliente(idCliente);
    }
}
