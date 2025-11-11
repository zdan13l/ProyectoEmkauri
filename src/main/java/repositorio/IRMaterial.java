package repositorio;

import modelo.Material;

import java.util.List;

public interface IRMaterial {
    void insertar(Material material, int idCurso) throws java.sql.SQLException;
    List<Material> listarPorCurso(int idCurso) throws java.sql.SQLException;
    Material buscarPorId(int idMaterial) throws java.sql.SQLException;
    void actualizar (Material material) throws java.sql.SQLException;
    void eliminar (int idMaterial) throws java.sql.SQLException;
}
