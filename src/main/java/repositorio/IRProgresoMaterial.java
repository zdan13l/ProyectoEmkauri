package repositorio;

import modelo.ProgresoMaterial;
import java.util.List;

// Interfaz para operaciones CRUD en la entidad ProgresoMaterial.
public interface IRProgresoMaterial {
    boolean crear(ProgresoMaterial progreso);
    boolean actualizar(ProgresoMaterial progreso);
    ProgresoMaterial obtenerPorClienteYMaterial(int idCliente, int idMaterial);
    List<ProgresoMaterial> obtenerPorCliente(int idCliente);
    int contarVistosPorCurso(int idCliente, int idProducto);
}
