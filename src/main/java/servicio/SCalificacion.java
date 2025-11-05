package servicio;

import modelo.Calificacion;
import repositorio.IRCalificacion;

import java.util.List;

// Servicio que implementa la lógica de negocio para las calificaciones.
public class SCalificacion implements ISCalificacion{
    private final IRCalificacion repoC;

    // Constructor con inyección de dependencia del repositorio.
    public SCalificacion(IRCalificacion repoC) { this.repoC = repoC; }

    // Crea una nueva calificación.
    public boolean crearCalificacion(modelo.Calificacion calificacion) { return repoC.guardar(calificacion); }

    // Obtiene calificaciones por ID de producto.
    public List<Calificacion> listarPorProducto(int idProducto) { return repoC.obtenerPorProducto(idProducto); }

    // Obtiene calificaciones por ID de cliente.
    public List<modelo.Calificacion> listarPorCliente(int idCliente) { return repoC.obtenerPorCliente(idCliente); }
}
