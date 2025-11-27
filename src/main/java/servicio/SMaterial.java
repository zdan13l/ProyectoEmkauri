package servicio;

import modelo.Material;
import repositorio.IRMaterial;
import java.util.*;

public class SMaterial implements ISMaterial{
    private final IRMaterial repoM;

    // Constructor con inyección de dependencia del repositorio.
    public SMaterial(IRMaterial repoM) { this.repoM = repoM; }

    // Insertar nuevo material asociado a un curso.
    @Override
    public void insertar(Material material, int idCurso) { repoM.insertar(material, idCurso); }

    // Listar materiales por curso.
    @Override
    public List<Material> listarPorCurso(int idCurso) {
        List<Material> lista = repoM.listarPorCurso(idCurso);
        return (lista != null) ? lista : new ArrayList<>();
    }

    // Buscar material por ID.
    @Override
    public Material buscarPorId(int idMaterial) { return repoM.buscarPorId(idMaterial); }

    // Modificar material existente.
    @Override
    public void modificar(Material material) { repoM.actualizar(material); }

    // Eliminar material por ID.
    @Override
    public void eliminar(int idMaterial) { repoM.eliminar(idMaterial); }
}
