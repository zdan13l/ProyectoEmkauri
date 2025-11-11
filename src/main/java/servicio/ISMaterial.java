package servicio;

import modelo.Material;

import java.util.List;

public interface ISMaterial {
    void insertar(Material material, int idCurso) throws Exception;
    List<Material> listarPorCurso(int idCurso) throws Exception;
    Material buscarPorId(int idMaterial) throws Exception;
    void modificar(Material material) throws Exception;
    void eliminar(int idMaterial) throws Exception;
}
