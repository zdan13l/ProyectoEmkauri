package servicio;

import modelo.Material;
import repositorio.IRMaterial;

public class SMaterial implements ISMaterial{
    private final IRMaterial repoM;

    // Constructor con inyección de dependencia del repositorio.
    public SMaterial(IRMaterial repoM) { this.repoM = repoM; }

    @Override
    public void insertar(Material material, int idCurso) throws Exception {

    }

    @Override
    public java.util.List<Material> listarPorCurso(int idCurso) throws Exception {
        return null;
    }

    @Override
    public Material buscarPorId(int idMaterial) throws Exception {
        return null;
    }

    @Override
    public void modificar(Material material) throws Exception {

    }

    @Override
    public void eliminar(int idMaterial) throws Exception {

    }

}
