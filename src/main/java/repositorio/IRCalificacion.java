package repositorio;

import modelo.Calificacion;

import java.util.List;

// Interfaz para operaciones CRUD en la entidad Calificacion.
public interface IRCalificacion {
    boolean guardar(Calificacion calificacion);
    List<Calificacion> obtenerPorProducto(int idProducto);
    List<Calificacion> obtenerPorCliente(int idCliente);
}
