package servicio;

import modelo.Curso;
import modelo.Material;

import java.util.List;

public interface ISCurso {
    // Cursos del emprendedor
    List<Curso> listarCursosDeEmprendedor(int idEmprendedor);

    // Materiales del curso
    List<Material> listarMateriales(int idCurso);
    Material crearMaterial(int idCurso, Material m);
    boolean actualizarMaterial(Material m);
    boolean eliminarMaterial(int idMaterial);
}
