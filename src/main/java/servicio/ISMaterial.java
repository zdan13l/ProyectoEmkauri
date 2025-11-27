package servicio;

import modelo.Material;
import java.util.List;

// Interfaz del servicio de gestión de materiales.
public interface ISMaterial {
    void insertar(Material material, int idCurso);
    List<Material> listarPorCurso(int idCurso);
    Material buscarPorId(int idMaterial);
    void modificar(Material material);
    void eliminar(int idMaterial);
}
