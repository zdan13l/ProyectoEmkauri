package servicio;

import modelo.Material;
import modelo.ProgresoMaterial;
import modelo.Usuario;
import repositorio.IRProgresoMaterial;

import java.util.List;

public class SProgresoMaterial implements ISProgresoMaterial {

    private final IRProgresoMaterial repo;

    public SProgresoMaterial(IRProgresoMaterial repo) {
        this.repo = repo;
    }

    @Override
    public ProgresoMaterial obtener(int idCliente, int idMaterial) {
        return repo.obtenerPorClienteYMaterial(idCliente, idMaterial);
    }

    @Override
    public ProgresoMaterial crear(Usuario cliente, Material material) {
        ProgresoMaterial p = new ProgresoMaterial();
        p.setIdCliente(cliente);
        p.setIdMaterial(material);
        p.setVisto(false);

        repo.crear(p);
        return p;
    }

    @Override
    public boolean actualizar(ProgresoMaterial progreso) {
        return repo.actualizar(progreso);
    }

    @Override
    public boolean marcarVisto(int idCliente, int idMaterial) {
        ProgresoMaterial progreso = obtener(idCliente, idMaterial);
        if (progreso == null) return false;

        progreso.setVisto(true);
        return repo.actualizar(progreso);
    }

    @Override
    public boolean marcarNoVisto(int idCliente, int idMaterial) {
        ProgresoMaterial progreso = obtener(idCliente, idMaterial);
        if (progreso == null) return false;

        progreso.setVisto(false);
        return repo.actualizar(progreso);
    }

    @Override
    public int contarVistos(int idCliente, int idProducto) {
        return repo.contarVistosPorCurso(idCliente, idProducto);
    }

    @Override
    public List<ProgresoMaterial> listarPorCliente(int idCliente) {
        return repo.obtenerPorCliente(idCliente);
    }
}
