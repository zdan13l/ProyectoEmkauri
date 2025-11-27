package repositorio;

import modelo.Material;
import java.util.List;

// Interfaz para operaciones CRUD en la entidad Material.
public interface IRMaterial {
    void insertar(Material material, int idCurso);
    List<Material> listarPorCurso(int idCurso);
    Material buscarPorId(int idMaterial);
    void actualizar (Material material);
    void eliminar (int idMaterial);
}
